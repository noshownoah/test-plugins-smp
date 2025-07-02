package me.vanilla.econ;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LoanManager {
    private final EconomyPlus plugin;
    private final Map<UUID, LoanData> loans = new HashMap<>();
    private final File file;
    private final YamlConfiguration config;

    public LoanManager(EconomyPlus plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "loans.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        loadLoans();
    }

    public LoanData getLoan(UUID uuid) {
        return loans.get(uuid);
    }

    public boolean hasActiveLoan(UUID uuid) {
        LoanData loan = loans.get(uuid);
        return loan != null && !loan.isRepaid();
    }

    public void takeLoan(UUID uuid, double amount, double interest, int dueDays) {
        LoanData loan = new LoanData(amount, interest, System.currentTimeMillis(), dueDays);
        loans.put(uuid, loan);
    }

    public boolean repayLoan(UUID uuid, double amount) {
        LoanData loan = loans.get(uuid);
        if (loan == null || loan.isRepaid()) return false;
        double owed = loan.getTotalOwed();
        if (amount >= owed) {
            loan.markRepaid();
            return true;
        }
        return false;
    }

    public void saveLoans() {
        for (UUID uuid : loans.keySet()) {
            LoanData loan = loans.get(uuid);
            if (loan == null) continue;
            String path = uuid.toString();
            config.set(path + ".principal", loan.getPrincipal());
            config.set(path + ".interest", loan.getInterestRate());
            config.set(path + ".timestamp", loan.getTimestamp());
            config.set(path + ".dueDays", loan.getDueDays());
            config.set(path + ".repaid", loan.isRepaid());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save loans.yml");
        }
    }

    public void loadLoans() {
        for (String uuidStr : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                double principal = config.getDouble(uuidStr + ".principal");
                double interest = config.getDouble(uuidStr + ".interest");
                long time = config.getLong(uuidStr + ".timestamp");
                int due = config.getInt(uuidStr + ".dueDays");
                boolean repaid = config.getBoolean(uuidStr + ".repaid");
                LoanData loan = new LoanData(principal, interest, time, due);
                if (repaid) loan.markRepaid();
                loans.put(uuid, loan);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Invalid entry in loans.yml: " + uuidStr);
            }
        }
    }
}
