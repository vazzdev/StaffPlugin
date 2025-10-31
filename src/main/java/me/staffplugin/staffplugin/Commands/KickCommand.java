package me.staffplugin.staffplugin.Commands;

import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {

    private StaffPlugin plugin;

    public KickCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staff.kick")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Uso: /kick <jogador> <motivo>");
            return true;
        }

        String targetName = args[0];
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Jogador não encontrado ou offline!");
            return true;
        }

        String kickerName = sender instanceof Player ? ((Player) sender).getName() : "Console";
        String kickMessage = ChatColor.RED + "Você foi expulso do servidor!\n" +
                ChatColor.YELLOW + "Motivo: " + ChatColor.WHITE + reason + "\n" +
                ChatColor.YELLOW + "Staff: " + ChatColor.WHITE + kickerName;

        target.kickPlayer(kickMessage);

        Bukkit.broadcastMessage(ChatColor.RED + "⚡ " + target.getName() +
                " foi expulso por " + kickerName + "!");
        Bukkit.broadcastMessage(ChatColor.RED + "Motivo: " + reason);

        return true;
    }
}
