package com.kleinercode.fabric.zoneprotector.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerExplosion.class)
public interface ExplosionAccessor {
    @Accessor
    ServerLevel getLevel();
}
