package com.frikifest.frikijobs.manager;

import com.frikifest.frikijobs.FrikiJobs;
import com.frikifest.frikijobs.model.Job;
import com.frikifest.frikijobs.model.JobTier;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobManager {
    private final FrikiJobs plugin;
    private final Map<String, Job> jobs = new HashMap<>();

    public JobManager(FrikiJobs plugin) {
        this.plugin = plugin;
    }

    public void loadJobs() {
        jobs.clear();
        ConfigurationSection jobsSec = plugin.getConfig().getConfigurationSection("jobs");
        if (jobsSec == null) return;

        for (String jobId : jobsSec.getKeys(false)) {
            String name = jobsSec.getString(jobId + ".name", jobId);
            String desc = jobsSec.getString(jobId + ".description", "");
            String maxRewardInfo = jobsSec.getString(jobId + ".max_reward_info", "¡Recompensa especial al alcanzar el nivel máximo!");
            ConfigurationSection tiersSec = jobsSec.getConfigurationSection(jobId + ".tiers");
            Map<String, JobTier> tiers = new HashMap<>();

            if (tiersSec != null) {
                for (String tierId : tiersSec.getKeys(false)) {
                    int reqXp = tiersSec.getInt(tierId + ".required_xp", 0);
                    double money = tiersSec.getDouble(tierId + ".reward_money", 0.0);
                    int xp = tiersSec.getInt(tierId + ".reward_xp", 0);
                    List<String> materials = tiersSec.getStringList(tierId + ".materials");
                    String info = tiersSec.getString(tierId + ".info", "Sin información");

                    tiers.put(tierId, new JobTier(reqXp, money, xp, materials, info));
                }
            }
            jobs.put(jobId.toLowerCase(), new Job(jobId, name, desc, maxRewardInfo, tiers));
        }
    }

    public Job getJob(String id) {
        return jobs.get(id.toLowerCase());
    }

    public Map<String, Job> getJobs() {
        return jobs;
    }
}
