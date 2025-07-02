package com.vanilla.uniqueloot;

import me.vanilla.uniqueLoot.LootManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UniqueLoot extends JavaPlugin {
    private LootManager lootManager;

    @Override
    public void onEnable() {
        lootManager = new LootManager(this);
        getServer().getPluginManager().registerEvents(new LootListener(this, lootManager), this);
        getLogger().info("✅ UniqueLoot enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("❌ UniqueLoot disabled.");
    }

    public LootManager getLootManager() {
        return lootManager;
    }
}
