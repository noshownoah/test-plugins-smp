package me.vanilla.econ;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.Player;

public class JobEarningsListener implements Listener {
    private final EconomyPlus plugin;

    public JobEarningsListener(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Material mat = event.getBlock().getType();
        double payout = plugin.getConfig().getDouble("jobs.break." + mat.name(), 0.0);
        if (payout > 0) {
            Player player = event.getPlayer();
            plugin.getAccountManager().deposit(player.getUniqueId(), payout);
            player.sendMessage("🛠️ Earned " + plugin.getAccountManager().format(payout));
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;
        Player killer = event.getEntity().getKiller();

        String mob = event.getEntityType().name();
        double payout = plugin.getConfig().getDouble("jobs.kill." + mob, 0.0);
        if (payout > 0) {
            plugin.getAccountManager().deposit(killer.getUniqueId(), payout);
            killer.sendMessage("⚔️ Earned " + plugin.getAccountManager().format(payout));
        }
    }
}