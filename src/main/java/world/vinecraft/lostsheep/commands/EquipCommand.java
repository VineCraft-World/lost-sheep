package world.vinecraft.lostsheep.commands;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    @Override
    public boolean execute(User user, String label, List<String> args) {
        ItemStack belt = new ItemStack(Material.LEAD);
        ItemMeta meta = belt.getItemMeta();
        meta.setDisplayName(addon.getSettings().getBeltName());
        belt.setItemMeta(meta);
        HashMap<Integer, ItemStack> map = user.getInventory().addItem(belt);
        map.values().forEach(i -> user.getWorld().dropItemNaturally(user.getLocation(), i));
        return true;
    }


}

