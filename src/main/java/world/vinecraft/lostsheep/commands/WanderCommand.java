package world.vinecraft.lostsheep.commands;

import java.util.List;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.teleport.SafeSpotTeleport;
import world.vinecraft.lostsheep.LostSheepAddon;

/**
 * Trigger a wandering sheep
 */
public class WanderCommand extends CompositeCommand {

    private LostSheepAddon addon;
    private static final int TARGET_DISTANCE = 5000; // straight‐line distance goal
    private World overWorld;

    public WanderCommand(LostSheepAddon addon, String label) {
        super(addon, label);
        this.addon = addon;
        overWorld = addon.getOverWorld();
    }

    @Override
    public void setup() {
        this.setOnlyPlayer(true);

    }

    /**
    * Returns a new Location TARGET_DISTANCE blocks away from the origin in a random direction.
    *
    * @param origin The starting location.
    * @return A new Location that is TARGET_DISTANCE away in a random direction, preserving Y.
    */
    public static Location getRandomTargetLocation(Location origin) {
        Random random = new Random();
        double angle = random.nextDouble() * 2 * Math.PI; // Random angle in radians

        double deltaX = TARGET_DISTANCE * Math.cos(angle);
        double deltaZ = TARGET_DISTANCE * Math.sin(angle);

        World world = origin.getWorld();
        if (world == null)
            throw new IllegalArgumentException("Origin location must have a world");

        int targetX = origin.getBlockX() + (int) deltaX;
        int targetZ = origin.getBlockZ() + (int) deltaZ;
        int targetY = world.getHighestBlockYAt(targetX, targetZ);

        return new Location(world, targetX, targetY, targetZ);
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        // Get current position
        Location endPos = user.getLocation();
        // Find a position in a random direction away from here
        Location startPost = getRandomTargetLocation(endPos);
        // Teleport the player to that position
        new SafeSpotTeleport.Builder(getPlugin()).location(startPost).entity(user.getPlayer()).buildFuture()
                .thenAccept(success -> {
                    if (!success) {
                        getPlugin().logDebug("Not successful");
                        return;
                    }
                    addon.getScoreManager().resetPlayer(user, endPos);
                    addon.getScoreManager().setInitialDistance(user, TARGET_DISTANCE); // TODO set real distance
                    addon.getBossBar().tryToShowBossBar(user.getUniqueId());
                    // Spawn a sheep new player
                    Sheep sheep = (Sheep) user.getWorld().spawnEntity(user.getLocation(), EntityType.SHEEP);
                    sheep.setAdult(); // ensure adult sheep
                    sheep.setColor(DyeColor.BLUE);
                });
        return true;
    }


}

