package world.vinecraft.lostsheep.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;
import world.vinecraft.lostsheep.LostSheepAddon;

/**
 * Trigger a wandering sheep
 */
public class WanderCommand extends CompositeCommand {

    private LostSheepAddon addon;
    private static final int TARGET_DISTANCE = 5000; // straight‐line distance goal
    private static final int MAX_STEPS = 15000; // fail‐safe step limit
    private static final int DROP_INTERVAL = 100; // drop blue wool every N steps
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

    @Override
    public boolean execute(User user, String label, List<String> args) {
        if (!user.getWorld().equals(overWorld)) {
            Util.teleportAsync(user.getPlayer(), overWorld.getSpawnLocation());
            return true;
        }
        Location startLoc = user.getLocation().clone();
        startLoc.setWorld(overWorld);

        // Spawn a passive sheep at the player’s feet
        Sheep sheep = (Sheep) overWorld.spawnEntity(startLoc, EntityType.SHEEP);
        sheep.setAI(false); // disable default AI so we can control movement
        sheep.setAdult(); // ensure adult sheep (optional)
        sheep.setInvulnerable(true); // so nothing kills it mid‐simulation
        sheep.setColor(DyeColor.BLUE);

        new SheepWanderTask(sheep, startLoc).runTaskTimer(addon.getPlugin(), 0L, 1L);
        user.sendMessage("Wandering sheep spawned at " + locationString(startLoc) + ".");
        addon.log("Started wander task for sheep at " + locationString(startLoc) + ".");

        return true;
    }

    private class SheepWanderTask extends BukkitRunnable {
        private final Sheep sheep;
        private final Location startLoc;
        private final World world;
        private final Random random = new Random();
        private final List<Location> droppedLocations = new ArrayList<>();

        private int steps = 0;
        private final double targetDistSq = (double) TARGET_DISTANCE * TARGET_DISTANCE;

        public SheepWanderTask(Sheep sheep, Location startLoc) {
            this.sheep = sheep;
            this.startLoc = startLoc.clone();
            this.world = sheep.getWorld();
        }

        @Override
        public void run() {
            // 1. Check if sheep is already far enough away
            Location current = sheep.getLocation();
            double distSq = current.distanceSquared(startLoc);

            if (distSq >= targetDistSq) {
                finish("Reached target distance (≥ " + TARGET_DISTANCE + " blocks).");
                return;
            }

            // 2. Check if we've exceeded MAX_STEPS
            if (steps >= MAX_STEPS) {
                finish("Exceeded max steps (" + MAX_STEPS + ").");
                return;
            }

            // 3. Gather all valid neighbour locations (N, S, E, W) with a vertical shift of -1, 0, or +1
            List<Location> validMoves = new ArrayList<>(4);

            int curX = current.getBlockX();
            int curY = current.getBlockY();
            int curZ = current.getBlockZ();

            // Four cardinal directions
            int[][] directions = { { +1, 0 }, { -1, 0 }, { 0, +1 }, { 0, -1 } };

            for (int[] dir : directions) {
                int nx = curX + dir[0];
                int nz = curZ + dir[1];

                for (int deltaY = +1; deltaY >= -1; deltaY--) {
                    int ny = curY + deltaY;

                    // 3.1. Check vertical constraint: |deltaY| <= 1 (always true because we iterate +1, 0, -1)
                    // 3.2. Ground block at (nx, ny-1, nz) must be solid & non‐liquid
                    Block groundBlock = world.getBlockAt(nx, ny - 1, nz);
                    if (!groundBlock.getType().isSolid() || groundBlock.isLiquid()) {
                        continue;
                    }

                    // 3.3. Headspace (ny, ny+1) must be empty (air)
                    Block head1 = world.getBlockAt(nx, ny, nz);
                    Block head2 = world.getBlockAt(nx, ny + 1, nz);
                    if (head1.getType() != Material.AIR || head2.getType() != Material.AIR) {
                        continue;
                    }

                    // If we reach here, (nx, ny, nz) is a valid sheep move
                    Location valid = new Location(world, nx, ny, nz);
                    validMoves.add(valid);
                    break; // stop checking other deltaY values for this direction
                }
            }

            // 4. If no valid moves → sheep is trapped
            if (validMoves.isEmpty()) {
                finish("Sheep is trapped at " + locationString(current) + ".");
                return;
            }

            // 5. Pick one at random and teleport sheep
            Location nextLoc = validMoves.get(random.nextInt(validMoves.size()));
            sheep.teleport(nextLoc);
            steps++;

            // 6. Every DROP_INTERVAL steps, place a blue wool block and log its location
            if (steps % DROP_INTERVAL == 0) {
                Location dropLoc = sheep.getLocation().getBlock().getLocation(); // integer coords
                world.getBlockAt(dropLoc).setType(Material.BLUE_WOOL);
                droppedLocations.add(dropLoc.clone());
                getLogger().info("Dropped blue wool at " + locationString(dropLoc) + ".");
            }
        }

        // Called whenever we decide to end the wandering
        private void finish(String reason) {
            // Cancel this repeating task
            this.cancel();

            Location finalLoc = sheep.getLocation();
            double dx = finalLoc.getX() - startLoc.getX();
            double dy = finalLoc.getY() - startLoc.getY();
            double dz = finalLoc.getZ() - startLoc.getZ();
            double finalDist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            getLogger().info("Sheep wander ended: " + reason);
            getLogger().info("Total steps: " + steps);
            getLogger().info("Final sheep location: " + locationString(finalLoc));
            getLogger().info(String.format("Straight‐line distance from start: %.2f blocks", finalDist));

            // Optionally remove or leave the sheep sitting there
            sheep.setAI(true); // re‐enable AI if you want it to behave normally afterwards
            sheep.setInvulnerable(false);
        }
    }

    // -----------------------
    // 2.3. Utility Method
    // -----------------------
    private static String locationString(Location loc) {
        return String.format("World=\"%s\", x=%d, y=%d, z=%d", loc.getWorld().getName(), loc.getBlockX(),
                loc.getBlockY(), loc.getBlockZ());
    }
}

