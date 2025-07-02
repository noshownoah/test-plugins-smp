package me.vanilla.econ;


import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class AccountManager {
    private final EconomyPlus plugin;
    private final Map<UUID, Double> accounts = new HashMap<>();
    private final File dataFile;
    private final YamlConfiguration config;
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");

    public AccountManager(EconomyPlus plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "accounts.yml");
        this.config = YamlConfiguration.loadConfiguration(dataFile);
        loadAccounts();
    }

    public double getBalance(UUID uuid) {
        return accounts.getOrDefault(uuid, 0.0);
    }

    public String format(double amount) {
        return "$" + moneyFormat.format(amount);
    }

    public void setBalance(UUID uuid, double amount) {
        accounts.put(uuid, Math.max(0.0, amount));
    }

    public void deposit(UUID uuid, double amount) {
        accounts.put(uuid, getBalance(uuid) + amount);
    }

    public boolean withdraw(UUID uuid, double amount) {
        double current = getBalance(uuid);
        if (current >= amount) {
            accounts.put(uuid, current - amount);
            return true;
        }
        return false;
    }

    public boolean transfer(UUID from, UUID to, double amount) {
        if (withdraw(from, amount)) {
            deposit(to, amount);
            return true;
        }
        return false;
    }

    public void loadAccounts() {
        if (!dataFile.exists()) return;

        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double balance = config.getDouble(key);
                accounts.put(uuid, balance);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Invalid UUID in accounts.yml: " + key);
            }
        }
    }

    public void saveAccounts() {
        for (Map.Entry<UUID, Double> entry : accounts.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(dataFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save accounts.yml");
            e.printStackTrace();
        }
    }

    public Set<UUID> getAllAccountHolders() {
        return accounts.keySet();
    }

    public OfflinePlayer getPlayer(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid);
    }
}
