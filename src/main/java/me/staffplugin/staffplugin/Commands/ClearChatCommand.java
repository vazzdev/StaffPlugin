package me.staffplugin.staffplugin.Commands;
import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {

    private StaffPlugin plugin;

    public ClearChatCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staff.clearchat")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("staff.clearchat.bypass")) {
                for (int i = 0; i < 100; i++) {
                    player.sendMessage(" ");
                }
            }
        }

        String staffName = sender instanceof Player ? ((Player) sender).getName() : "Console";
        Bukkit.broadcastMessage(ChatColor.YELLOW + "O chat foi limpo por " + staffName + "!");

        return true;
    }
}