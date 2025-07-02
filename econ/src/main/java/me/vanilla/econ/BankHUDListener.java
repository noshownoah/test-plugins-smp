package me.vanilla.econ;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

class BankHUDManager implements Listener {
    private final EconomyPlus plugin;

    public BankHUDManager(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                showSidebar(p);
            }
        }, 0L, 100L); // 5 seconds
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        showSidebar(event.getPlayer());
    }

    private void showSidebar(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("bankstatus", "dummy", "🏦 Bank Status");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        var accounts = plugin.getAccountManager();
        var loans = plugin.getLoanManager();
        var uuid = player.getUniqueId();

        int score = 6;
        obj.getScore(" ").setScore(score--);
        obj.getScore("💼 Balance: " + accounts.format(accounts.getBalance(uuid))).setScore(score--);

        var loan = loans.getLoan(uuid);
        if (loan != null && !loan.isRepaid()) {
            obj.getScore("💳 Loan: $" + loan.getPrincipal()).setScore(score--);
            obj.getScore("💸 Owed: $" + loan.getTotalOwed()).setScore(score--);
            obj.getScore("📅 Due: " + loan.getDueDays() + " days").setScore(score--);
        } else {
            obj.getScore("✅ No active loan").setScore(score--);
        }

        obj.getScore(" ").setScore(score);
        player.setScoreboard(board);
    }
}
