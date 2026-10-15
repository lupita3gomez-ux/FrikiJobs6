package com.frikifest.frikijobs.listener;

import com.frikifest.frikijobs.FrikiJobs;
import com.frikifest.frikijobs.model.Job;
import com.frikifest.frikijobs.model.JobTier;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JobActionListener implements Listener {

    private final FrikiJobs plugin;
    private final Set<String> playerPlacedBlocks = new HashSet<>();

    public JobActionListener(FrikiJobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        String blockKey = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();
        playerPlacedBlocks.add(blockKey);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String blockKey = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();

        if (playerPlacedBlocks.contains(blockKey)) {
            playerPlacedBlocks.remove(blockKey);
            return;
        }

        String currentJob = plugin.getPlayerJobManager().getPlayerJob(player.getUniqueId());
        if (currentJob == null || !currentJob.equalsIgnoreCase("minero")) return;

        Job mineroJob = plugin.getJobManager().getJob("minero");
        if (mineroJob != null) {
            for (Map.Entry<String, JobTier> entry : mineroJob.getTiers().entrySet()) {
                JobTier tier = entry.getValue();
                if (tier.getMaterials() != null && tier.getMaterials().contains(block.getType().name())) {
                    
                    double money = tier.getRewardMoney();
                    int xp = tier.getRewardXp();

                    if (plugin.getEconomyManager() != null) {
                        plugin.getEconomyManager().depositPlayer(player, money);
                    }

                    player.sendActionBar(net.kyori.adventure.text.Component.text(
                        ChatColor.translateAlternateColorCodes('&', 
                        "&6[Minero &e" + entry.getKey() + "&6] &a+$" + String.format("%.2f", money) + " &b(+" + xp + " XP)")
                    ));
                    break;
                }
            }
        }
    }
}
