package com.kleinercode.fabric;

import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Position;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class StateSaverAndLoader extends PersistentState {

    public List<Zone> zones = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList zonesNbt = new NbtList();
        zones.forEach((zone) -> {
            NbtCompound zoneNbt = new NbtCompound();
            zoneNbt.putInt("x1", zone.x1);
            zoneNbt.putInt("y1", zone.y1);
            zoneNbt.putInt("z1", zone.z1);
            zoneNbt.putInt("x2", zone.x2);
            zoneNbt.putInt("y2", zone.y2);
            zoneNbt.putInt("z2", zone.z2);
            zoneNbt.putString("dimension", zone.dimension.toString());
            zonesNbt.add(zoneNbt);
        });
        nbt.put("zones", zonesNbt);

        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        NbtList zonesList = tag.getList("zones", NbtElement.COMPOUND_TYPE);
        zonesList.forEach((item) -> {
            NbtCompound zoneNbt = (NbtCompound) item;
            Zone zone = new Zone(
                    Identifier.of(zoneNbt.getString("dimension")),
                    new BlockPosition(
                            zoneNbt.getInt("x1"),
                            zoneNbt.getInt("y1"),
                            zoneNbt.getInt("z1")
                    ),
                    new BlockPosition(
                            zoneNbt.getInt("x2"),
                            zoneNbt.getInt("y2"),
                            zoneNbt.getInt("z2")
                    )
            );
            state.zones.add(zone);
        });

        return state;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new,
            StateSaverAndLoader::createFromNbt,
            null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, ZoneProtector.MOD_ID);

        state.markDirty();

        return state;
    }

}
