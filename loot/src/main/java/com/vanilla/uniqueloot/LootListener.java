package com.vanilla.uniqueloot;

import me.vanilla.uniqueLoot.LootManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LootListener implements Listener {
    private final com.vanilla.uniqueloot.UniqueLoot plugin;
    private final LootManager lootManager;

    public LootListener(com.vanilla.uniqueloot.UniqueLoot plugin, LootManager lootManager) {
        this.plugin = plugin;
        this.lootManager = lootManager;
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST) return;

        Player player = event.getPlayer();
        Chest chest = (Chest) block.getState();

        if (lootManager.hasOpened(player, chest.getLocation())) return;

        Inventory inv = chest.getBlockInventory();
        inv.clear();

        List<ItemStack> loot = lootManager.generateRandomLoot(chest.getLocation());
        loot.forEach(inv::addItem);

        lootManager.markOpened(player, chest.getLocation());
        player.sendMessage("🎁 You discovered unique loot!");
    }
}