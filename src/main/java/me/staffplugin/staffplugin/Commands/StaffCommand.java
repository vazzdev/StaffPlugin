package me.staffplugin.staffplugin.Commands;

import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffCommand implements CommandExecutor {

    private StaffPlugin plugin;

    public StaffCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("staff.help")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        // Menu de ajuda da staff
        player.sendMessage(ChatColor.GOLD + "=== Comandos da Staff ===");
        player.sendMessage(ChatColor.YELLOW + "/ban <jogador> <motivo> " + ChatColor.GRAY + "- Banir permanentemente");
        player.sendMessage(ChatColor.YELLOW + "/tempban <jogador> <tempo> <motivo> " + ChatColor.GRAY + "- Banir temporariamente");
        player.sendMessage(ChatColor.YELLOW + "/unban <jogador> " + ChatColor.GRAY + "- Desbanir jogador");
        player.sendMessage(ChatColor.YELLOW + "/editban <jogador> <novo motivo> " + ChatColor.GRAY + "- Editar banimento");
        player.sendMessage(ChatColor.YELLOW + "/kick <jogador> <motivo> " + ChatColor.GRAY + "- Expulsar jogador");
        player.sendMessage(ChatColor.YELLOW + "/mute <jogador> <tempo> [motivo] " + ChatColor.GRAY + "- Silenciar jogador");
        player.sendMessage(ChatColor.YELLOW + "/staffchat [mensagem] " + ChatColor.GRAY + "- Chat da equipe");
        player.sendMessage(ChatColor.YELLOW + "/vanish " + ChatColor.GRAY + "- Modo invisível");
        player.sendMessage(ChatColor.YELLOW + "/clearchat " + ChatColor.GRAY + "- Limpar chat");
        player.sendMessage(ChatColor.YELLOW + "/setrank <jogador> <cargo> " + ChatColor.GRAY + "- Definir cargo");
        player.sendMessage(ChatColor.YELLOW + "/addperm <jogador> <add/remove> <permissão> " + ChatColor.GRAY + "- Gerenciar permissões");

        return true;
    }
}