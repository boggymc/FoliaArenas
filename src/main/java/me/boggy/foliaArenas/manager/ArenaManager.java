package me.boggy.foliaArenas.manager;

import me.boggy.foliaArenas.FoliaArenas;
import me.boggy.foliaArenas.Region;
import me.boggy.foliaArenas.database.ArenaIO;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ArenaManager {
    private final FoliaArenas plugin;
    private HashMap<LocationKey, Material> regionMaterials = new HashMap<>();
    private List<Region> arenas = new ArrayList<>();
    private boolean shouldReset = true;

    public ArenaManager(FoliaArenas plugin) {
        this.plugin = plugin;
    }

    public void addArenas() {
        World world = Bukkit.getWorld("world");
        for (String sectionName : plugin.getConfig().getKeys(false)) {
            if (sectionName.length() > 2) {
                continue;
            }
            ConfigurationSection section = plugin.getConfig().getConfigurationSection(sectionName);
            String pos1String = section.getString("pos-1").replace(" ", "");
            String pos2String = section.getString("pos-2").replace(" ", "");

            String[] part = pos1String.split(",");
            int x = Integer.parseInt(part[0]);
            int y = Integer.parseInt(part[1]);
            int z = Integer.parseInt(part[2]);
            Location pos1 = new Location(world, x, y, z);

            part = pos2String.split(",");
            x = Integer.parseInt(part[0]);
            y = Integer.parseInt(part[1]);
            z = Integer.parseInt(part[2]);
            Location pos2 = new Location(world, x, y, z);

            arenas.add(new Region(pos1, pos2));
        }
    }

    public void loadRegion() {
        File folder = new File(plugin.getDataFolder(), "/arena/");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(plugin.getDataFolder(), "/arena/arena.dat");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            regionMaterials = ArenaIO.loadLocationMaterialMap(file);
            Bukkit.getConsoleSender().sendMessage("[FoliaArenas] " + ChatColor.GREEN + "Arena Loaded");
        }
    }

    public void saveRegion() {
        HashMap<LocationKey, Material> newRegionMaterials = getRegionMaterials();
        regionMaterials = newRegionMaterials;
        ArenaIO.saveLocationMaterialMap(new File(plugin.getDataFolder(), "/arena/" + "arena.dat"), newRegionMaterials);
    }

    public HashMap<LocationKey, Material> getRegionMaterials() {
        HashMap<LocationKey, Material> materials = new HashMap<>();
        for (Region region : arenas) {
            List<Location> blockLocations = region.getBlockLocations();
            for (Location location : blockLocations) {
                materials.put(new LocationKey(location.getBlockX(), location.getBlockY(), location.getBlockZ()), location.getBlock().getType());
            }
        }
        return materials;
    }

    public void startRegionReset() {
        World world = Bukkit.getWorld("world");

        plugin.getLogger().info("Starting arena reset...");

        plugin.getLogger().info("Loading blocks into memory...");
        Bukkit.getAsyncScheduler().runNow(plugin, (runnable) -> {
            int counter = 0;
            List<List<LocationKey>> chunks = new ArrayList<>();
            List<LocationKey> chunk = new ArrayList<>();

            List<LocationKey> sortedLocations = new ArrayList<>(regionMaterials.keySet());
            sortedLocations.sort(Comparator.comparingInt(LocationKey::getY));

            for (LocationKey location : sortedLocations) {
                counter++;
                chunk.add(location);

                if (counter == plugin.getConfig().getInt("regen-blocks")) {
                    counter = 0;
                    chunks.add(new ArrayList<>(chunk));
                    chunk.clear();
                }
            }

            if (!chunk.isEmpty()) {
                chunks.add(new ArrayList<>(chunk));
            }

            Bukkit.getGlobalRegionScheduler().run(plugin, (runnable2) -> {
                plugin.getLogger().info("Setting blocks...");
                int delayBetween = plugin.getConfig().getInt("delay-between");
                int counter2 = 1;
                for (List<LocationKey> regionChunk : chunks) {
                    if (regionChunk.isEmpty()) {
                        continue;
                    }
                    counter2++;
                    Bukkit.getAsyncScheduler().runDelayed(plugin, (runnable3) -> {
                        for (LocationKey location : regionChunk) {
                            Material material = regionMaterials.get(location);
                            Bukkit.getRegionScheduler().run(plugin, new Location(world, regionChunk.get(0).getX(), regionChunk.get(0).getY(), regionChunk.get(0).getZ()), (runnable4) -> {
                                world.getBlockAt(location.getX(), location.getY(), location.getZ()).setType(material);
                            });
                        }
                    },(delayBetween / 20L) * counter2, TimeUnit.SECONDS);
                }
                plugin.getLogger().info("Arena reset complete");
            });
        });

    }

    public boolean shouldReset() { return shouldReset; }
    public void setShouldReset(boolean b) { shouldReset = b; }

    private static class ChunkCoords {
        private final int x;
        private final int z;

        public ChunkCoords(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChunkCoords that = (ChunkCoords) o;
            return x == that.x && z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }
    }

    public static class LocationKey {
        private final int x;
        private final int y;
        private final int z;

        public LocationKey(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocationKey that = (LocationKey) o;
            return x == that.x && y == that.y && z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }

}
