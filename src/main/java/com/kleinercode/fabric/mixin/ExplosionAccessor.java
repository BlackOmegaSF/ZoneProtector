package com.kleinercode.fabric.mixin;

import com.jcraft.jorbis.Block;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(Explosion.class)
public interface ExplosionAccessor {
    @Accessor
    World getWorld();

    @Accessor
    ObjectArrayList<BlockPos> getAffectedBlocks();
}
