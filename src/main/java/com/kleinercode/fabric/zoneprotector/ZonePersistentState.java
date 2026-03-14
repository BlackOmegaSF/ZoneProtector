package com.kleinercode.fabric.zoneprotector;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;
import java.util.*;

public class ZonePersistentState extends SavedData {

    private static final String PERSISTENT_STATE_KEY = "zones";

    public static final SavedDataType<ZonePersistentState> STATE_TYPE = new SavedDataType<>(
            PERSISTENT_STATE_KEY,
            () -> new ZonePersistentState(new ArrayList<>()),
            CompoundTag.CODEC.xmap(
                    ZonePersistentState::readNbt,
                    state -> state.writeNbt(new CompoundTag())
            ),
            DataFixTypes.LEVEL
    );

    private final List<Zone> zones;

    public ZonePersistentState(List<Zone> zones) {
        super();
        this.zones = new ArrayList<>();
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public CompoundTag writeNbt(CompoundTag nbt) {
        ListTag zonesNbt = new ListTag();
        zones.forEach((zone) -> {
            CompoundTag zoneNbt = new CompoundTag();
            zoneNbt.putInt("x1", zone.x1);
            zoneNbt.putInt("y1", zone.y1);
            zoneNbt.putInt("z1", zone.z1);
            zoneNbt.putInt("x2", zone.x2);
            zoneNbt.putInt("y2", zone.y2);
            zoneNbt.putInt("z2", zone.z2);
            zoneNbt.putString("dimension", zone.worldId.toString());
            zonesNbt.add(zoneNbt);
        });
        nbt.put("zones", zonesNbt);

        return nbt;
    }

    public static ZonePersistentState readNbt(CompoundTag tag) {
        ZonePersistentState state = new ZonePersistentState(new ArrayList<>());
        try {
            ListTag zonesList = tag.getList("zones").orElseThrow();
            zonesList.forEach((item) -> {
                try {
                    CompoundTag zoneNbt = (CompoundTag) item;
                    Zone zone = new Zone(
                            Identifier.parse(zoneNbt.getString("dimension").orElseThrow()),
                            new BlockPosition(
                                    zoneNbt.getInt("x1").orElseThrow(),
                                    zoneNbt.getInt("y1").orElseThrow(),
                                    zoneNbt.getInt("z1").orElseThrow()
                            ),
                            new BlockPosition(
                                    zoneNbt.getInt("x2").orElseThrow(),
                                    zoneNbt.getInt("y2").orElseThrow(),
                                    zoneNbt.getInt("z2").orElseThrow()
                            )
                    );
                    state.zones.add(zone);
                } catch (NoSuchElementException e) {
                    ZoneProtector.LOGGER.warn("Error loading a protected zone, verify zones are still protected!");
                }
            });

        } catch (NoSuchElementException e) {
            ZoneProtector.LOGGER.warn("Protected zones data missing or corrupted");
        }

        return state;
    }

    public static ZonePersistentState getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = Objects.requireNonNull(server.getLevel(Level.OVERWORLD)).getDataStorage();

        ZonePersistentState state = persistentStateManager.computeIfAbsent(STATE_TYPE);

        state.setDirty();

        return state;
    }

}
