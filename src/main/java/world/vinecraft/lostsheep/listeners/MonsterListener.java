package world.vinecraft.lostsheep.listeners;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import world.bentobox.bentobox.BentoBox;
import world.vinecraft.lostsheep.LostSheepAddon;

/**
 * This class will name monsters and make they susceptible to the belt of truth
 */
public class MonsterListener implements Listener {

    private NamespacedKey key = new NamespacedKey(BentoBox.getInstance(), "belt-of-truth");

    private final LostSheepAddon addon;

    public MonsterListener(LostSheepAddon addon) {
        super();
        this.addon = addon;
    }

    /**
     * Only show lies when monsters are nearby
     * @param e event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent e) {
        // Get entities around player
        double range = addon.getSettings().getNameRange();
        e.getPlayer().getNearbyEntities(range, range, range).stream().filter(en -> en instanceof Monster)
                .filter(en -> en.getCustomName() == null).forEach(m -> {
            // We have a monster
            String lie = addon.getSettings().getLies().get(new Random().nextInt(addon.getSettings().getLies().size()));
            m.setCustomName(lie);
            m.setCustomNameVisible(true);
                });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player) {
            // Only proceed if player is holding a lead
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() != Material.LEAD)
                return;

            // Check if the lead has the right name
            ItemMeta meta = item.getItemMeta();
            if (!meta.hasDisplayName() || !meta.getDisplayName().equalsIgnoreCase(addon.getSettings().getBeltName())) {
                return;
            }
            // check if it's a hostile mob
            if (!(e instanceof Monster mob))
                return;

            mob.setFireTicks(60);
            mob.getWorld().playSound(mob, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1F, 2F);
            mob.damage(10, player);
            e.setCancelled(true);
        }
    }

    /**
     * Handle using the leash
     * @param event event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRightClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        // Only proceed if player is holding a lead
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.LEAD)
            return;

        // Check if the lead has the right name
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName() || !meta.getDisplayName().equalsIgnoreCase(addon.getSettings().getBeltName())) {
            return;
        }

        // check if it's a hostile mob
        if (!(entity instanceof Monster mob))
            return;

        mob.setFireTicks(60);
        mob.getWorld().playSound(entity, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1F, 2F);
        mob.damage(10, player);
        event.setCancelled(true);
        /*
        // Leash the monster to the player
        boolean isOK = mob.setLeashHolder(player);
        if (isOK) {
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                int heldItemSlot = player.getInventory().getHeldItemSlot();
                player.getInventory().clear(heldItemSlot);
            }
        
            player.updateInventory();
            event.setCancelled(true);
            mob.setAware(false);
            mob.setFireTicks(0);
            mob.setTarget(null);
            mob.setCustomNameVisible(false);
            mob.getWorld().playSound(entity, Sound.ENTITY_VILLAGER_NO, 1F, 1F);
        
        }*/
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
            ItemStack belt = new ItemStack(Material.LEAD);
            ItemMeta meta = belt.getItemMeta();
            meta.setDisplayName(addon.getSettings().getBeltName());
            belt.setItemMeta(meta);
            HashMap<Integer, ItemStack> map = e.getPlayer().getInventory().addItem(belt);
            map.values().forEach(i -> e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), i));
            e.setCancelled(true);
        }
    }

}
