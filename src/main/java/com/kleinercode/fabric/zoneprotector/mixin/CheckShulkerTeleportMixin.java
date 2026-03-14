package com.kleinercode.fabric.zoneprotector.mixin;

import com.kleinercode.fabric.zoneprotector.ZonePersistentState;
import com.kleinercode.fabric.zoneprotector.Zone;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Shulker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Shulker.class)
public abstract class CheckShulkerTeleportMixin {

    // Prevent shulkers from teleporting into protected zones

    @Inject(method = "teleportSomewhere", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Shulker;findAttachableSurface(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Direction;"), cancellable = true)
    private void onShulkerTeleportCheck(CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) BlockPos blockPos2) {

        ZonePersistentState state = ZonePersistentState.getServerState(((Shulker)(Object)this).level().getServer());
        Identifier worldId = ((Shulker)(Object)this).level().dimension().identifier();
        for (Zone zone : state.getZones()) {
            if (zone.containsPosition(worldId, blockPos2)) {
                // Teleport needs to be cancelled
                cir.setReturnValue(false);
                return;
            }
        }

    }

}
