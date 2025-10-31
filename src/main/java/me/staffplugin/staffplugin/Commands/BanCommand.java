package me.staffplugin.staffplugin.Commands;

import me.staffplugin.staffplugin.BanManager;
import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand implements CommandExecutor {

    private StaffPlugin plugin;
    private BanManager banManager;

    public BanCommand(StaffPlugin plugin) {
        this.plugin = plugin;
        this.banManager = new BanManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("staff.ban")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Uso: /ban <jogador> <motivo>");
            return true;
        }

        String targetName = args[0];
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();

        if (banManager.banPlayer(player, targetName, reason)) {
            player.sendMessage(ChatColor.GREEN + "Jogador " + targetName + " banido com sucesso!");
        } else {
            player.sendMessage(ChatColor.RED + "Jogador não encontrado ou offline!");
        }

        return true;
    }
}