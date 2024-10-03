package com.kleinercode.fabric.mixin;

import com.google.common.collect.Sets;
import com.kleinercode.fabric.StateSaverAndLoader;
import com.kleinercode.fabric.Zone;
import com.kleinercode.fabric.ZoneProtector;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Explosion.class)
public abstract class ExplosionProtectionMixin {

    // Prevent explosions from damaging protected areas

    @Inject(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;addAll(Ljava/util/Collection;)Z", remap = false))
    private void onExplosionCheck(CallbackInfo info, @Local Set<BlockPos> set) {

        World world = ((ExplosionAccessor)this).getWorld();
        StateSaverAndLoader state = StateSaverAndLoader.getServerState(world.getServer());
        Identifier worldId = world.getRegistryKey().getValue();
        Set<BlockPos> toRemove = Sets.newHashSet();
        for (BlockPos pos : set) {
            for (Zone zone : state.zones) {
                if (zone.containsPosition(worldId, pos)) {
                    toRemove.add(pos);
                }
            }
        }
        set.removeAll(toRemove);

    }

}
