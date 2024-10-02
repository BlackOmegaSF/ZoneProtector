package com.kleinercode.fabric.mixin;

import com.kleinercode.fabric.Constants;
import com.kleinercode.fabric.StateSaverAndLoader;
import com.kleinercode.fabric.Zone;
import com.kleinercode.fabric.ZoneProtector;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnHelper.class)
public class CheckNaturalSpawnsMixin {

    // Check if natural spawn is within protected zone

    @Inject(method = "canSpawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/biome/SpawnSettings$SpawnEntry;Lnet/minecraft/util/math/BlockPos$Mutable;D)Z", at = @At("HEAD"), cancellable = true)
    private static void onNaturalSpawnCheck(ServerWorld world, SpawnGroup group, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnSettings.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {

        if (Constants.bannedTypes.contains(spawnEntry.type)) {
            StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());
            for (Zone zone : serverState.zones) {
                if (zone.containsPosition(world.getRegistryKey().getValue(), pos)) {
                    // Natural spawn needs to be cancelled
                    ZoneProtector.LOGGER.debug("Cancelling spawn within zone {}", zone.prettyPrint());
                    cir.setReturnValue(false);
                    return;
                }
            }
        }

    }


}
