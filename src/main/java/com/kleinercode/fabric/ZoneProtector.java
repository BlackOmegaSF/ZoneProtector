package com.kleinercode.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.mob.EndermanEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoneProtector implements DedicatedServerModInitializer {

    public static final String MOD_ID = "zone-protector";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeServer() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ZoneCommand.register(dispatcher));

    }
}
