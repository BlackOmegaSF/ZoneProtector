package com.kleinercode.fabric.zoneprotector;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.*;

public class ZonePersistentState extends PersistentState {

    private static final String PERSISTENT_STATE_KEY = "zones";

    public static final PersistentStateType<ZonePersistentState> STATE_TYPE = new PersistentStateType<>(
            PERSISTENT_STATE_KEY,
            (ctx) -> new ZonePersistentState(new ArrayList<>()),
            (ctx) -> NbtCompound.CODEC.xmap(
                    ZonePersistentState::readNbt,
                    state -> state.writeNbt(new NbtCompound())
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

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList zonesNbt = new NbtList();
        zones.forEach((zone) -> {
            NbtCompound zoneNbt = new NbtCompound();
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

    public static ZonePersistentState readNbt(NbtCompound tag) {
        ZonePersistentState state = new ZonePersistentState(new ArrayList<>());
        try {
            NbtList zonesList = tag.getList("zones").orElseThrow();
            zonesList.forEach((item) -> {
                try {
                    NbtCompound zoneNbt = (NbtCompound) item;
                    Zone zone = new Zone(
                            Identifier.of(zoneNbt.getString("dimension").orElseThrow()),
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
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        ZonePersistentState state = persistentStateManager.getOrCreate(STATE_TYPE);

        state.markDirty();

        return state;
    }

}
