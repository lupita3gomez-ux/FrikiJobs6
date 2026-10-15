package com.frikifest.frikijobs.manager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {
    private Economy economy;

    public boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public void depositPlayer(OfflinePlayer player, double amount) {
        if (economy != null && amount > 0) {
            economy.depositPlayer(player, amount);
        }
    }

    public void withdrawPlayer(OfflinePlayer player, double amount) {
        if (economy != null && amount > 0) {
            economy.withdrawPlayer(player, amount);
        }
    }

    public double getBalance(OfflinePlayer player) {
        if (economy != null) {
            return economy.getBalance(player);
        }
        return 0.0;
    }

    public boolean hasAccount(OfflinePlayer player) {
        if (economy != null) {
            return economy.hasAccount(player);
        }
        return true;
    }
}
