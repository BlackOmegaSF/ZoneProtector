package com.kleinercode.fabric.zoneprotector;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class Constants {

    public static final String nbtZones = "zones";
    public static final String nbtZoneName = "zoneName";
    public static final String nbtDimension = "dimension";

    public static final List<EntityType<?>> bannedTypes = List.of(
            EntityTypes.BLAZE,
            EntityTypes.BOGGED,
            EntityTypes.BREEZE,
            EntityTypes.CAVE_SPIDER,
            EntityTypes.CREAKING,
            EntityTypes.CREEPER,
            EntityTypes.DRAGON_FIREBALL,
            EntityTypes.DROWNED,
            EntityTypes.ELDER_GUARDIAN,
            EntityTypes.ENDER_DRAGON,
            EntityTypes.ENDERMAN,
            EntityTypes.ENDERMITE,
            EntityTypes.EVOKER,
            EntityTypes.EVOKER_FANGS,
            EntityTypes.FIREBALL,
            EntityTypes.GHAST,
            EntityTypes.GIANT,
            EntityTypes.GUARDIAN,
            EntityTypes.HOGLIN,
            EntityTypes.HUSK,
            EntityTypes.ILLUSIONER,
            EntityTypes.LLAMA_SPIT,
            EntityTypes.MAGMA_CUBE,
            EntityTypes.PARCHED,
            EntityTypes.PHANTOM,
            EntityTypes.PIGLIN,
            EntityTypes.PIGLIN_BRUTE,
            EntityTypes.PILLAGER,
            EntityTypes.RAVAGER,
            EntityTypes.SHULKER,
            EntityTypes.SHULKER_BULLET,
            EntityTypes.SILVERFISH,
            EntityTypes.SKELETON,
            EntityTypes.SKELETON_HORSE,
            EntityTypes.SLIME,
            EntityTypes.SMALL_FIREBALL,
            EntityTypes.SPIDER,
            EntityTypes.STRAY,
            EntityTypes.TNT,
            EntityTypes.TNT_MINECART,
            EntityTypes.VEX,
            EntityTypes.VINDICATOR,
            EntityTypes.WARDEN,
            EntityTypes.WITCH,
            EntityTypes.WITHER,
            EntityTypes.WITHER_SKELETON,
            EntityTypes.WITHER_SKULL,
            EntityTypes.ZOGLIN,
            EntityTypes.ZOMBIE,
            EntityTypes.ZOMBIE_HORSE,
            EntityTypes.ZOMBIE_VILLAGER,
            EntityTypes.ZOMBIFIED_PIGLIN
    );

    public static final List<Item> bannedSpawnEggs = List.of(
            Items.BLAZE_SPAWN_EGG,
            Items.BOGGED_SPAWN_EGG,
            Items.BREEZE_SPAWN_EGG,
            Items.CAVE_SPIDER_SPAWN_EGG,
            Items.CREAKING_SPAWN_EGG,
            Items.CREEPER_SPAWN_EGG,
            Items.DROWNED_SPAWN_EGG,
            Items.ELDER_GUARDIAN_SPAWN_EGG,
            Items.ENDER_DRAGON_SPAWN_EGG,
            Items.ENDERMAN_SPAWN_EGG,
            Items.ENDERMITE_SPAWN_EGG,
            Items.EVOKER_SPAWN_EGG,
            Items.GHAST_SPAWN_EGG,
            Items.GUARDIAN_SPAWN_EGG,
            Items.HOGLIN_SPAWN_EGG,
            Items.HUSK_SPAWN_EGG,
            Items.MAGMA_CUBE_SPAWN_EGG,
            Items.PARCHED_SPAWN_EGG,
            Items.PHANTOM_SPAWN_EGG,
            Items.PIGLIN_SPAWN_EGG,
            Items.PIGLIN_BRUTE_SPAWN_EGG,
            Items.PILLAGER_SPAWN_EGG,
            Items.RAVAGER_SPAWN_EGG,
            Items.SHULKER_SPAWN_EGG,
            Items.SILVERFISH_SPAWN_EGG,
            Items.SKELETON_SPAWN_EGG,
            Items.SKELETON_HORSE_SPAWN_EGG,
            Items.SLIME_SPAWN_EGG,
            Items.SPIDER_SPAWN_EGG,
            Items.STRAY_SPAWN_EGG,
            Items.VEX_SPAWN_EGG,
            Items.VINDICATOR_SPAWN_EGG,
            Items.WARDEN_SPAWN_EGG,
            Items.WITCH_SPAWN_EGG,
            Items.WITHER_SPAWN_EGG,
            Items.WITHER_SKELETON_SPAWN_EGG,
            Items.ZOGLIN_SPAWN_EGG,
            Items.ZOMBIE_SPAWN_EGG,
            Items.ZOMBIE_HORSE_SPAWN_EGG,
            Items.ZOMBIE_VILLAGER_SPAWN_EGG,
            Items.ZOMBIFIED_PIGLIN_SPAWN_EGG
    );

    public enum CommandMode {
        LIST,
        PROTECT,
        UNPROTECT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
