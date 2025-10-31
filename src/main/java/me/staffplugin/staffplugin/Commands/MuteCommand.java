package me.staffplugin.staffplugin.Commands;
import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MuteCommand implements CommandExecutor {

    private StaffPlugin plugin;
    private HashMap<UUID, Long> mutedPlayers = new HashMap<>();

    public MuteCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staff.mute")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Uso: /mute <jogador> <tempo> [motivo]");
            sender.sendMessage(ChatColor.YELLOW + "Exemplo: /mute John 1h Spam no chat");
            return true;
        }

        String targetName = args[0];
        String timeString = args[1];

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.length() > 0 ? reasonBuilder.toString().trim() : "Sem motivo";

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Jogador não encontrado ou offline!");
            return true;
        }

        long duration = parseTime(timeString);
        if (duration == -1) {
            sender.sendMessage(ChatColor.RED + "Formato de tempo inválido!");
            return true;
        }

        long unmuteTime = System.currentTimeMillis() + duration;
        mutedPlayers.put(target.getUniqueId(), unmuteTime);

        String staffName = sender instanceof Player ? ((Player) sender).getName() : "Console";
        String timeFormatted = formatTime(duration);

        target.sendMessage(ChatColor.RED + "Você foi silenciado por " + timeFormatted + "!");
        target.sendMessage(ChatColor.RED + "Motivo: " + reason);
        target.sendMessage(ChatColor.RED + "Staff: " + staffName);

        Bukkit.broadcastMessage(ChatColor.RED + "🔇 " + target.getName() +
                " foi silenciado por " + staffName + " por " + timeFormatted + "!");
        Bukkit.broadcastMessage(ChatColor.RED + "Motivo: " + reason);

        return true;
    }

    public boolean isMuted(Player player) {
        if (!mutedPlayers.containsKey(player.getUniqueId())) {
            return false;
        }

        long unmuteTime = mutedPlayers.get(player.getUniqueId());
        if (System.currentTimeMillis() > unmuteTime) {
            mutedPlayers.remove(player.getUniqueId());
            return false;
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

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " dia" + (days > 1 ? "s" : "");
        } else if (hours > 0) {
            return hours + " hora" + (hours > 1 ? "s" : "");
        } else if (minutes > 0) {
            return minutes + " minuto" + (minutes > 1 ? "s" : "");
        } else {
            return seconds + " segundo" + (seconds > 1 ? "s" : "");
        }
    }
}