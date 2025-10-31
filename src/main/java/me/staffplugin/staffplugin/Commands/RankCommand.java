package me.staffplugin.staffplugin.Commands;

import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class RankCommand implements CommandExecutor {

    private StaffPlugin plugin;
    private List<String> availableRanks = Arrays.asList(
            "admin", "mod", "mod+", "trial", "helper", "builder",
            "youtuber", "youtuber+", "vip_volt", "vip", "vip_eternal", "vip_mvp"
    );

    public RankCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staff.setrank")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Uso: /setrank <jogador> <cargo>");
            sender.sendMessage(ChatColor.YELLOW + "Cargos disponíveis: " + String.join(", ", availableRanks));
            return true;
        }

        String targetName = args[0];
        String rank = args[1].toLowerCase();

        if (!availableRanks.contains(rank)) {
            sender.sendMessage(ChatColor.RED + "Cargo inválido! Cargos disponíveis:");
            sender.sendMessage(ChatColor.YELLOW + String.join(", ", availableRanks));
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Jogador não encontrado ou offline!");
            return true;
        }

        // Salvar no arquivo de configuração
        plugin.getPlayersConfig().set("players." + target.getUniqueId() + ".rank", rank);
        plugin.getPlayersConfig().set("players." + target.getUniqueId() + ".name", target.getName());
        plugin.savePlayersConfig();

        // Atualizar permissões (você precisaria integrar com um sistema de permissões como LuckPerms)
        updatePermissions(target, rank);

        sender.sendMessage(ChatColor.GREEN + "Cargo de " + target.getName() +
                " definido para " + rank.toUpperCase() + "!");

        target.sendMessage(ChatColor.GREEN + "Seu cargo foi atualizado para " +
                rank.toUpperCase() + "!");

        return true;
    }

    private void updatePermissions(Player player, String rank) {
       //integra api do luck perms
    }
}