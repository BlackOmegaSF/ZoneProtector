package com.kleinercode.plugins.zoneprotector;

import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class CoordinateUtils {

    public int parseIntCoordinate(String coordinate, Player player, Enums.CoordinateType type) {
        Pattern relativePattern = Pattern.compile("^~(?:-?\\d+)?$");
        if (relativePattern.matcher(coordinate).matches()) {
            // Parse relative coordinate
            int playerCoordinate;
            switch(type) {
                case Enums.CoordinateType.X -> playerCoordinate = player.getLocation().getBlockX();
                case Enums.CoordinateType.Y -> playerCoordinate = player.getLocation().getBlockY();
                case Enums.CoordinateType.Z -> playerCoordinate = player.getLocation().getBlockZ();
                default -> playerCoordinate = 0;
            }

            String modifierString = coordinate.replace("~", "");
            if (modifierString.isBlank()) return playerCoordinate;
            int modifier = Integer.parseInt(coordinate);

            return playerCoordinate + modifier;

        } else {
            return Integer.parseInt(coordinate);
        }
    }

}
