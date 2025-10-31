package me.staffplugin.staffplugin.Commands;
import me.staffplugin.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PermissionCommand implements CommandExecutor {

    private StaffPlugin plugin;

    public PermissionCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staff.addperm")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Uso: /addperm <jogador> <add/remove> <permissão>");
            return true;
        }

        String targetName = args[0];
        String action = args[1].toLowerCase();
        String permission = args[2];

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Jogador não encontrado ou offline!");
            return true;
        }

        if (!action.equals("add") && !action.equals("remove")) {
            sender.sendMessage(ChatColor.RED + "Ação deve ser 'add' ou 'remove'!");
            return true;
        }

        // Salvar no arquivo de configuração
        String path = "players." + target.getUniqueId() + ".permissions";
        List<String> permissions = plugin.getPlayersConfig().getStringList(path);

        if (permissions == null) {
            permissions = new ArrayList<>();
        }

        if (action.equals("add")) {
            if (!permissions.contains(permission)) {
                permissions.add(permission);
                sender.sendMessage(ChatColor.GREEN + "Permissão " + permission +
                        " adicionada para " + target.getName() + "!");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Jogador já possui esta permissão!");
            }
        } else {
            if (permissions.contains(permission)) {
                permissions.remove(permission);
                sender.sendMessage(ChatColor.GREEN + "Permissão " + permission +
                        " removida de " + target.getName() + "!");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Jogador não possui esta permissão!");
            }
        }

        plugin.getPlayersConfig().set(path, permissions);
        plugin.savePlayersConfig();

        return true;
    }
}
