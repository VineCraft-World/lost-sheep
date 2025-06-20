package world.vinecraft.lostsheep.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.EntityUnleashEvent.UnleashReason;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import world.bentobox.bentobox.BentoBox;
import world.vinecraft.lostsheep.LostSheepAddon;

/**
 * This class will name monsters and make they susceptible to the belt of truth
 */
public class LeashListener implements Listener {

    private NamespacedKey key = new NamespacedKey(BentoBox.getInstance(), "belt-of-truth");

    private final LostSheepAddon addon;

    public LeashListener(LostSheepAddon addon) {
        super();
        this.addon = addon;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeash(PlayerLeashEntityEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof LivingEntity le) {
            ItemStack lead = e.getPlayer().getInventory().getItem(e.getHand());
            ItemMeta meta = lead.getItemMeta();
            if (!meta.hasDisplayName() || !meta.getDisplayName().equalsIgnoreCase(addon.getSettings().getBeltName())) {
                return;
            }
            // Belt of truth
            le.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1 // store boolean as byte (1 = true, 0 = false)
            );
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onUnleash(PlayerUnleashEntityEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof LivingEntity le) {
            Byte val = entity.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
            boolean hasBeltOfTruth = val != null && val == 1;
            if (!hasBeltOfTruth) {
                return;
            }
            le.setLeashHolder(null);
            ItemStack belt = makeBelt(new ItemStack(Material.LEAD));
            HashMap<Integer, ItemStack> map = e.getPlayer().getInventory().addItem(belt);
            map.values().forEach(i -> e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), i));
            e.setCancelled(true);
        }
    }

    public ItemStack makeBelt(ItemStack belt) {
        ItemMeta meta = belt.getItemMeta();
        meta.setDisplayName(addon.getSettings().getBeltName());
        meta.setRarity(ItemRarity.EPIC);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Stand firm then,");
        lore.add(ChatColor.GRAY + "with the belt of truth");
        lore.add(ChatColor.GRAY + "buckled around your waist,");
        lore.add(ChatColor.DARK_AQUA + "Protects against lies.");
        lore.add(ChatColor.BLUE + "Symbol of God's Word.");
        lore.add(ChatColor.AQUA + "Unbreakable by deceit.");
        lore.add(ChatColor.GREEN + "Worn by the righteous.");
        lore.add(ChatColor.DARK_GREEN + "Guided by Scripture.");
        lore.add(ChatColor.LIGHT_PURPLE + "Ephesians 6:14");
        meta.setLore(lore);
        belt.setItemMeta(meta);
        return belt;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onUnleash(EntityUnleashEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof LivingEntity) {
            Byte val = entity.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
            boolean hasBeltOfTruth = val != null && val == 1;
            if (!hasBeltOfTruth || e.getReason() == UnleashReason.PLAYER_UNLEASH) {
                return;
            }
            // Wait 1 tick to allow the lead to drop
            Bukkit.getScheduler().runTask(addon.getPlugin(), () -> {
                for (Entity nearby : entity.getNearbyEntities(2, 2, 2)) {
                    if (nearby instanceof Item itemEntity) {
                        ItemStack stack = itemEntity.getItemStack();
                        if (stack.getType() == Material.LEAD) {
                            // We have found the leash
                            stack = makeBelt(stack);
                        }
                    }
                }
            });
        }
    }

}
