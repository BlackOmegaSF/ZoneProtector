package com.kleinercode.plugins.zoneprotector;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Zone implements ConfigurationSerializable {

    UUID worldId;
    BlockCoordinate lowerLocation;
    BlockCoordinate upperLocation;

    public Zone(Location lowerLocation, Location upperLocation) {
        if (lowerLocation.getWorld().getUID() != upperLocation.getWorld().getUID()) {
            throw new IllegalArgumentException("Locations must be in the same world.");
        }

        if (
            lowerLocation.getBlockX() > upperLocation.getBlockX() ||
            lowerLocation.getBlockY() > upperLocation.getBlockY() ||
            lowerLocation.getBlockZ() > upperLocation.getBlockZ()
        ) {
            throw new IllegalArgumentException("Lower location coordinates must be lower than or equal to upper location coordinates.");
        }

        this.worldId = lowerLocation.getWorld().getUID();
        this.lowerLocation = BlockCoordinate.fromLocation(lowerLocation);
        this.upperLocation = BlockCoordinate.fromLocation(upperLocation);

    }

    public Zone(UUID worldId, BlockCoordinate lowerLocation, BlockCoordinate upperLocation) {
        this.worldId = worldId;
        this.lowerLocation = lowerLocation;
        this.upperLocation = upperLocation;
    }

    public boolean checkIfWithin(Location location) {
        if (location.getWorld().getUID() != worldId) {
            return false;
        }

        // Check x
        if (location.getBlockX() < lowerLocation.x || location.getBlockX() > upperLocation.x) {
            return false;
        }

        // Check z
        if (location.getBlockZ() < lowerLocation.z || location.getBlockZ() > upperLocation.z) {
            return false;
        }

        // Check y
        if (location.getBlockY() < lowerLocation.y || location.getBlockY() > upperLocation.y) {
            return false;
        }

        // It's within the box
        return true;

    }

    public String prettyPrint() {
        return worldId + " [" + lowerLocation.x + ", " + lowerLocation.y + ", " + lowerLocation.z +
                "] to [" + upperLocation.x + ", " + upperLocation.y + ", " + upperLocation.z + "]";
    }

    public String prettyPrint(Server server) {
        World world = server.getWorld(worldId);
        String worldName;
        if (world == null) {
            worldName = "[UNKNOWN WORLD]";
        } else {
            worldName = world.getName() + ":" + world.getEnvironment();
        }
        return worldName + " [" + lowerLocation.x + ", " + lowerLocation.y + ", " + lowerLocation.z +
                "] to [" + upperLocation.x + ", " + upperLocation.y + ", " + upperLocation.z + "]";
    }

    public boolean equals(Zone zone) {
        if (!zone.worldId.equals(worldId)) return false;
        if (!(zone.lowerLocation.equals(lowerLocation))) return false;
        if (!(zone.upperLocation.equals(upperLocation))) return false;
        return true;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("worldId", worldId.toString());
        data.put("lower.x", lowerLocation.x);
        data.put("lower.y", lowerLocation.y);
        data.put("lower.z", lowerLocation.z);
        data.put("upper.x", upperLocation.x);
        data.put("upper.y", upperLocation.y);
        data.put("upper.z", upperLocation.z);

        return data;

    }

    public static Zone deserialize(Map<String, Object> args) {
        return new Zone(
                UUID.fromString((String) args.get("worldId")),
                new BlockCoordinate(
                        (int) args.get("lower.x"),
                        (int) args.get("lower.y"),
                        (int) args.get("lower.z")
                ),
                new BlockCoordinate(
                        (int) args.get("upper.x"),
                        (int) args.get("upper.y"),
                        (int) args.get("upper.z")
                )

        );
    }
}
