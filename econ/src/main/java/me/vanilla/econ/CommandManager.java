package me.vanilla.econ;

public class CommandManager {
    public CommandManager(EconomyPlus plugin) {
        plugin.getCommand("balance").setExecutor(new BankCommand(plugin));
        plugin.getCommand("pay").setExecutor(new BankCommand(plugin));
        plugin.getCommand("deposit").setExecutor(new BankCommand(plugin));
        plugin.getCommand("withdraw").setExecutor(new BankCommand(plugin));
        plugin.getCommand("loan").setExecutor(new LoanCommand(plugin));

    }
}
