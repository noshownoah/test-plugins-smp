package me.vanilla.econ;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class BankGUI implements Listener {
    private final EconomyPlus plugin;

    public BankGUI(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "🏦 Bank Menu");

        gui.setItem(0, makeItem(Material.GOLD_INGOT, "Deposit $100"));
        gui.setItem(1, makeItem(Material.EMERALD, "Withdraw $100"));
        gui.setItem(3, makeItem(Material.PAPER, "Loan Status"));
        gui.setItem(4, makeItem(Material.BOOK, "Take Loan"));
        gui.setItem(5, makeItem(Material.EXPERIENCE_BOTTLE, "Repay $100"));
        gui.setItem(8, makeItem(Material.NAME_TAG, "Balance"));

        player.openInventory(gui);
    }

    private ItemStack makeItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("🏦 Bank Menu")) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();

        var accounts = plugin.getAccountManager();
        var loans = plugin.getLoanManager();

        switch (event.getSlot()) {
            case 0 -> {
                accounts.withdraw(uuid, 100);
                player.sendMessage("💰 Deposited $100");
            }
            case 1 -> {
                accounts.deposit(uuid, 100);
                player.sendMessage("💵 Withdrew $100");
            }
            case 3 -> {
                var loan = loans.getLoan(uuid);
                if (loan == null || loan.isRepaid()) {
                    player.sendMessage("📜 No active loans.");
                } else {
                    player.sendMessage("💳 You owe $" + loan.getTotalOwed() +
                            " (" + (loan.getInterestRate() * 100) + "% interest, " + loan.getDueDays() + " days due)");
                }
            }
            case 4 -> {
                if (loans.hasActiveLoan(uuid)) {
                    player.sendMessage("❌ You already have a loan!");
                } else {
                    double amount = 500;
                    double interest = 0.1;
                    int days = 7;
                    loans.takeLoan(uuid, amount, interest, days);
                    accounts.deposit(uuid, amount);
                    player.sendMessage("🏛️ Loan taken: $500 at 10% interest, due in 7 days.");
                }
            }
            case 5 -> {
                double repay = 100;
                if (accounts.withdraw(uuid, repay)) {
                    loans.repayLoan(uuid, repay);
                    player.sendMessage("✅ Repaid $" + repay + " toward your loan.");
                } else {
                    player.sendMessage("❌ Not enough funds to repay.");
                }
            }
            case 8 -> {
                player.sendMessage("💼 Balance: " + accounts.format(accounts.getBalance(uuid)));
            }
        }

        player.closeInventory();
    }
}
