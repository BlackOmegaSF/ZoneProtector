package com.kleinercode.fabric.zoneprotector.mixin;

import com.google.common.collect.Sets;
import com.kleinercode.fabric.zoneprotector.ZonePersistentState;
import com.kleinercode.fabric.zoneprotector.Zone;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(ExplosionImpl.class)
public abstract class ExplosionProtectionMixin {

    // Prevent explosions from damaging protected areas

    @Inject(method = "getBlocksToDestroy", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;<init>(Ljava/util/Collection;)V", remap = false))
    private void onExplosionGetBlocksToDestroy(CallbackInfoReturnable<List<BlockPos>> cir, @Local Set<BlockPos> set) {

        ServerWorld world = ((ExplosionAccessor)this).getWorld();
        ZonePersistentState state = ZonePersistentState.getServerState(world.getServer());
        Identifier worldId = world.getRegistryKey().getValue();
        Set<BlockPos> toRemove = Sets.newHashSet();
        for (BlockPos pos : set) {
            for (Zone zone : state.getZones()) {
                if (zone.containsPosition(worldId, pos)) {
                    toRemove.add(pos);
                }
            }
        }
        set.removeAll(toRemove);

    }

}
