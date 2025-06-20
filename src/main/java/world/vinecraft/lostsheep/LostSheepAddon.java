package world.vinecraft.lostsheep;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.vinecraft.lostsheep.commands.EquipCommand;
import world.vinecraft.lostsheep.commands.SetDestinationCommand;
import world.vinecraft.lostsheep.commands.WanderCommand;
import world.vinecraft.lostsheep.listeners.BossBarListener;
import world.vinecraft.lostsheep.listeners.LeashListener;
import world.vinecraft.lostsheep.listeners.MonsterListener;


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
    private WanderCommand wander;
    private @NonNull Location destination;
    private LeashListener leashListener;
    private ScoreManager scoreManager;
    private BossBarListener bossBar;

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
            bossBar = new BossBarListener(this);
            registerListener(bossBar);
            leashListener = new LeashListener(this);
            registerListener(leashListener);
            scoreManager = new ScoreManager(this);
            registerListener(scoreManager);
            registerListener(new MonsterListener(this));
            // registerListener(new FlyListener(this));
            // Make the world if it doesn't exist
            overWorld = WorldCreator.name(getSettings().getWorldName()).environment(Environment.NORMAL).createWorld();
            netherWorld = WorldCreator.name(getSettings().getWorldName()).environment(Environment.NETHER).createWorld();
            // Commands
            wander = new WanderCommand(this, "getlost");
            new EquipCommand(this, "equip");
            new SetDestinationCommand(this, "setdestination");
    }


    /**
     * Disable addon.
     */
    @Override
    public void onDisable() {

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

    public void setDestination(@NonNull Location location) {
        this.destination = location;

    }

    /**
     * @return the destination
     */
    public Location getDestination() {
        return destination;
    }

    /**
     * @return the leashListener
     */
    public LeashListener getLeashListener() {
        return leashListener;
    }

    /**
     * @return the scoreManager
     */
    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    /**
     * @return the bossBar
     */
    public BossBarListener getBossBar() {
        return bossBar;
    }

}
