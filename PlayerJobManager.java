package com.frikifest.frikijobs.manager;

import com.frikifest.frikijobs.FrikiJobs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJobManager {
    private final FrikiJobs plugin;
    private File file;
    private FileConfiguration config;
    private final Map<UUID, String> playerJobs = new HashMap<>();

    public PlayerJobManager(FrikiJobs plugin) {
        this.plugin = plugin;
        createFile();
    }

    private void createFile() {
        file = new File(plugin.getDataFolder(), "player_jobs.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("No se pudo crear player_jobs.yml");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                playerJobs.put(uuid, config.getString(key));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public String getPlayerJob(UUID uuid) {
        return playerJobs.get(uuid);
    }

    public void setPlayerJob(UUID uuid, String jobId) {
        if (jobId == null) {
            playerJobs.remove(uuid);
            config.set(uuid.toString(), null);
        } else {
            playerJobs.put(uuid, jobId);
            config.set(uuid.toString(), jobId);
        }
        save();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar player_jobs.yml");
        }
    }
}
