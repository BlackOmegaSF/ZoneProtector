package com.kleinercode.fabric.zoneprotector.mixin;

import com.kleinercode.fabric.zoneprotector.StateSaverAndLoader;
import com.kleinercode.fabric.zoneprotector.Zone;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerEntity.class)
public abstract class CheckShulkerTeleportMixin {

    // Prevent shulkers from teleporting into protected zones

    @Inject(method = "tryTeleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ShulkerEntity;findAttachSide(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/Direction;"), cancellable = true)
    private void onShulkerTeleportCheck(CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) BlockPos blockPos2) {

        StateSaverAndLoader state = StateSaverAndLoader.getServerState(((ShulkerEntity)(Object)this).getServer());
        Identifier worldId = ((ShulkerEntity)(Object)this).getWorld().getRegistryKey().getValue();
        for (Zone zone : state.zones) {
            if (zone.containsPosition(worldId, blockPos2)) {
                // Teleport needs to be cancelled
                cir.setReturnValue(false);
                return;
            }
        }

    }

}
