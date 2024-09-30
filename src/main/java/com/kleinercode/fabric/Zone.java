package com.kleinercode.fabric;



import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;

public class Zone {

    Identifier worldId;
    int x1; // Lower x
    int y1; // Lower y
    int z1; // Lower z
    int x2; // Upper x
    int y2; // Upper y
    int z2; // Upper z

    public Zone(Identifier worldIdentifier, BlockPosition position1, BlockPosition position2) {
        worldId = worldIdentifier;
        int p1x = position1.x;
        int p1y = position1.y;
        int p1z = position1.z;
        int p2x = position2.x;
        int p2y = position2.y;
        int p2z = position2.z;

        if (p1x > p2x) {
            x1 = p2x;
            x2 = p1x;
        } else {
            x1 = p1x;
            x2 = p2x;
        }

        if (p1y > p2y) {
            y1 = p2y;
            y2 = p1y;
        } else {
            y1 = p1y;
            y2 = p2y;
        }

        if (p1z > p2z) {
            z1 = p2z;
            z2 = p1z;
        } else {
            z1 = p1z;
            z2 = p2z;
        }
    }

    public boolean containsPosition(Identifier worldIdentifier, BlockPos position) {
        // Check dimension
        if (!worldId.equals(worldIdentifier)) {
            return false;
        }

        // Check x
        if (position.getX() < x1 || (int) position.getX() > x2) {
            return false;
        }

        // Check z
        if (position.getZ() < z1 || (int) position.getZ() > z2) {
            return false;
        }

        // Check y
        if (position.getY() < y1 || (int) position.getY() > y2) {
            return false;
        }

        // Position is inside this zone
        return true;
    }

    public String prettyPrint() {
        return worldId.toTranslationKey() + " [" + x1 + ", " + y1 + ", " + z1 + "] to [" + x2 + ", " + y2 + ", " + z2 + "]";
    }

    public boolean equals(Zone zone) {
        if (!(x1 == zone.x1)) return false;
        if (!(y1 == zone.y1)) return false;
        if (!(z1 == zone.z1)) return false;
        if (!(x2 == zone.x2)) return false;
        if (!(y2 == zone.y2)) return false;
        if (!(z2 == zone.z2)) return false;
        // They're the same
        return true;
    }

}
