package me.staffplugin.staffplugin;

import me.staffplugin.staffplugin.Commands.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class StaffPlugin extends JavaPlugin {

    private static StaffPlugin instance;
    private FileConfiguration playersConfig;
    private File playersFile;
    private HashMap<UUID, Boolean> staffChat = new HashMap<>();
    private HashMap<UUID, Boolean> vanished = new HashMap<>();
    private BanManager banManager;

    @Override
    public void onEnable() {
        instance = this;
        setupConfigs();
        this.banManager = new BanManager(this);
        registerCommands();
        registerEvents();
        getLogger().info("StaffPlugin habilitado com sucesso!");
    }

    public BanManager getBanManager() {
        return banManager;
    }

    @Override
    public void onDisable() {
        getLogger().info("StaffPlugin desabilitado!");
    }

    private void setupConfigs() {
        saveDefaultConfig();

        playersFile = new File(getDataFolder(), "players.yml");
        if (!playersFile.exists()) {
            try {
                playersFile.getParentFile().mkdirs();
                playersFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Erro ao criar players.yml: " + e.getMessage());
            }
        }
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    private void registerCommands() {
        // Sistema de Banimento
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("tempban").setExecutor(new TempBanCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));
        getCommand("editban").setExecutor(new EditBanCommand(this));

        // Sistema de Staff
        getCommand("staffchat").setExecutor(new StaffChatCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));

        // Sistema de Ranks
        getCommand("setrank").setExecutor(new RankCommand(this));
        getCommand("addperm").setExecutor(new PermissionCommand(this));

        // Utilitários
        getCommand("clearchat").setExecutor(new ClearChatCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            getLogger().severe("Erro ao salvar players.yml: " + e.getMessage());
        }
    }

    public static StaffPlugin getInstance() {
        return instance;
    }

    public FileConfiguration getPlayersConfig() {
        return playersConfig;
    }

    public HashMap<UUID, Boolean> getStaffChat() {
        return staffChat;
    }

    public HashMap<UUID, Boolean> getVanished() {
        return vanished;
    }
}