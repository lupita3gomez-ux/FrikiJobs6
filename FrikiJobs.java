package com.frikifest.frikijobs;

import com.frikifest.frikijobs.gui.JobsGUI;
import com.frikifest.frikijobs.listener.GUIListener;
import com.frikifest.frikijobs.listener.JobActionListener;
import com.frikifest.frikijobs.manager.EconomyManager;
import com.frikifest.frikijobs.manager.JobManager;
import com.frikifest.frikijobs.manager.PlayerJobManager;
import com.frikifest.frikijobs.model.Job;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class FrikiJobs extends JavaPlugin {

    private static FrikiJobs instance;
    private JobManager jobManager;
    private EconomyManager economyManager;
    private PlayerJobManager playerJobManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.economyManager = new EconomyManager();
        if (!economyManager.setupEconomy()) {
            getLogger().warning("Vault no encontrado. La integración económica se deshabilitó.");
        }

        this.jobManager = new JobManager(this);
        jobManager.loadJobs();

        this.playerJobManager = new PlayerJobManager(this);

        getServer().getPluginManager().registerEvents(new JobActionListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        getLogger().info("FrikiJobs inicializado correctamente con subcomando /jobs reload y nombre actualizado.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("jobs")) {
            if (args.length > 0) {
                String subCommand = args[0].toLowerCase();

                // Subcomando /jobs reload
                if (subCommand.equals("reload")) {
                    if (!sender.hasPermission("jobs.admin")) {
                        sender.sendMessage(ChatColor.RED + "No tienes permiso para recargar la configuración de FrikiJobs.");
                        return true;
                    }

                    reloadConfig();
                    jobManager.loadJobs();
                    sender.sendMessage(ChatColor.GREEN + "[FrikiJobs] Configuración y empleos recargados correctamente.");
                    return true;
                }

                // Subcomandos administrativos /jobs set y /jobs remove
                if (!sender.hasPermission("jobs.admin")) {
                    sender.sendMessage(ChatColor.RED + "No tienes permiso para ejecutar comandos administrativos de trabajos.");
                    return true;
                }

                if (subCommand.equals("set")) {
                    if (args.length < 3) {
                        sender.sendMessage(ChatColor.RED + "Uso correcto: /jobs set <jugador> <trabajo>");
                        return true;
                    }

                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
                        sender.sendMessage(ChatColor.RED + "El jugador '" + args[1] + "' no ha sido encontrado.");
                        return true;
                    }

                    String jobId = args[2].toLowerCase();
                    Job job = jobManager.getJob(jobId);
                    if (job == null) {
                        sender.sendMessage(ChatColor.RED + "El trabajo '" + args[2] + "' no existe. Trabajos válidos: minero, granjero, hechicero, pescador, guerrero, lenador, alquimista.");
                        return true;
                    }

                    playerJobManager.setPlayerJob(target.getUniqueId(), jobId);
                    sender.sendMessage(ChatColor.GREEN + "[FrikiJobs] Se ha asignado el trabajo de " + job.getName() + " a " + target.getName() + " correctamente.");

                    if (target.isOnline()) {
                        ((Player) target).sendMessage(ChatColor.GOLD + "[FrikiJobs] Un administrador te ha asignado el trabajo de " + job.getName() + ".");
                    }
                    return true;
                }

                if (subCommand.equals("remove") || subCommand.equals("clear")) {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Uso correcto: /jobs remove <jugador>");
                        return true;
                    }

                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
                        sender.sendMessage(ChatColor.RED + "El jugador '" + args[1] + "' no ha sido encontrado.");
                        return true;
                    }

                    String currentJob = playerJobManager.getPlayerJob(target.getUniqueId());
                    if (currentJob == null) {
                        sender.sendMessage(ChatColor.YELLOW + "[FrikiJobs] El jugador " + target.getName() + " no tiene ningún trabajo asignado.");
                        return true;
                    }

                    playerJobManager.setPlayerJob(target.getUniqueId(), null);
                    sender.sendMessage(ChatColor.GREEN + "[FrikiJobs] Se le ha retirado el trabajo a " + target.getName() + " correctamente.");

                    if (target.isOnline()) {
                        ((Player) target).sendMessage(ChatColor.RED + "[FrikiJobs] Un administrador te ha retirado tu trabajo actual.");
                    }
                    return true;
                }

                sender.sendMessage(ChatColor.RED + "Subcomando desconocido. Usos administrativos:");
                sender.sendMessage(ChatColor.YELLOW + "/jobs reload");
                sender.sendMessage(ChatColor.YELLOW + "/jobs set <jugador> <trabajo>");
                sender.sendMessage(ChatColor.YELLOW + "/jobs remove <jugador>");
                return true;
            }

            if (sender instanceof Player player) {
                JobsGUI.openJobsMenu(player);
                return true;
            } else {
                sender.sendMessage("Uso en consola: /jobs reload | /jobs set <jugador> <trabajo> | /jobs remove <jugador>");
            }
        }
        return false;
    }

    public static FrikiJobs getInstance() {
        return instance;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public PlayerJobManager getPlayerJobManager() {
        return playerJobManager;
    }
}
