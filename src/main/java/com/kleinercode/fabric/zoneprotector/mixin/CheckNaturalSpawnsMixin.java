package com.kleinercode.fabric.zoneprotector.mixin;

import com.kleinercode.fabric.zoneprotector.Constants;
import com.kleinercode.fabric.zoneprotector.ZonePersistentState;
import com.kleinercode.fabric.zoneprotector.Zone;
import com.kleinercode.fabric.zoneprotector.ZoneProtector;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public class CheckNaturalSpawnsMixin {

    // Check if natural spawn is within protected zone

    @Inject(method = "isValidSpawnPostitionForType(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/biome/MobSpawnSettings$SpawnerData;Lnet/minecraft/core/BlockPos$MutableBlockPos;D)Z", at = @At("HEAD"), cancellable = true)
    private static void onNaturalSpawnCheck(ServerLevel world, MobCategory group, StructureManager structureAccessor, ChunkGenerator chunkGenerator, MobSpawnSettings.SpawnerData spawnEntry, BlockPos.MutableBlockPos pos, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {

        if (Constants.bannedTypes.contains(spawnEntry.type())) {
            ZonePersistentState serverState = ZonePersistentState.getServerState(world.getServer());
            for (Zone zone : serverState.getZones()) {
                if (zone.containsPosition(world.dimension().identifier(), pos)) {
                    // Natural spawn needs to be cancelled
                    ZoneProtector.LOGGER.debug("Cancelling spawn within zone {}", zone.prettyPrint());
                    cir.setReturnValue(false);
                    return;
                }
            }
        }

    }


}
