package me.vanilla.uniqueLoot;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;
import java.util.stream.Collectors;

public class LootManager {
    private final com.vanilla.uniqueloot.UniqueLoot plugin;
    private final Map<UUID, Set<Location>> openedChests = new HashMap<>();
    private final Random random = new Random();

    private final List<StructureType> supportedStructures = Arrays.asList(
            StructureType.JUNGLE_PYRAMID,
            StructureType.DESERT_PYRAMID,
            StructureType.MINESHAFT,
            StructureType.STRONGHOLD,
            StructureType.OCEAN_RUIN,
            StructureType.NETHER_FORTRESS,
            StructureType.VILLAGE
            // We handle ancient_city as a fallback
    );

    public LootManager(com.vanilla.uniqueloot.UniqueLoot plugin) {
        this.plugin = plugin;
    }

    public boolean hasOpened(Player player, Location chestLoc) {
        return openedChests
                .getOrDefault(player.getUniqueId(), Collections.emptySet())
                .contains(chestLoc);
    }

    public void markOpened(Player player, Location chestLoc) {
        openedChests
                .computeIfAbsent(player.getUniqueId(), k -> new HashSet<>())
                .add(chestLoc);
    }

    public List<ItemStack> generateRandomLoot(Location chestLoc) {
        String structureKey = detectStructureKey(chestLoc);
        List<Material> lootPool = getLootPoolForKey(structureKey);

        Collections.shuffle(lootPool);
        List<ItemStack> loot = new ArrayList<>();
        int itemCount = 3 + random.nextInt(4); // 3–6 items

        for (int i = 0; i < itemCount && i < lootPool.size(); i++) {
            Material mat = lootPool.get(i);
            int maxAmount = Math.max(1, mat.getMaxStackSize() / 4);
            int amount = 1 + random.nextInt(maxAmount);
            ItemStack item = new ItemStack(mat, Math.min(amount, mat.getMaxStackSize()));

            if (isEnchantable(mat) && random.nextDouble() < 0.3) {
                item = applyRandomEnchantments(item);
            } else if (mat == Material.BOOK && random.nextDouble() < 0.3) {
                item = convertToEnchantedBook();
            }

            loot.add(item);
        }

        return loot;
    }

    private String detectStructureKey(Location loc) {
        World world = loc.getWorld();
        if (world == null) return "village";

        for (StructureType type : supportedStructures) {
            if (world.locateNearestStructure(loc, type, 16, false) != null) {
                return type.getKey().getKey(); // e.g., "jungle_pyramid"
            }
        }

        // Fallback to ancient_city if others didn't match
        return "ancient_city";
    }

    private List<Material> getLootPoolForKey(String structureKey) {
        switch (structureKey) {
            case "jungle_pyramid":
                return Arrays.asList(Material.BAMBOO, Material.MOSSY_COBBLESTONE, Material.EMERALD,
                        Material.TRIPWIRE_HOOK, Material.GOLDEN_SWORD, Material.IRON_PICKAXE);
            case "desert_pyramid":
                return Arrays.asList(Material.SAND, Material.TNT, Material.GOLD_INGOT,
                        Material.ENCHANTED_BOOK, Material.ROTTEN_FLESH, Material.IRON_INGOT);
            case "mineshaft":
                return Arrays.asList(Material.RAIL, Material.COAL, Material.IRON_INGOT,
                        Material.NAME_TAG, Material.GOLDEN_APPLE, Material.STONE_PICKAXE);
            case "stronghold":
                return Arrays.asList(Material.BOOK, Material.PAPER, Material.ENCHANTED_BOOK,
                        Material.COMPASS, Material.MAP);
            case "ocean_ruin":
                return Arrays.asList(Material.KELP, Material.PRISMARINE_SHARD, Material.FISHING_ROD,
                        Material.COD, Material.NAUTILUS_SHELL);
            case "nether_fortress":
                return Arrays.asList(Material.NETHER_WART, Material.BLAZE_ROD, Material.GOLD_INGOT,
                        Material.BONE, Material.IRON_SWORD);
            case "ancient_city":
                return Arrays.asList(Material.ECHO_SHARD, Material.SCULK, Material.EXPERIENCE_BOTTLE,
                        Material.ENCHANTED_BOOK, Material.DIAMOND_HOE, Material.HONEY_BOTTLE);
            default:
                return getGenericSurvivalItems();
        }
    }

    private List<Material> getGenericSurvivalItems() {
        return Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(mat -> mat.getMaxStackSize() > 0)
                .filter(mat -> !mat.name().contains("SPAWN_EGG"))
                .filter(mat -> !mat.name().contains("COMMAND") && !mat.name().contains("BARRIER") && !mat.name().contains("DEBUG"))
                .filter(mat -> !mat.name().contains("STRUCTURE") && !mat.name().contains("LIGHT"))
                .collect(Collectors.toList());
    }

    private boolean isEnchantable(Material mat) {
        return mat.name().endsWith("_SWORD") ||
                mat.name().endsWith("_AXE") ||
                mat.name().endsWith("_PICKAXE") ||
                mat.name().endsWith("_SHOVEL") ||
                mat.name().endsWith("_HOE") ||
                mat.name().endsWith("_HELMET") ||
                mat.name().endsWith("_CHESTPLATE") ||
                mat.name().endsWith("_LEGGINGS") ||
                mat.name().endsWith("_BOOTS") ||
                mat == Material.BOW || mat == Material.CROSSBOW || mat == Material.FISHING_ROD;
    }

    private ItemStack applyRandomEnchantments(ItemStack item) {
        int enchantCount = 1 + random.nextInt(2);

        for (int i = 0; i < enchantCount; i++) {
            Enchantment enchant = getRandomCompatibleEnchantment(item);
            if (enchant != null) {
                int level = 1 + random.nextInt(enchant.getMaxLevel());
                item.addEnchantment(enchant, level);
            }
        }

        return item;
    }

    private Enchantment getRandomCompatibleEnchantment(ItemStack item) {
        List<Enchantment> compatible = Arrays.stream(Enchantment.values())
                .filter(e -> e.canEnchantItem(item))
                .collect(Collectors.toList());

        if (compatible.isEmpty()) return null;
        return compatible.get(random.nextInt(compatible.size()));
    }

    private ItemStack convertToEnchantedBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        Enchantment enchant = Enchantment.values()[random.nextInt(Enchantment.values().length)];
        int level = 1 + random.nextInt(enchant.getMaxLevel());
        meta.addStoredEnchant(enchant, level, true);
        book.setItemMeta(meta);
        return book;
    }
}