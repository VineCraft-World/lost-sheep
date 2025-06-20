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
 * Sets the destination
 */
public class SetDestinationCommand extends CompositeCommand {

    private LostSheepAddon addon;

    public SetDestinationCommand(LostSheepAddon addon, String label) {
        super(addon, label);
        this.addon = addon;
    }

    @Override
    public void setup() {
        this.setOnlyPlayer(true);
        this.setPermission("set-destination");

    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        addon.setDestination(user.getLocation());
        user.sendMessage("general.success");
        return true;
    }


}

