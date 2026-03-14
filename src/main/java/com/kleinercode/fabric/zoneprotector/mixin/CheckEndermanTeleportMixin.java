package com.kleinercode.fabric.zoneprotector.mixin;

import com.kleinercode.fabric.zoneprotector.ZonePersistentState;
import com.kleinercode.fabric.zoneprotector.Zone;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class CheckEndermanTeleportMixin {

    // Prevent enderman from teleporting into protected zones

    @Inject(method = "teleport(DDD)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1), cancellable = true)
    private void onEndermanTeleportCheck(double x, double y, double z, CallbackInfoReturnable<Boolean> cir, @Local BlockPos.MutableBlockPos mutable) {

        ZonePersistentState serverState = ZonePersistentState.getServerState(((EnderMan)(Object)this).level().getServer());
        Identifier worldId = ((EnderMan)(Object)this).level().dimension().identifier();
        //BlockPos position = new BlockPos((int) x, (int) y, (int) z);
        for (Zone zone : serverState.getZones()) {
            if (zone.containsPosition(worldId, mutable)) {
                // Teleport needs to be cancelled
                //ZoneProtector.LOGGER.debug("Cancelling enderman teleport within zone {}", zone.prettyPrint());
                cir.setReturnValue(false);
                return;
            }
        }

    }

}
