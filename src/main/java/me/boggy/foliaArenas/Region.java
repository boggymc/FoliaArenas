package me.boggy.foliaArenas;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private Location minLocation;
    private Location maxLocation;

    public Region(Location firstPoint, Location secondPoint) {
        minLocation = new Location(firstPoint.getWorld(),
                min(firstPoint.getX(), secondPoint.getX()),
                min(firstPoint.getY(), secondPoint.getY()),
                min(firstPoint.getZ(), secondPoint.getZ()));

        maxLocation = new Location(firstPoint.getWorld(),
                max(firstPoint.getX(), secondPoint.getX()),
                max(firstPoint.getY(), secondPoint.getY()),
                max(firstPoint.getZ(), secondPoint.getZ()));
    }

    public boolean isInRegion(Location loc) {
        return (minLocation.getX() <= loc.getX()
                && minLocation.getY() <= loc.getY()
                && minLocation.getZ() <= loc.getZ()
                && maxLocation.getX() >= loc.getX()
                && maxLocation.getY() >= loc.getY()
                && maxLocation.getZ() >= loc.getZ());
    }

    public List<Location> getBlockLocations() {
        List<Location> blockLocations = new ArrayList<>();
        World world = Bukkit.getWorld("world");

        for (int x = minLocation.getBlockX(); x <= maxLocation.getBlockX(); x++) {
            for (int y = minLocation.getBlockY(); y <= maxLocation.getBlockY(); y++) {
                for (int z = minLocation.getBlockZ(); z <= maxLocation.getBlockZ(); z++) {
                    blockLocations.add(new Location(world, x, y, z));
                }
            }
        }

        return blockLocations;
    }

    private double min(double a, double b) {
        return Math.min(a, b);
    }

    private double max(double a, double b) {
        return Math.max(a, b);
    }

}