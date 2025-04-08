package me.boggy.foliaArenas.database;

import me.boggy.foliaArenas.manager.ArenaManager;
import org.bukkit.Material;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ArenaIO {

    private static final byte LOCATION_SPLIT = '\u0001'; // Character to split location data
    private static final byte MATERIAL_SPLIT = '\u0002'; // Character to split material data

    public static boolean saveLocationMaterialMap(File file, HashMap<ArenaManager.LocationKey, Material> map) {
        try (FileOutputStream stream = new FileOutputStream(file);
             ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {

            for (Map.Entry<ArenaManager.LocationKey, Material> entry : map.entrySet()) {
                ArenaManager.LocationKey location = entry.getKey();
                Material material = entry.getValue();

                String locationData =
                        location.getX() + "," +
                        location.getY() + "," +
                        location.getZ();
                byte[] locationBytes = locationData.getBytes(StandardCharsets.US_ASCII);
                byteStream.write(locationBytes);
                byteStream.write(LOCATION_SPLIT);

                String materialName = material.name();
                byte[] materialBytes = materialName.getBytes(StandardCharsets.US_ASCII);
                byteStream.write(materialBytes);
                byteStream.write(MATERIAL_SPLIT);
            }

            byte[] totalBytes = byteStream.toByteArray();
            stream.write(totalBytes);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static HashMap<ArenaManager.LocationKey, Material> loadLocationMaterialMap(File file) {
        HashMap<ArenaManager.LocationKey, Material> map = new HashMap<>();

        try {
            byte[] readBytes = Files.readAllBytes(file.toPath());

            String data = new String(readBytes, StandardCharsets.US_ASCII);
            String[] entries = data.split(Character.toString(MATERIAL_SPLIT));

            for (String entry : entries) {
                if (entry.isEmpty()) continue;

                String[] parts = entry.split(Character.toString(LOCATION_SPLIT));
                if (parts.length != 2) continue;

                String[] locData = parts[0].split(",");
                if (locData.length != 3) continue;

                int x = Integer.parseInt(locData[0]);
                int y = Integer.parseInt(locData[1]);
                int z = Integer.parseInt(locData[2]);

                Material material = Material.matchMaterial(parts[1]);
                if (material == null) {
                    System.err.println("Unknown material: " + parts[1]);
                    continue;
                }

                map.put(new ArenaManager.LocationKey(x, y, z), material);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }
}
