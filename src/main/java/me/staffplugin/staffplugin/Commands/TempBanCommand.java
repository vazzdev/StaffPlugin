package me.staffplugin.staffplugin.Commands;

import me.staffplugin.staffplugin.BanManager;
import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempBanCommand implements CommandExecutor {

    private StaffPlugin plugin;
    private BanManager banManager;

    public TempBanCommand(StaffPlugin plugin) {
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

        if (!player.hasPermission("staff.tempban")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Uso: /tempban <jogador> <tempo> <motivo>");
            player.sendMessage(ChatColor.YELLOW + "Exemplo: /tempban John 7d Hacking");
            player.sendMessage(ChatColor.YELLOW + "Formatos de tempo: s (segundos), m (minutos), h (horas), d (dias)");
            return true;
        }

        String targetName = args[0];
        String timeString = args[1];

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();

        long duration = parseTime(timeString);
        if (duration == -1) {
            player.sendMessage(ChatColor.RED + "Formato de tempo inválido!");
            player.sendMessage(ChatColor.YELLOW + "Exemplos: 30m, 2h, 7d");
            return true;
        }

        if (banManager.tempBanPlayer(player, targetName, reason, duration)) {
            player.sendMessage(ChatColor.GREEN + "Jogador " + targetName + " banido temporariamente!");
        } else {
            player.sendMessage(ChatColor.RED + "Jogador não encontrado ou offline!");
        }

        return true;
    }

    private long parseTime(String timeString) {
        try {
            char unit = timeString.charAt(timeString.length() - 1);
            long number = Long.parseLong(timeString.substring(0, timeString.length() - 1));

            switch (unit) {
                case 's': return number * 1000;
                case 'm': return number * 60 * 1000;
                case 'h': return number * 60 * 60 * 1000;
                case 'd': return number * 24 * 60 * 60 * 1000;
                default: return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }
}