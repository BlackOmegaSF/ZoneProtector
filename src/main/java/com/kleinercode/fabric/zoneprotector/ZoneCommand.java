package com.kleinercode.fabric.zoneprotector;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

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
                        })
                )
                .then(literal(Constants.CommandMode.PROTECT.toString())
                        .then(argument("pos1", BlockPosArgument.blockPos())
                        .then(argument("pos2", BlockPosArgument.blockPos())
                                .executes(context -> commonCreateZone(context, "Unnamed Zone"))
                                .then(argument("zone_name", StringArgumentType.string())
                                    .executes(context -> commonCreateZone(context, StringArgumentType.getString(context, "zone_name")))
                                )
                        ))
                )
                .then(literal(Constants.CommandMode.UNPROTECT.toString())
                        .then(literal("by_name")
                                .then(argument("zone_name", StringArgumentType.string())
                                        .executes( context -> {
                                            List<Zone> zones = ZonePersistentState.getServerState(context.getSource().getServer()).getZones();
                                            List<String> zoneNames = new java.util.ArrayList<>(Collections.emptyList());
                                            String requestedName = StringArgumentType.getString(context, "zone_name");

                                            for (Zone zone : zones) {
                                                zoneNames.add(zone.zoneName);
                                            }

                                            int occurrences = Collections.frequency(zoneNames, requestedName);
                                            if (occurrences < 1) {
                                                // None found
                                                context.getSource().sendSuccess(() -> Component.literal("No zone under protection named " + requestedName), false);
                                                return -1;
                                            }
                                            if (occurrences > 1) {
                                                // Multiple found
                                                context.getSource().sendSuccess(() -> Component.literal("Multiple zones named " + requestedName + "! Define by coordinates instead"), false);
                                                return -1;
                                            }

                                            // Only one found, delete this one
                                            Zone toDelete = null;
                                            for (Zone zone : zones) {
                                                if (zone.zoneName.equals(requestedName)) {
                                                    toDelete = zone;
                                                }
                                            }

                                            // Null check just in case
                                            if (toDelete == null) {
                                                context.getSource().sendSuccess(() -> Component.literal("Internal error removing zone " + requestedName), false);
                                                return -1;
                                            }

                                            ZonePersistentState.getServerState(context.getSource().getServer()).getZones().remove(toDelete);
                                            String prettyPrinted = toDelete.prettyPrint();
                                            context.getSource().sendSuccess(() -> Component.literal("Stopped protecting zone " + prettyPrinted), true);
                                            return 1;

                                            }
                                        )
                                )
                        )
                        .then(literal("by_coords")
                            .then(argument("pos1", BlockPosArgument.blockPos())
                            .then(argument("pos2", BlockPosArgument.blockPos())
                                .executes(context -> {

                                    // Now we have all the arguments needed
                                    final BlockPos pos1 = BlockPosArgument.getBlockPos(context, "pos1");
                                    final BlockPos pos2 = BlockPosArgument.getBlockPos(context, "pos2");
                                    final Level world = context.getSource().getLevel();

                                    BlockPosition position1 = BlockPosition.fromBlockPos(pos1);
                                    BlockPosition position2 = BlockPosition.fromBlockPos(pos2);

                                    Zone newZone = new Zone(world.dimension().identifier(), position1, position2, "Unnamed Zone");

                                    ZonePersistentState serverState = ZonePersistentState.getServerState(context.getSource().getServer());
                                    for (Zone zone : serverState.getZones()) {
                                        if (zone.equals(newZone)) {
                                            String prettyPrinted = zone.prettyPrint();
                                            serverState.getZones().remove(zone);
                                            context.getSource().sendSuccess(() -> Component.literal("Stopped protecting zone " + prettyPrinted), true);
                                            return 1;
                                        }
                                    }
                                    context.getSource().sendSuccess(() -> Component.literal("That zone isn't under protection!"), false);
                                    return -1;

                                })
                            ))
                        )
                )
        );
    }

    private static int commonCreateZone(CommandContext<CommandSourceStack> context, String zoneName) {
        final BlockPos pos1 = BlockPosArgument.getBlockPos(context, "pos1");
        final BlockPos pos2 = BlockPosArgument.getBlockPos(context, "pos2");
        final Level world = context.getSource().getLevel();

        BlockPosition position1 = BlockPosition.fromBlockPos(pos1);
        BlockPosition position2 = BlockPosition.fromBlockPos(pos2);

        Zone newZone = new Zone(world.dimension().identifier(), position1, position2, zoneName);

        ZonePersistentState serverState = ZonePersistentState.getServerState(context.getSource().getServer());
        if (serverState.getZones().contains(newZone)) {
            context.getSource().sendSuccess(() -> Component.literal("That zone is already under protection!"), false);
            return -1;
        }

        // Now add the zone
        serverState.getZones().add(newZone);
        context.getSource().sendSuccess(() -> Component.literal("Began protecting zone " + newZone.prettyPrint()), true);
        return 1;
    }

}
