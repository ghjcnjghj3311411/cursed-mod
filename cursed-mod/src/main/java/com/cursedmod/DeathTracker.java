package com.cursedmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class DeathTracker {
    private Map<String, WorldData> worldsData = new HashMap<>();
    private String currentWorldId = null;
    private final Path saveFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public static class WorldData {
        public int deaths = 0;
        public boolean completed = false;
    }
    
    public DeathTracker() {
        saveFile = Paths.get(Minecraft.getInstance().gameDirectory.getPath(), 
                            "cursedmod_data.json");
        loadData();
    }
    
    public void setCurrentWorld(String worldId) {
        this.currentWorldId = worldId;
        if (!worldsData.containsKey(worldId)) {
            worldsData.put(worldId, new WorldData());
        }
        saveData();
    }
    
    public void clearCurrentWorld() {
        this.currentWorldId = null;
    }
    
    public String getCurrentWorldId() {
        return currentWorldId;
    }
    
    public int getDeaths() {
        if (currentWorldId == null) return 0;
        WorldData data = worldsData.get(currentWorldId);
        return data != null ? data.deaths : 0;
    }
    
    public void addDeath() {
        if (currentWorldId == null) return;
        WorldData data = worldsData.get(currentWorldId);
        if (data != null && !data.completed) {
            data.deaths++;
            saveData();
        }
    }
    
    public boolean isCompleted() {
        if (currentWorldId == null) return false;
        WorldData data = worldsData.get(currentWorldId);
        return data != null && data.completed;
    }
    
    public void setCompleted() {
        if (currentWorldId == null) return;
        WorldData data = worldsData.get(currentWorldId);
        if (data != null) {
            data.completed = true;
            data.deaths = 0;
            saveData();
        }
    }
    
    public int getEffectLevel() {
        return Math.min(getDeaths(), 10);
    }
    
    public boolean isMemzMode() {
        return getDeaths() >= 10;
    }
    
    private void loadData() {
        try {
            if (Files.exists(saveFile)) {
                String json = new String(Files.readAllBytes(saveFile));
                worldsData = gson.fromJson(json, 
                    new TypeToken<HashMap<String, WorldData>>(){}.getType());
                if (worldsData == null) {
                    worldsData = new HashMap<>();
                }
            }
        } catch (Exception e) {
            CursedMod.LOGGER.error("Ошибка загрузки: " + e.getMessage());
            worldsData = new HashMap<>();
        }
    }
    
    private void saveData() {
        try {
            String json = gson.toJson(worldsData);
            Files.write(saveFile, json.getBytes());
        } catch (Exception e) {
            CursedMod.LOGGER.error("Ошибка сохранения: " + e.getMessage());
        }
    }
}