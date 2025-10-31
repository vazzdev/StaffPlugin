package me.staffplugin.staffplugin.Commands;

import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    private StaffPlugin plugin;

    public VanishCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("staff.vanish")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        boolean currentState = plugin.getVanished().getOrDefault(player.getUniqueId(), false);

        if (currentState) {
            // Aparecer
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                online.showPlayer(plugin, player);
            }
            plugin.getVanished().put(player.getUniqueId(), false);
            player.sendMessage(ChatColor.GREEN + "Vanish desativado!");
        } else {
            // Desaparecer
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                if (!online.hasPermission("staff.vanish.see")) {
                    online.hidePlayer(plugin, player);
                }
            }
            plugin.getVanished().put(player.getUniqueId(), true);
            player.sendMessage(ChatColor.GREEN + "Vanish ativado!");
        }

        return true;
    }
}