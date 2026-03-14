package com.kleinercode.fabric.zoneprotector;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.level.Level;

import static net.minecraft.commands.Commands.*;

public final class ZoneCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("zone")
                .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.MODERATORS))) // Must be op level 1
                .then(literal(Constants.CommandMode.LIST.toString())
                        .executes(context -> {
                            ZonePersistentState serverState = ZonePersistentState.getServerState(context.getSource().getServer());
                            if (serverState.getZones().isEmpty()) {
                                context.getSource().sendSuccess(() -> Component.literal("No zones are under protection."), false);
                                return 1;
                            }

                            StringBuilder builder = new StringBuilder();
                            builder.append("Protecting ").append(serverState.getZones().size()).append(" zones:\n");
                            for (Zone zone : serverState.getZones()) {
                                builder.append(zone.prettyPrint()).append("\n");
                            }
                            context.getSource().sendSuccess(() -> Component.literal(builder.toString()), false);
                            return 1;
                        }))
                .then(literal(Constants.CommandMode.PROTECT.toString())
                        .then(argument("pos1", BlockPosArgument.blockPos())
                        .then(argument("pos2", BlockPosArgument.blockPos())
                                .executes(context -> {

                                    // Now we have all the arguments needed
                                    final BlockPos pos1 = BlockPosArgument.getBlockPos(context, "pos1");
                                    final BlockPos pos2 = BlockPosArgument.getBlockPos(context, "pos2");
                                    final Level world = context.getSource().getLevel();

                                    BlockPosition position1 = BlockPosition.fromBlockPos(pos1);
                                    BlockPosition position2 = BlockPosition.fromBlockPos(pos2);

                                    Zone newZone = new Zone(world.dimension().identifier(), position1, position2);

                                    ZonePersistentState serverState = ZonePersistentState.getServerState(context.getSource().getServer());
                                    if (serverState.getZones().contains(newZone)) {
                                        context.getSource().sendSuccess(() -> Component.literal("That zone is already under protection!"), false);
                                        return -1;
                                    }

                                    // Now add the zone
                                    serverState.getZones().add(newZone);
                                    context.getSource().sendSuccess(() -> Component.literal("Began protecting zone " + newZone.prettyPrint()), true);
                                    return 1;
                                }))))
                .then(literal(Constants.CommandMode.UNPROTECT.toString())
                        .then(argument("pos1", BlockPosArgument.blockPos())
                        .then(argument("pos2", BlockPosArgument.blockPos())
                            .executes(context -> {

                                // Now we have all the arguments needed
                                final BlockPos pos1 = BlockPosArgument.getBlockPos(context, "pos1");
                                final BlockPos pos2 = BlockPosArgument.getBlockPos(context, "pos2");
                                final Level world = context.getSource().getLevel();

                                BlockPosition position1 = BlockPosition.fromBlockPos(pos1);
                                BlockPosition position2 = BlockPosition.fromBlockPos(pos2);

                                Zone newZone = new Zone(world.dimension().identifier(), position1, position2);

                                ZonePersistentState serverState = ZonePersistentState.getServerState(context.getSource().getServer());
                                for (Zone zone : serverState.getZones()) {
                                    if (zone.equals(newZone)) {
                                        serverState.getZones().remove(zone);
                                        context.getSource().sendSuccess(() -> Component.literal("Stopped protecting zone " + newZone.prettyPrint()), true);
                                        return 1;
                                    }
                                }
                                context.getSource().sendSuccess(() -> Component.literal("That zone isn't under protection!"), false);
                                return -1;

                            }))))
        );
    }

}
