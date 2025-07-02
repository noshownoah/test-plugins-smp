package me.vanilla.econ;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LoanCommand implements CommandExecutor {
    private final EconomyPlus plugin;

    public LoanCommand(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        UUID uuid = player.getUniqueId();
        var loans = plugin.getLoanManager();
        var accounts = plugin.getAccountManager();

        if (args.length == 0) {
            player.sendMessage("Usage: /loan <take|repay|status>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "status" -> {
                var loan = loans.getLoan(uuid);
                if (loan == null || loan.isRepaid()) {
                    player.sendMessage("📜 No outstanding loans.");
                } else {
                    player.sendMessage("📋 Loan: $" + loan.getPrincipal() + " at " + (loan.getInterestRate() * 100) + "% interest");
                    player.sendMessage("🗓️ Due in " + loan.getDueDays() + " days");
                    player.sendMessage("💰 Total owed: $" + loan.getTotalOwed());
                }
            }

            case "take" -> {
                if (loans.hasActiveLoan(uuid)) {
                    player.sendMessage("❌ You already have a loan.");
                    return true;
                }

                if (args.length != 2) return false;
                double amount = Double.parseDouble(args[1]);
                double interest = 0.10;
                int dueDays = 7;

                loans.takeLoan(uuid, amount, interest, dueDays);
                accounts.deposit(uuid, amount);
                player.sendMessage("🏛️ Took loan: $" + amount + " at 10% interest due in 7 days.");
            }

            case "repay" -> {
                if (args.length != 2) return false;
                double pay = Double.parseDouble(args[1]);
                if (accounts.withdraw(uuid, pay)) {
                    if (loans.repayLoan(uuid, pay)) {
                        player.sendMessage("✅ Loan fully repaid!");
                    } else {
                        player.sendMessage("🪙 Repayment of $" + pay + " made.");
                    }
                } else {
                    player.sendMessage("❌ Not enough funds.");
                }
            }
        }

        return true;
    }
}
