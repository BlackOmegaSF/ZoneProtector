package com.kleinercode.fabric.zoneprotector;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoneProtector implements DedicatedServerModInitializer {

    public static final String MOD_ID = "zone-protector";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeServer() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ZoneCommand.register(dispatcher));

        UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> {
            if (player.isSpectator()) {
                return ActionResult.PASS;
            }

            ItemStack itemStack;
            switch (hand) {
                case Hand.MAIN_HAND -> {
                    itemStack = player.getMainHandStack();
                }
                case Hand.OFF_HAND -> {
                    itemStack = player.getOffHandStack();
                }
                default -> {
                    // Weird stuff is happening, just get out of this
                    return ActionResult.PASS;
                }
            }

            if (itemStack == null) return ActionResult.PASS;

            if (Constants.bannedSpawnEggs.contains(itemStack.getItem())) {
                // Item is a banned spawn egg
                StateSaverAndLoader state = StateSaverAndLoader.getServerState(world.getServer());
                BlockPos spawnPos = hitResult.getBlockPos().offset(hitResult.getSide());
                for (Zone zone : state.zones) {
                    if (zone.containsPosition(world.getRegistryKey().getValue(), spawnPos)) {
                        // Monster is being spawned in zone
                        player.sendMessage(Text.literal("This area is protected! You cannot spawn monsters here."));
                        return ActionResult.FAIL;
                    }
                }
            }

            return ActionResult.PASS;
        }));

    }
}
