package com.kleinercode.plugins.zoneprotector;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static com.kleinercode.plugins.zoneprotector.CoordinateUtils.parseIntCoordinate;

public final class ZoneProtector extends JavaPlugin implements Listener {

    private static final String _PROTECT = "protect";
    private static final String _UNPROTECT = "unprotect";
    private static final String _LIST = "list";

    private final List<Zone> zones = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        ConfigurationSerialization.registerClass(Zone.class);
        saveDefaultConfig(); // Saves default config if none exists. Will not overwrite existing
        // Get and load config
        List<Zone> loadedZones;
        try {
            loadedZones = (List<Zone>) getConfig().getList("zones");
            if (loadedZones != null) {
                zones.addAll(loadedZones);
            }
        } catch (ClassCastException e) {
            getLogger().log(Level.CONFIG, "Error loading zones from configuration!");
        }

        PluginCommand zone = this.getCommand("zone");
        if (zone != null) {
            zone.setExecutor(new CommandZone());
            zone.setTabCompleter(new CommandZoneTabCompleter());
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
        if (!(Constants.bannedTypes.contains(event.getType()))) {
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
        if (!(Constants.bannedTypes.contains(event.getEntityType()))) {
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

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {

        // Only check right clicks on blocks
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Only do anything when it's a spawn egg being used
        ItemStack item = event.getItem();
        if (item == null) return;
        Material material = item.getType();
        if (Constants.bannedSpawnEggs.contains(material)) {
            Location interactionPoint = event.getInteractionPoint();
            if (interactionPoint == null) return;
            for (Zone zone : zones) {
                if (zone.checkIfWithin(event.getInteractionPoint())) {
                    event.getPlayer().sendMessage(Component.text("This area is protected! You cannot spawn monsters here."));
                }
            }
        }

    }

    @EventHandler
    public void entityExplodeEvent(EntityExplodeEvent event) {
        // Only check entities from banned list
        if (Constants.bannedTypes.contains(event.getEntityType())) {
            // Check if any block destroyed is inside a protected zone
            for (Zone zone : zones) {
                for (Block block : event.blockList()) {
                    if (zone.checkIfWithin(block.getLocation())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void entityTeleportEvent(EntityTeleportEvent event) {
        // Only check entities from banned list
        if (Constants.bannedTypes.contains(event.getEntityType())) {
            // Check if teleporting into protected zone
            Location toLocation = event.getTo();
            if (toLocation == null) return;
            for (Zone zone : zones) {
                if (zone.checkIfWithin(toLocation)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void entityChangeBlockEvent(EntityChangeBlockEvent event) {
        // Only check entities from banned list
        if (Constants.bannedTypes.contains(event.getEntityType())) {
            // Check if block is protected
            for (Zone zone : zones) {
                if (zone.checkIfWithin(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }


    public class CommandZone implements CommandExecutor {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                return false;
            }

            if (args.length < 7 && !args[0].equalsIgnoreCase(_LIST)) {
                sender.sendMessage("Missing arguments!");
                return false;
            }

            if (args.length > 7) {
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

                case _PROTECT, _UNPROTECT -> {
                    UUID worldId;
                    if (sender instanceof BlockCommandSender) {
                        worldId = ((BlockCommandSender) sender).getBlock().getWorld().getUID();
                    } else if (sender instanceof Entity) {
                        worldId = ((Entity) sender).getWorld().getUID();
                    } else {
                        // Sender isn't locatable, send error message
                        sender.sendMessage("This command can only be run by locatable senders");
                        return true;
                    }

                    // Parse coordinates
                    BlockCoordinate lowerLocation;
                    BlockCoordinate upperLocation;
                    try {
                        lowerLocation = new BlockCoordinate(
                                parseIntCoordinate(args[1], sender, Enums.CoordinateType.X),
                                parseIntCoordinate(args[2], sender, Enums.CoordinateType.Y),
                                parseIntCoordinate(args[3], sender, Enums.CoordinateType.Z)
                        );

                        upperLocation = new BlockCoordinate(
                                parseIntCoordinate(args[4], sender, Enums.CoordinateType.X),
                                parseIntCoordinate(args[5], sender, Enums.CoordinateType.Y),
                                parseIntCoordinate(args[6], sender, Enums.CoordinateType.Z)
                        );
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Invalid integer coordinates!");
                        return true;
                    }


                    // Create the zone
                    Zone newZone;
                    try {
                        newZone = new Zone(worldId, lowerLocation, upperLocation);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(e.getMessage());
                        return true;
                    }

                    // Check if zone already exists
                    try {
                        for (Zone zone : zones) {
                            if (zone.equals(newZone)) {
                                if (args[0].equalsIgnoreCase(_UNPROTECT)) {
                                    // Remove it from the list
                                    zones.remove(zone);
                                    getConfig().set("zones", zones);
                                    saveConfig();
                                    sender.sendMessage("Stopped protecting zone:\n" + newZone.prettyPrint(getServer()));
                                    return true;
                                } else {
                                    throw new Exception("Zone already exists!");
                                }
                            }
                        }
                    } catch (Exception e) {
                        sender.sendMessage(e.getMessage());
                        return true;
                    }

                    if (args[0].equalsIgnoreCase(_PROTECT)) {
                        // Zone doesn't exist already, add it
                        zones.add(newZone);
                        getConfig().set("zones", zones);
                        saveConfig();
                        sender.sendMessage("Now protecting zone:\n" + newZone.prettyPrint(getServer()));
                    } else {
                        // If we reach here, the zone wasn't in the list
                        sender.sendMessage("Zone not currently under protection.");
                    }
                    return true;
                }

                default -> {
                    sender.sendMessage("Invalid parameter \"" + args[0].toLowerCase() + "\"");
                    return false;
                }

            }
        }
    }

    public class CommandZoneTabCompleter implements TabCompleter {

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            List<String> tabCompleteValues = new ArrayList<>();
            int position = args.length - 1;
            if (position == 0) {
                // Command type (list, protect, or unprotect)
                if (args[position].isEmpty()) {
                    tabCompleteValues.add(_LIST);
                    tabCompleteValues.add(_PROTECT);
                    tabCompleteValues.add(_UNPROTECT);
                } else {
                    Pattern pattern = Pattern.compile(args[0].toLowerCase());
                    if (pattern.matcher(_LIST).lookingAt()) {
                        tabCompleteValues.add(_LIST);
                    }
                    if (pattern.matcher(_PROTECT).lookingAt()) {
                        tabCompleteValues.add(_PROTECT);
                    }
                    if (pattern.matcher(_UNPROTECT).lookingAt()) {
                        tabCompleteValues.add(_UNPROTECT);
                    }
                }
                return tabCompleteValues;
            }

            if (args[0].equalsIgnoreCase(_LIST)) return tabCompleteValues;

            if (args[0].equalsIgnoreCase(_PROTECT)) {
                switch(position) {
                    case 1, 4 -> {
                        // X coordinate
                        if (sender instanceof Player) {
                            String xCoord = String.valueOf(((Player) sender).getLocation().getBlockX());
                            if (matchValuePattern(args[position], xCoord)) tabCompleteValues.add(xCoord);
                        }
                    }

                    case 2, 5 -> {
                        // Y coordinate
                        if (sender instanceof Player) {
                            String yCoord = String.valueOf(((Player) sender).getLocation().getBlockY());
                            if (matchValuePattern(args[position], yCoord)) tabCompleteValues.add(yCoord);
                        }
                    }

                    case 3, 6 -> {
                        // Z coordinate
                        if (sender instanceof Player) {
                            String zCoord = String.valueOf(((Player) sender).getLocation().getBlockZ());
                            if (matchValuePattern(args[position], zCoord)) tabCompleteValues.add(zCoord);
                        }
                    }
                }
            }

            if (args[0].equalsIgnoreCase(_UNPROTECT)) {
                switch(position) {

                    case 1 -> {
                        // First x-coordinate
                        for (Zone zone: zones) {
                            String xCoord = String.valueOf(zone.lowerLocation.x);
                            if (matchValuePattern(args[position], xCoord)) tabCompleteValues.add(xCoord);
                        }
                    }

                    case 2 -> {
                        // First y-coordinate
                        for (Zone zone: zones) {
                            try {
                                if (Integer.parseInt(args[1]) == zone.lowerLocation.x) {
                                    String yCoord = String.valueOf(zone.lowerLocation.y);
                                    if (matchValuePattern(args[position], yCoord)) tabCompleteValues.add(yCoord);
                                }
                            } catch (NumberFormatException e) { /* Do nothing, something is invalid*/ }
                        }
                    }

                    case 3 -> {
                        // First z-coordinate
                        for (Zone zone: zones) {
                            try {
                                if (Integer.parseInt(args[1]) == zone.lowerLocation.x && Integer.parseInt(args[2]) == zone.lowerLocation.y) {
                                    String zCoord = String.valueOf(zone.lowerLocation.z);
                                    if (matchValuePattern(args[position], zCoord)) tabCompleteValues.add(zCoord);
                                }
                            } catch (NumberFormatException e) { /* Do nothing, something is invalid*/ }
                        }
                    }

                    case 4 -> {
                        // Second x-coordinate
                        for (Zone zone: zones) {
                            try {
                                if (Integer.parseInt(args[1]) == zone.lowerLocation.x
                                        && Integer.parseInt(args[2]) == zone.lowerLocation.y
                                        && Integer.parseInt(args[3]) == zone.lowerLocation.z
                                ) {
                                    String xCoord = String.valueOf(zone.upperLocation.x);
                                    if (matchValuePattern(args[position], xCoord)) tabCompleteValues.add(xCoord);
                                }
                            } catch (NumberFormatException e) { /* Do nothing, something is invalid*/ }
                        }
                    }

                    case 5 -> {
                        // Second y-coordinate
                        for (Zone zone: zones) {
                            try {
                                if (Integer.parseInt(args[1]) == zone.lowerLocation.x
                                        && Integer.parseInt(args[2]) == zone.lowerLocation.y
                                        && Integer.parseInt(args[3]) == zone.lowerLocation.z
                                        && Integer.parseInt(args[4]) == zone.upperLocation.x
                                ) {
                                    String yCoord = String.valueOf(zone.upperLocation.y);
                                    if (matchValuePattern(args[position], yCoord)) tabCompleteValues.add(yCoord);
                                }
                            } catch (NumberFormatException e) { /* Do nothing, something is invalid*/ }
                        }
                    }

                    case 6 -> {
                        // Second z-coordinate
                        for (Zone zone: zones) {
                            try {
                                if (Integer.parseInt(args[1]) == zone.lowerLocation.x
                                        && Integer.parseInt(args[2]) == zone.lowerLocation.y
                                        && Integer.parseInt(args[3]) == zone.lowerLocation.z
                                        && Integer.parseInt(args[4]) == zone.upperLocation.x
                                        && Integer.parseInt(args[5]) == zone.upperLocation.y
                                ) {
                                    String zCoord = String.valueOf(zone.upperLocation.z);
                                    if (matchValuePattern(args[position], zCoord)) tabCompleteValues.add(zCoord);
                                }
                            } catch (NumberFormatException e) { /* Do nothing, something is invalid*/ }
                        }
                    }

                }
            }

            return tabCompleteValues;
        }

        private Boolean matchValuePattern(String userInput, String match) {
            if (userInput.isEmpty()) {
                return true;
            } else {
                Pattern pattern = Pattern.compile(userInput);
                return pattern.matcher(match).lookingAt();
            }
        }
    }
}
