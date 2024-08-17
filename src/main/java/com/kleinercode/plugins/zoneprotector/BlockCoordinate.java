package com.kleinercode.plugins.zoneprotector;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BlockCoordinate implements ConfigurationSerializable {

    int x;
    int y;
    int z;

    public BlockCoordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockCoordinate fromLocation(Location location) {
        return new BlockCoordinate(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    public boolean equals(BlockCoordinate coordinate) {
        if (coordinate.x != x) return false;
        if (coordinate.y != y) return false;
        if (coordinate.z != z) return false;
        return true;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("x", this.x);
        data.put("y", this.y);
        data.put("z", this.z);

        return data;
    }

    public static BlockCoordinate deserialize(Map<String, Object> args) {
        return new BlockCoordinate(
                (int) args.get("x"),
                (int) args.get("y"),
                (int) args.get("z")
        );
    }
}
