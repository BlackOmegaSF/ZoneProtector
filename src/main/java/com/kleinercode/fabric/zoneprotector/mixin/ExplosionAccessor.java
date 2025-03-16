package com.kleinercode.fabric.zoneprotector.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExplosionImpl.class)
public interface ExplosionAccessor {
    @Accessor
    ServerWorld getWorld();
}
