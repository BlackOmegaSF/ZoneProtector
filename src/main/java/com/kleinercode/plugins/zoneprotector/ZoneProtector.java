package com.kleinercode.plugins.zoneprotector;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static java.lang.Integer.parseInt;

public final class ZoneProtector extends JavaPlugin implements Listener {

    private final List<Zone> zones = new ArrayList<>();
    private final List<EntityType> bannedTypes = List.of(
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

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        ConfigurationSerialization.registerClass(Zone.class);
        saveDefaultConfig(); // Saves default config if none exists. Will not overwrite existing
        // Get and load config
        List<Zone> loadedZones = (List<Zone>) getConfig().getList("zones");
        if (zones != null) {
            assert loadedZones != null;
            zones.addAll(loadedZones);
        }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        zones.clear();
    }

    @EventHandler
    public void preCreatureSpawnEvent(PreCreatureSpawnEvent event) {

        // Check creature type
        if (!(bannedTypes.contains(event.getType()))) {
            // Spawn is allowed, continue
            return;
        }

        // Check each zone, shortcut out if needed
        for (Zone zone : zones) {
            if (zone.checkIfWithin(event.getSpawnLocation())) {
                // In the box, that's illegal!
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void creatureSpawnEvent(CreatureSpawnEvent event) {

        // Check creature type
        if (!(bannedTypes.contains(event.getEntityType()))) {
            // Spawn is allowed, continue
            return;
        }

        // Check each zone, shortcut out if needed
        for (Zone zone : zones) {
            if (zone.checkIfWithin(event.getLocation())) {
                // In the box, that's illegal!
                event.setCancelled(true);
                break;
            }
        }
    }

    public class CommandZone implements CommandExecutor {

        private static final String _PROTECT = "protect";
        private static final String _UNPROTECT = "unprotect";
        private static final String _LIST = "list";

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                return false;
            }

            if (args.length < 8 && !args[0].equalsIgnoreCase(_LIST)) {
                sender.sendMessage("Missing arguments!");
                return false;
            }

            if (args.length > 8) {
                sender.sendMessage("Too many arguments!");
                return false;
            }

            switch(args[0].toLowerCase()) {

                case _LIST -> {
                    if (zones.isEmpty()) {
                        sender.sendMessage("No zones protected.");
                    } else {
                        StringJoiner joiner = new StringJoiner("\n");
                        zones.forEach(zone -> joiner.add(zone.prettyPrint()));
                        sender.sendMessage("Protecting " + zones.size() + " zones:\n" + joiner);
                    }
                    return true;
                }

                case _PROTECT -> {
                    World.Environment newEnv;
                    try {
                        newEnv = World.Environment.valueOf(args[1]);
                    } catch (Exception e) {
                        sender.sendMessage("Invalid dimension provided!");
                        return true;
                    }

                    // Parse coordinates
                    BlockCoordinate lowerLocation;
                    BlockCoordinate upperLocation;
                    try {
                        lowerLocation = new BlockCoordinate(
                            parseInt(args[2]),
                            parseInt(args[3]),
                            parseInt(args[4])
                        );

                        upperLocation = new BlockCoordinate(
                                parseInt(args[5]),
                                parseInt(args[6]),
                                parseInt(args[7])
                        );
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Invalid coordinates!");
                        return true;
                    }

                    // Create the zone
                    Zone newZone;
                    try {
                        newZone = new Zone(newEnv, lowerLocation, upperLocation);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(e.getMessage());
                        return true;
                    }

                    // Check if zone already exists
                    try {
                        for (Zone zone : zones) {
                            if (zone.equals(newZone)) {
                                throw new Exception("Zone already exists!");
                            }
                        }
                    } catch (Exception e) {
                        sender.sendMessage(e.getMessage());
                        return true;
                    }

                    // Zone doesn't exist already, add it
                    zones.add(newZone);
                    getConfig().set("zones", zones);
                    saveConfig();
                    sender.sendMessage("Now protecting zone:\n" + newZone.prettyPrint());
                    return true;
                }

                case _UNPROTECT -> {
                    World.Environment targetEnv;
                    try {
                        targetEnv = World.Environment.valueOf(args[1]);
                    } catch (Exception e) {
                        sender.sendMessage("Invalid dimension provided!");
                        return true;
                    }

                    // Parse coordinates
                    BlockCoordinate lowerLocation;
                    BlockCoordinate upperLocation;
                    try {
                        lowerLocation = new BlockCoordinate(
                                parseInt(args[2]),
                                parseInt(args[3]),
                                parseInt(args[4])
                        );

                        upperLocation = new BlockCoordinate(
                                parseInt(args[5]),
                                parseInt(args[6]),
                                parseInt(args[7])
                        );
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Invalid coordinates!");
                        return true;
                    }

                    // Create the zone
                    Zone targetZone;
                    try {
                        targetZone = new Zone(targetEnv, lowerLocation, upperLocation);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(e.getMessage());
                        return true;
                    }

                    // Check if zone already exists
                    for (Zone zone : zones) {
                        if (zone.equals(targetZone)) {
                            // Remove it from the list
                            zones.remove(zone);
                            getConfig().set("zones", zones);
                            saveConfig();
                            sender.sendMessage("Stopped protecting zone:\n" + targetZone.prettyPrint());
                            return true;
                        }
                    }

                    // If we reach here, the zone wasn't in the list
                    sender.sendMessage("Zone not currently under protection.");
                    return true;
                }

                default -> {
                    sender.sendMessage("Invalid parameter \"" + args[0].toLowerCase() + "\"");
                    return false;
                }

            }
        }
    }
}
