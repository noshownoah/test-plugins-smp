package me.vanilla.econ;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class PriceManager {
    private final FileConfiguration config;

    public PriceManager(EconomyPlus plugin) {
        this.config = plugin.getConfig();
    }

    public double getSellPrice(Material material, int quantity) {
        String key = "prices." + material.name();
        if (!config.contains(key)) return 0.0;

        double unitPrice = config.getDouble(key);
        return unitPrice * quantity;
    }
}
