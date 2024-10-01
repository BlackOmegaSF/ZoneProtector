package com.kleinercode.fabric;

import jdk.jfr.Category;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public class Constants {

    public static final List<EntityType<?>> bannedTypes = List.of(
            EntityType.BLAZE,
            EntityType.BOGGED,
            EntityType.BREEZE,
            EntityType.CAVE_SPIDER,
            EntityType.CREEPER,
            EntityType.DRAGON_FIREBALL,
            EntityType.ELDER_GUARDIAN,
            EntityType.ENDER_DRAGON,
            EntityType.ENDERMAN,
            EntityType.ENDERMITE,
            EntityType.EVOKER,
            EntityType.EVOKER_FANGS,
            EntityType.FIREBALL,
            EntityType.GHAST,
            EntityType.GIANT,
            EntityType.GUARDIAN,
            EntityType.HOGLIN,
            EntityType.HUSK,
            EntityType.ILLUSIONER,
            EntityType.LLAMA_SPIT,
            EntityType.MAGMA_CUBE,
            EntityType.PHANTOM,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.PILLAGER,
            EntityType.RAVAGER,
            EntityType.SHULKER,
            EntityType.SHULKER_BULLET,
            EntityType.SILVERFISH,
            EntityType.SKELETON,
            EntityType.SKELETON_HORSE,
            EntityType.SLIME,
            EntityType.SMALL_FIREBALL,
            EntityType.SPIDER,
            EntityType.STRAY,
            EntityType.TNT,
            EntityType.TNT_MINECART,
            EntityType.VEX,
            EntityType.VINDICATOR,
            EntityType.WARDEN,
            EntityType.WITCH,
            EntityType.WITHER,
            EntityType.WITHER_SKELETON,
            EntityType.WITHER_SKULL,
            EntityType.ZOGLIN,
            EntityType.ZOMBIE,
            EntityType.ZOMBIE_HORSE,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.ZOMBIFIED_PIGLIN
    );

    public static final List<Item> bannedSpawnEggs = List.of(
            Items.BLAZE_SPAWN_EGG,
            Items.BOGGED_SPAWN_EGG,
            Items.BREEZE_SPAWN_EGG,
            Items.CAVE_SPIDER_SPAWN_EGG,
            Items.CREEPER_SPAWN_EGG,
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
            Items.ZOMBIE_VILLAGER_SPAWN_EGG
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
