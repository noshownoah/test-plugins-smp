package me.vanilla.econ;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BankCommand implements CommandExecutor {
    private final EconomyPlus plugin;

    public BankCommand(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this.");
            return true;
        }

        var accounts = plugin.getAccountManager();
        UUID uuid = player.getUniqueId();

        switch (cmd.getName().toLowerCase()) {
            case "balance" -> {
                double bal = accounts.getBalance(uuid);
                player.sendMessage("💰 Your balance is " + accounts.format(bal));
            }

            case "deposit" -> {
                if (args.length != 1) return false;
                double deposit = Double.parseDouble(args[0]);
                accounts.deposit(uuid, deposit);
                player.sendMessage("🏦 Deposited " + accounts.format(deposit));
            }

            case "withdraw" -> {
                if (args.length != 1) return false;
                double withdraw = Double.parseDouble(args[0]);
                if (accounts.withdraw(uuid, withdraw)) {
                    player.sendMessage("💸 Withdrew " + accounts.format(withdraw));
                } else {
                    player.sendMessage("❌ Not enough funds.");
                }
            }

            case "pay" -> {
                if (args.length != 2) return false;
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null || target.getUniqueId().equals(uuid)) {
                    player.sendMessage("❌ Invalid player.");
                    return true;
                }
                double amount = Double.parseDouble(args[1]);
                if (accounts.transfer(uuid, target.getUniqueId(), amount)) {
                    player.sendMessage("✅ Paid " + accounts.format(amount) + " to " + target.getName());
                    target.sendMessage("💸 Received " + accounts.format(amount) + " from " + player.getName());
                } else {
                    player.sendMessage("❌ Not enough balance.");
                }
            }
        }

        return true;
    }
}