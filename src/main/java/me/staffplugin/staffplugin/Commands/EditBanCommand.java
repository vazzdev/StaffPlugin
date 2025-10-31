package me.staffplugin.staffplugin.Commands;

import me.staffplugin.staffplugin.BanManager;
import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditBanCommand implements CommandExecutor {

    private StaffPlugin plugin;
    private BanManager banManager;

    public EditBanCommand(StaffPlugin plugin) {
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

        if (!player.hasPermission("staff.editban")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Uso: /editban <jogador> <novo motivo>");
            return true;
        }

        String targetName = args[0];
        StringBuilder newReasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            newReasonBuilder.append(args[i]).append(" ");
        }
        String newReason = newReasonBuilder.toString().trim();

        if (banManager.editBan(player, targetName, newReason)) {
            player.sendMessage(ChatColor.GREEN + "Motivo do banimento de " + targetName + " atualizado!");
        } else {
            player.sendMessage(ChatColor.RED + "Jogador não encontrado na lista de banimentos!");
        }

        return true;
    }
}