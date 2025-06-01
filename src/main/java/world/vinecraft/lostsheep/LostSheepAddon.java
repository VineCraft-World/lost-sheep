package world.vinecraft.lostsheep;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.vinecraft.lostsheep.commands.WanderCommand;


/**
 * IslandFlyAddon main class. Enables addon.
 */
public class LostSheepAddon extends Addon {
    /**
     * Settings object for IslandFlyAddon
     */
    private Settings settings;
    private World overWorld;
    private World netherWorld;

    /**
     * Executes code when loading the addon. This is called before {@link #onEnable()}. This should preferably
     * be used to setup configuration and worlds.
     */
    @Override
    public void onLoad()
    {
        super.onLoad();
        // Save default config.yml
        this.saveDefaultConfig();
        // Load the plugin's config
        this.settings = new Config<>(this, Settings.class).loadConfigObject();
        this.loadSettings();
    }

    /**
     * Loads addon settings and hooks into available GameModes
     */
    @Override
    public void onEnable() {


            // Register Listeners
            // registerListener(new FlyListener(this));
            // Make the world if it doesn't exist
            overWorld = WorldCreator.name(getSettings().getWorldName()).environment(Environment.NORMAL).createWorld();
            netherWorld = WorldCreator.name(getSettings().getWorldName()).environment(Environment.NETHER).createWorld();
            //ommand
            new WanderCommand(this, "getlost");
    }


    /**
     * Disable addon.
     */
    @Override
    public void onDisable() {
        //Nothing to do here
    }

    /**
     * This method loads addon configuration settings in memory.
     */
    private void loadSettings() {

        if (this.settings == null) {
            // Disable
            this.logError("Settings could not load! Addon disabled.");
            this.setState(State.DISABLED);
        }
    }


    /**
     * Get addon settings
     * @return settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * @return the overWorld
     */
    public World getOverWorld() {
        return overWorld;
    }

    /**
     * @return the netherWorld
     */
    public World getNetherWorld() {
        return netherWorld;
    }

}
