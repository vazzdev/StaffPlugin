package me.staffplugin.staffplugin;

import me.staffplugin.staffplugin.Commands.MuteCommand;
import me.staffplugin.staffplugin.Commands.StaffChatCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private StaffPlugin plugin;
    private MuteCommand muteCommand;
    private BanManager banManager;

    public PlayerListener(StaffPlugin plugin) {
        this.plugin = plugin;
        this.muteCommand = new MuteCommand(plugin);
        this.banManager = new BanManager(plugin);
        this.banManager = plugin.getBanManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Verificar se está banido - CORREÇÃO AQUI
        if (banManager.isBanned(player.getName())) {
            // Obter informações do banimento
            String banInfo = getBanMessage(player.getName());
            if (banInfo != null) {
                player.kickPlayer(banInfo);
                return; // Impedir que o jogador continue no servidor
            }
        }

        // Aplicar vanish para jogadores com permissão
        if (player.hasPermission("staff.vanish.see")) {
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                UUID playerUUID = onlinePlayer.getUniqueId();
                if (plugin.getVanished().getOrDefault(playerUUID, false)) {
                    player.showPlayer(plugin, onlinePlayer);
                }
            }
        }

        // Mensagem de join para staff
        if (player.hasPermission("staff.join.notify")) {
            String joinMessage = ChatColor.GRAY + "[Staff] " + ChatColor.GREEN +
                    player.getName() + " entrou no servidor.";
            for (Player staff : plugin.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("staff.join.notify")) {
                    staff.sendMessage(joinMessage);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Remover do staff chat e vanish ao sair
        plugin.getStaffChat().remove(player.getUniqueId());
        plugin.getVanished().remove(player.getUniqueId());

        // Mensagem de quit para staff
        if (player.hasPermission("staff.join.notify")) {
            String quitMessage = ChatColor.GRAY + "[Staff] " + ChatColor.RED +
                    player.getName() + " saiu do servidor.";
            for (Player staff : plugin.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("staff.join.notify")) {
                    staff.sendMessage(quitMessage);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Verificar se está mutado
        if (muteCommand.isMuted(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Você está silenciado e não pode falar no chat!");
            return;
        }

        // Staff Chat
        if (plugin.getStaffChat().getOrDefault(player.getUniqueId(), false)) {
            event.setCancelled(true);
            StaffChatCommand staffChat = new StaffChatCommand(plugin);
            staffChat.sendStaffMessage(player, event.getMessage());
            return;
        }

        // Formatação de chat baseada no rank
        String rank = plugin.getPlayersConfig().getString("players." + player.getUniqueId() + ".rank", "default");
        String format = getChatFormat(rank, player);

        if (format != null) {
            event.setFormat(format.replace("%player%", player.getName()).replace("%message%", event.getMessage()));
        }
    }

    /**
     * Método para obter a mensagem de banimento formatada
     */
    private String getBanMessage(String playerName) {
        // Aqui você precisaria acessar os dados do banimento do BanManager
        // Vou criar um método auxiliar no BanManager para isso
        return banManager.getBanMessage(playerName);
    }

    private String getChatFormat(String rank, Player player) {
        switch (rank) {
            case "admin":
                return ChatColor.DARK_RED + "[ADMIN] " + ChatColor.RED + "%player% » " + ChatColor.WHITE + "%message%";
            case "mod":
                return ChatColor.DARK_GREEN + "[MOD] " + ChatColor.GREEN + "%player% » " + ChatColor.WHITE + "%message%";
            case "mod+":
                return ChatColor.GREEN + "[MOD+] " + ChatColor.AQUA + "%player% » " + ChatColor.WHITE + "%message%";
            case "trial":
                return ChatColor.BLUE + "[TRIAL] " + ChatColor.AQUA + "%player% » " + ChatColor.WHITE + "%message%";
            case "helper":
                return ChatColor.YELLOW + "[HELPER] " + ChatColor.GOLD + "%player% » " + ChatColor.WHITE + "%message%";
            case "builder":
                return ChatColor.DARK_AQUA + "[BUILDER] " + ChatColor.AQUA + "%player% » " + ChatColor.WHITE + "%message%";
            case "youtuber":
                return ChatColor.RED + "[YT] " + ChatColor.WHITE + "%player% » " + ChatColor.GRAY + "%message%";
            case "youtuber+":
                return ChatColor.RED + "[YT+] " + ChatColor.WHITE + "%player% » " + ChatColor.GRAY + "%message%";
            case "vip_volt":
                return ChatColor.GOLD + "[VIP VOLT] " + ChatColor.YELLOW + "%player% » " + ChatColor.WHITE + "%message%";
            case "vip":
                return ChatColor.GREEN + "[VIP] " + ChatColor.AQUA + "%player% » " + ChatColor.WHITE + "%message%";
            case "vip_eternal":
                return ChatColor.LIGHT_PURPLE + "[VIP ETERNAL] " + ChatColor.DARK_PURPLE + "%player% » " + ChatColor.WHITE + "%message%";
            case "vip_mvp":
                return ChatColor.AQUA + "[VIP MVP] " + ChatColor.BLUE + "%player% » " + ChatColor.WHITE + "%message%";
            default:
                return ChatColor.GRAY + "%player% » " + ChatColor.WHITE + "%message%";
        }
    }
}