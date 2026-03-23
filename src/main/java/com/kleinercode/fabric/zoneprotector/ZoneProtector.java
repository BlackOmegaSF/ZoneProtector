package com.kleinercode.fabric.zoneprotector;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoneProtector implements DedicatedServerModInitializer {

    public static final String MOD_ID = "zone-protector";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // TODO Add protections for:
    //  Fire destruction
    //  Fire spread
    //  Endermen griefing
    //  Herbivores eating grass

    @Override
    public void onInitializeServer() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ZoneCommand.register(dispatcher));

        UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> {
            if (player.isSpectator()) {
                return InteractionResult.PASS;
            }

            ItemStack itemStack;
            switch (hand) {
                case InteractionHand.MAIN_HAND -> itemStack = player.getMainHandItem();
                case InteractionHand.OFF_HAND -> itemStack = player.getOffhandItem();
                default -> {
                    // Weird stuff is happening, just get out of this
                    return InteractionResult.PASS;
                }
            }

            if (itemStack.isEmpty()) return InteractionResult.PASS;

            if (Constants.bannedSpawnEggs.contains(itemStack.getItem())) {
                // Item is a banned spawn egg
                ZonePersistentState state = ZonePersistentState.getServerState(world.getServer());
                BlockPos spawnPos = hitResult.getBlockPos().relative(hitResult.getDirection());
                for (Zone zone : state.getZones()) {
                    if (zone.containsPosition(world.dimension().identifier(), spawnPos)) {
                        // Monster is being spawned in zone
                        player.displayClientMessage(Component.literal("This area is protected! You cannot spawn monsters here."), false);
                        return InteractionResult.FAIL;
                    }
                }
            }

            return InteractionResult.PASS;
        }));

    }
}
