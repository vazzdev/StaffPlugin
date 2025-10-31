package me.staffplugin.staffplugin.Commands;

import me.staffplugin.staffplugin.BanManager;
import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class  UnbanCommand implements CommandExecutor {

    private StaffPlugin plugin;
    private BanManager banManager;

    public UnbanCommand(StaffPlugin plugin) {
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

        if (!player.hasPermission("staff.unban")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Uso: /unban <jogador>");
            return true;
        }

        String targetName = args[0];

        if (banManager.unbanPlayer(player, targetName)) {
            player.sendMessage(ChatColor.GREEN + "Jogador " + targetName + " desbanido com sucesso!");
        } else {
            player.sendMessage(ChatColor.RED + "Jogador não encontrado na lista de banimentos!");
        }

        return true;
    }
}