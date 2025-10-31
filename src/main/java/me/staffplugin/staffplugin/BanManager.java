package me.staffplugin.staffplugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class BanManager {

    private StaffPlugin plugin;
    private File bansFile;
    private FileConfiguration bansConfig;

    public BanManager(StaffPlugin plugin) {
        this.plugin = plugin;
        setupBansFile();
    }

    private void setupBansFile() {
        bansFile = new File(plugin.getDataFolder(), "bans.yml");
        if (!bansFile.exists()) {
            try {
                bansFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Erro ao criar bans.yml: " + e.getMessage());
            }
        }
        bansConfig = YamlConfiguration.loadConfiguration(bansFile);
    }

    public boolean banPlayer(Player banner, String targetName, String reason) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            return false;
        }

        UUID targetUUID = target.getUniqueId();
        String bannerName = banner.getName();
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        bansConfig.set("bans." + targetUUID.toString() + ".player", target.getName());
        bansConfig.set("bans." + targetUUID.toString() + ".banner", bannerName);
        bansConfig.set("bans." + targetUUID.toString() + ".reason", reason);
        bansConfig.set("bans." + targetUUID.toString() + ".date", date);
        bansConfig.set("bans." + targetUUID.toString() + ".type", "PERMANENTE");

        saveBansConfig();

        String kickMessage = ChatColor.RED + "Você foi banido permanentemente!\n" +
                ChatColor.YELLOW + "Motivo: " + ChatColor.WHITE + reason + "\n" +
                ChatColor.YELLOW + "Staff: " + ChatColor.WHITE + bannerName + "\n" +
                ChatColor.GRAY + "Data: " + date;

        target.kickPlayer(kickMessage);

        // Anúncio no servidor
        Bukkit.broadcastMessage(ChatColor.RED + "⚡ " + target.getName() +
                " foi banido permanentemente por " + bannerName + "!");
        Bukkit.broadcastMessage(ChatColor.RED + "Motivo: " + reason);

        return true;
    }

    public boolean tempBanPlayer(Player banner, String targetName, String reason, long duration) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            return false;
        }

        UUID targetUUID = target.getUniqueId();
        String bannerName = banner.getName();
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        long unbanTime = System.currentTimeMillis() + duration;

        bansConfig.set("bans." + targetUUID.toString() + ".player", target.getName());
        bansConfig.set("bans." + targetUUID.toString() + ".banner", bannerName);
        bansConfig.set("bans." + targetUUID.toString() + ".reason", reason);
        bansConfig.set("bans." + targetUUID.toString() + ".date", date);
        bansConfig.set("bans." + targetUUID.toString() + ".type", "TEMPORARIO");
        bansConfig.set("bans." + targetUUID.toString() + ".unbanTime", unbanTime);
        bansConfig.set("bans." + targetUUID.toString() + ".duration", duration);

        saveBansConfig();

        String timeFormatted = formatTime(duration);
        String kickMessage = ChatColor.RED + "Você foi banido temporariamente!\n" +
                ChatColor.YELLOW + "Motivo: " + ChatColor.WHITE + reason + "\n" +
                ChatColor.YELLOW + "Staff: " + ChatColor.WHITE + bannerName + "\n" +
                ChatColor.YELLOW + "Duração: " + ChatColor.WHITE + timeFormatted + "\n" +
                ChatColor.GRAY + "Data: " + date;

        target.kickPlayer(kickMessage);

        // Anúncio no servidor
        Bukkit.broadcastMessage(ChatColor.RED + "⚡ " + target.getName() +
                " foi banido temporariamente por " + bannerName + "!");
        Bukkit.broadcastMessage(ChatColor.RED + "Motivo: " + reason +
                " | Duração: " + timeFormatted);

        return true;
    }

    public boolean unbanPlayer(Player unbanner, String targetName) {
        for (String key : bansConfig.getConfigurationSection("bans").getKeys(false)) {
            String playerName = bansConfig.getString("bans." + key + ".player");
            if (playerName.equalsIgnoreCase(targetName)) {
                bansConfig.set("bans." + key, null);
                saveBansConfig();

                Bukkit.broadcastMessage(ChatColor.GREEN + "✅ " + targetName +
                        " foi desbanido por " + unbanner.getName() + "!");
                return true;
            }
        }
        return false;
    }

    public boolean editBan(Player editor, String targetName, String newReason) {
        for (String key : bansConfig.getConfigurationSection("bans").getKeys(false)) {
            String playerName = bansConfig.getString("bans." + key + ".player");
            if (playerName.equalsIgnoreCase(targetName)) {
                bansConfig.set("bans." + key + ".reason", newReason);
                bansConfig.set("bans." + key + ".editor", editor.getName());
                bansConfig.set("bans." + key + ".editDate",
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                saveBansConfig();

                editor.sendMessage(ChatColor.GREEN + "Banimento de " + targetName +
                        " editado com sucesso!");
                return true;
            }
        }
        return false;
    }

    public boolean isBanned(String playerName) {
        for (String key : bansConfig.getConfigurationSection("bans").getKeys(false)) {
            String bannedPlayer = bansConfig.getString("bans." + key + ".player");
            if (bannedPlayer.equalsIgnoreCase(playerName)) {
                String type = bansConfig.getString("bans." + key + ".type");
                if (type.equals("TEMPORARIO")) {
                    long unbanTime = bansConfig.getLong("bans." + key + ".unbanTime");
                    if (System.currentTimeMillis() > unbanTime) {
                        // Banimento expirou
                        bansConfig.set("bans." + key, null);
                        saveBansConfig();
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
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


    public String getBanMessage(String playerName) {
        for (String key : bansConfig.getConfigurationSection("bans").getKeys(false)) {
            String bannedPlayer = bansConfig.getString("bans." + key + ".player");
            if (bannedPlayer.equalsIgnoreCase(playerName)) {
                String type = bansConfig.getString("bans." + key + ".type");
                String reason = bansConfig.getString("bans." + key + ".reason");
                String banner = bansConfig.getString("bans." + key + ".banner");
                String date = bansConfig.getString("bans." + key + ".date");

                if (type.equals("TEMPORARIO")) {
                    long unbanTime = bansConfig.getLong("bans." + key + ".unbanTime");
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > unbanTime) {

                        bansConfig.set("bans." + key, null);
                        saveBansConfig();
                        return null;
                    } else {

                        long remaining = unbanTime - currentTime;
                        String timeFormatted = formatTime(remaining);
                        return ChatColor.RED + "Você está banido temporariamente!\n" +
                                ChatColor.YELLOW + "Motivo: " + ChatColor.WHITE + reason + "\n" +
                                ChatColor.YELLOW + "Staff: " + ChatColor.WHITE + banner + "\n" +
                                ChatColor.YELLOW + "Tempo restante: " + ChatColor.WHITE + timeFormatted + "\n" +
                                ChatColor.GRAY + "Data: " + date;
                    }
                } else {
                    return ChatColor.RED + "Você foi banido permanentemente!\n" +
                            ChatColor.YELLOW + "Motivo: " + ChatColor.WHITE + reason + "\n" +
                            ChatColor.YELLOW + "Staff: " + ChatColor.WHITE + banner + "\n" +
                            ChatColor.GRAY + "Data: " + date;
                }
            }
        }
        return null;
    }
    private void saveBansConfig() {
        try {
            bansConfig.save(bansFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar bans.yml: " + e.getMessage());
        }
    }
}

