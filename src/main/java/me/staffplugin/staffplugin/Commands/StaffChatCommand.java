package me.staffplugin.staffplugin.Commands;

import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {

    private StaffPlugin plugin;

    public StaffChatCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("staff.chat")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length == 0) {
            // Toggle staff chat
            boolean currentState = plugin.getStaffChat().getOrDefault(player.getUniqueId(), false);
            plugin.getStaffChat().put(player.getUniqueId(), !currentState);

            if (!currentState) {
                player.sendMessage(ChatColor.GREEN + "Staff Chat " + ChatColor.YELLOW + "ativado!");
            } else {
                player.sendMessage(ChatColor.RED + "Staff Chat " + ChatColor.YELLOW + "desativado!");
            }
            return true;
        }

        // Enviar mensagem no staff chat
        StringBuilder messageBuilder = new StringBuilder();
        for (String arg : args) {
            messageBuilder.append(arg).append(" ");
        }
        String message = messageBuilder.toString().trim();

        sendStaffMessage(player, message);
        return true;
    }

    public void sendStaffMessage(Player sender, String message) {
        String staffMessage = ChatColor.DARK_RED + "[Staff] " +
                ChatColor.RED + sender.getName() + ChatColor.DARK_GRAY + " » " +
                ChatColor.WHITE + message;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("staff.chat")) {
                player.sendMessage(staffMessage);
            }
        }

        // Log no console também
        Bukkit.getConsoleSender().sendMessage(staffMessage);
    }
}