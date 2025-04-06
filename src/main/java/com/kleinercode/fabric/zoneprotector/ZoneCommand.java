package com.kleinercode.fabric.zoneprotector;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.*;

public final class ZoneCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("zone")
                .requires(source -> source.hasPermissionLevel(0)) // Must be op level 1
                .then(literal(Constants.CommandMode.LIST.toString())
                        .executes(context -> {
                            ZonePersistentState serverState = ZonePersistentState.getServerState(context.getSource().getServer());
                            if (serverState.getZones().isEmpty()) {
                                context.getSource().sendFeedback(() -> Text.literal("No zones are under protection."), false);
                                return 1;
                            }

                            StringBuilder builder = new StringBuilder();
                            builder.append("Protecting ").append(serverState.getZones().size()).append(" zones:\n");
                            for (Zone zone : serverState.getZones()) {
                                builder.append(zone.prettyPrint()).append("\n");
                            }
                            context.getSource().sendFeedback(() -> Text.literal(builder.toString()), false);
                            return 1;
                        }))
                .then(literal(Constants.CommandMode.PROTECT.toString())
                        .then(argument("pos1", BlockPosArgumentType.blockPos())
                        .then(argument("pos2", BlockPosArgumentType.blockPos())
                                .executes(context -> {

                                    // Now we have all the arguments needed
                                    final BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
                                    final BlockPos pos2 = BlockPosArgumentType.getBlockPos(context, "pos2");
                                    final World world = context.getSource().getWorld();

                                    BlockPosition position1 = BlockPosition.fromBlockPos(pos1);
                                    BlockPosition position2 = BlockPosition.fromBlockPos(pos2);

                                    Zone newZone = new Zone(world.getRegistryKey().getValue(), position1, position2);

                                    ZonePersistentState serverState = ZonePersistentState.getServerState(context.getSource().getServer());
                                    if (serverState.getZones().contains(newZone)) {
                                        context.getSource().sendFeedback(() -> Text.literal("That zone is already under protection!"), false);
                                        return -1;
                                    }

                                    // Now add the zone
                                    serverState.getZones().add(newZone);
                                    context.getSource().sendFeedback(() -> Text.literal("Began protecting zone " + newZone.prettyPrint()), true);
                                    return 1;
                                }))))
                .then(literal(Constants.CommandMode.UNPROTECT.toString())
                        .then(argument("pos1", BlockPosArgumentType.blockPos())
                        .then(argument("pos2", BlockPosArgumentType.blockPos())
                            .executes(context -> {

                                // Now we have all the arguments needed
                                final BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
                                final BlockPos pos2 = BlockPosArgumentType.getBlockPos(context, "pos2");
                                final World world = context.getSource().getWorld();

                                BlockPosition position1 = BlockPosition.fromBlockPos(pos1);
                                BlockPosition position2 = BlockPosition.fromBlockPos(pos2);

                                Zone newZone = new Zone(world.getRegistryKey().getValue(), position1, position2);

                                ZonePersistentState serverState = ZonePersistentState.getServerState(context.getSource().getServer());
                                for (Zone zone : serverState.getZones()) {
                                    if (zone.equals(newZone)) {
                                        serverState.getZones().remove(zone);
                                        context.getSource().sendFeedback(() -> Text.literal("Stopped protecting zone " + newZone.prettyPrint()), true);
                                        return 1;
                                    }
                                }
                                context.getSource().sendFeedback(() -> Text.literal("That zone isn't under protection!"), false);
                                return -1;

                            }))))
        );
    }

}
