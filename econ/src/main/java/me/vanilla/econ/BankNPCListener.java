package me.vanilla.econ;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BankNPCListener implements Listener {
    private final EconomyPlus plugin;

    public BankNPCListener(EconomyPlus plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new BankGUI(plugin), plugin);
    }

    @EventHandler
    public void onBankerClick(NPCRightClickEvent event) {
        if (!event.getNPC().getName().equalsIgnoreCase("Banker")) return;
        new BankGUI(plugin).open(event.getClicker());
    }
}