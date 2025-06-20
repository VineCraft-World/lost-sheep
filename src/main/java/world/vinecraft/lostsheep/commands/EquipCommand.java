package world.vinecraft.lostsheep.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.vinecraft.lostsheep.LostSheepAddon;

/**
 * Equip player
 */
public class EquipCommand extends CompositeCommand {

    private LostSheepAddon addon;

    public EquipCommand(LostSheepAddon addon, String label) {
        super(addon, label);
        this.addon = addon;
    }

    @Override
    public void setup() {
        this.setOnlyPlayer(true);
        this.setPermission("equip");

    }

    private void giveTo(User user, ItemStack item) {
        HashMap<Integer, ItemStack> map = user.getInventory().addItem(item);
        map.values().forEach(i -> user.getWorld().dropItemNaturally(user.getLocation(), i));
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        if (args.isEmpty()) {
            this.showHelp(this, user);
            return false;
        }
        if (args.get(0).equalsIgnoreCase("belt")) {
            // Belt
            giveTo(user, addon.getLeashListener().makeBelt(new ItemStack(Material.LEAD)));
            user.sendMessage("general.success");
            user.getWorld().playSound(user.getPlayer(), Sound.ENTITY_ITEM_PICKUP, 1F, 1.5F);

        } else if (args.get(0).equalsIgnoreCase("compass")) {
            // Compass
            if (addon.getDestination() == null) {
                user.sendMessage("error.no-destination");
                return false;
            }
            if (addon.getDestination().getBlock().getType() != Material.LODESTONE) {
                addon.logWarning("No lodestone at " + addon.getDestination() + " - adding one");
                addon.getDestination().getBlock().setType(Material.LODESTONE);
            }
            ItemStack compass = new ItemStack(Material.COMPASS);
            CompassMeta meta = (CompassMeta) compass.getItemMeta();
            meta.setLodestoneTracked(false); // Disable before setting
            meta.setLodestone(addon.getDestination());
            compass.setItemMeta(meta);
            giveTo(user, compass);
        } else {
            // unknown
            this.showHelp(this, user);
            return false;
        }
        return true;
    }

    @Override
    public Optional<List<String>> tabComplete(User user, String alias, List<String> args) {
        return Optional.of(List.of("compass", "belt"));
    }

}

