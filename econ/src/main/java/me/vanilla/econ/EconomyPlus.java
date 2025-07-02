package me.vanilla.econ;

import org.bukkit.plugin.java.JavaPlugin;

public class EconomyPlus extends JavaPlugin {
    private AccountManager accountManager;
    private LoanManager loanManager;
    private PriceManager priceManager;
    @Override
    public void onEnable() {
        saveDefaultConfig();

        accountManager = new AccountManager(this);
        loanManager = new LoanManager(this);
        priceManager = new PriceManager(this);

        new CommandManager(this); // already exists
        new BankHUDManager(this).register(); // already exists
        getServer().getPluginManager().registerEvents(new BankNPCListener(this), this); // already exists
        getServer().getPluginManager().registerEvents(new JobEarningsListener(this), this); // ✅ Add this line

        getCommand("sell").setExecutor(new SellCommand(this)); // ✅ Add this too

        getLogger().info("✅ EconomyPlus is live!");
    }
    public PriceManager getPriceManager() {
        return priceManager;
    }
    @Override
    public void onDisable() {
        accountManager.saveAccounts();
        loanManager.saveLoans();
        getLogger().info("💾 EconomyPlus shut down cleanly.");
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public LoanManager getLoanManager() {
        return loanManager;
    }
}
