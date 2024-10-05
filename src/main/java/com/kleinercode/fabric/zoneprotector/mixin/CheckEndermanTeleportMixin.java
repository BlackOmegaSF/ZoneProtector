package com.kleinercode.fabric.zoneprotector.mixin;

import com.kleinercode.fabric.zoneprotector.StateSaverAndLoader;
import com.kleinercode.fabric.zoneprotector.Zone;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public abstract class CheckEndermanTeleportMixin {

    // Prevent enderman from teleporting into protected zones

    @Inject(method = "teleportTo(DDD)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 1), cancellable = true)
    private void onEndermanTeleportCheck(double x, double y, double z, CallbackInfoReturnable<Boolean> cir, @Local BlockPos.Mutable mutable) {

        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(((EndermanEntity)(Object)this).getServer());
        Identifier worldId = ((EndermanEntity)(Object)this).getWorld().getRegistryKey().getValue();
        //BlockPos position = new BlockPos((int) x, (int) y, (int) z);
        for (Zone zone : serverState.zones) {
            if (zone.containsPosition(worldId, mutable)) {
                // Teleport needs to be cancelled
                //ZoneProtector.LOGGER.debug("Cancelling enderman teleport within zone {}", zone.prettyPrint());
                cir.setReturnValue(false);
                return;
            }
        }

    }

}
