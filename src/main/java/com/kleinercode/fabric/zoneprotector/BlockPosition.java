package com.kleinercode.fabric.zoneprotector;

import net.minecraft.util.math.BlockPos;

public class BlockPosition {

    int x;
    int y;
    int z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPosition(double x, double y, double z) {
        this.x = (int) x;
        this.y = (int) y;
        this.z = (int) z;
    }

    public boolean equals(BlockPosition position) {
        if (position.x != x) return false;
        if (position.y != y) return false;
        if (position.z != z) return false;
        return true;
    }

    public static BlockPosition fromBlockPos(BlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

}
