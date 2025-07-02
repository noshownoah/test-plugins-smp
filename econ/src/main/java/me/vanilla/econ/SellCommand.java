package me.vanilla.econ;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellCommand implements CommandExecutor {
    private final EconomyPlus plugin;

    public SellCommand(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            sellAllItems(player);
        } else {
            sellHandItem(player);
        }

        return true;
    }

    private void sellHandItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("❌ You're not holding anything.");
            return;
        }

        int amount = item.getAmount();
        double price = plugin.getPriceManager().getSellPrice(item.getType(), amount);

        if (price <= 0) {
            player.sendMessage("❌ This item can't be sold.");
            return;
        }

        player.getInventory().removeItem(item);
        plugin.getAccountManager().deposit(player.getUniqueId(), price);
        player.sendMessage("💰 Sold " + amount + "x " + item.getType() + " for " + plugin.getAccountManager().format(price));
    }

    private void sellAllItems(Player player) {
        double total = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            double price = plugin.getPriceManager().getSellPrice(item.getType(), item.getAmount());
            if (price <= 0) continue;

            total += price;
            player.getInventory().removeItem(item);
        }

        if (total > 0) {
            plugin.getAccountManager().deposit(player.getUniqueId(), total);
            player.sendMessage("💸 You sold your inventory for " + plugin.getAccountManager().format(total));
        } else {
            player.sendMessage("🪙 Nothing to sell.");
        }
    }
}
