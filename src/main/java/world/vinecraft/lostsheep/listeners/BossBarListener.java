package world.vinecraft.lostsheep.listeners;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.bentobox.api.user.User;
import world.vinecraft.lostsheep.LostSheepAddon;
import world.vinecraft.lostsheep.dataobjects.PlayerData;

public class BossBarListener implements Listener {

    public BossBarListener(LostSheepAddon addon) {
        super();
        this.addon = addon;
    }

    private LostSheepAddon addon;

    // Store a boss bar for each player (using their UUID)
    private final Map<UUID, BossBar> islandBossBars = new HashMap<>();

    /**
     * Try to show the bossbar to the player
     * @param uuid player's UUID
     * @param island island they are on
     */
    public void tryToShowBossBar(UUID uuid) {
        User user = User.getInstance(uuid);
        // Prepare boss bar
        String title = user.getTranslationOrNothing("lostsheep.bossbar.title");
        BarColor c;
        try {
            c = BarColor.valueOf(user.getTranslation("lostsheep.bossbar.color").toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            c = BarColor.RED;
            addon.logError("Bossbar color unknown. Pick from RED, WHITE, PINK, BLUE, GREEN, YELLOW, or PURPLE");
        }
        BarStyle s = BarStyle.SOLID;
        try {
            s = BarStyle.valueOf(user.getTranslation("aoneblock.bossbar.style").toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            s = BarStyle.SOLID;
            addon.logError(
                    "Bossbar style unknow. Pick from SOLID, SEGMENTED_6,  SEGMENTED_10, SEGMENTED_12, SEGMENTED_20");
        }
        // Get it or make it
        BossBar bar = this.islandBossBars.getOrDefault(uuid, Bukkit.createBossBar(title, c, s));
        // Get the progress
        @NonNull
        PlayerData pd = addon.getScoreManager().getScore(uuid);

        // Set progress
        // Get distance
        double progress = 1;
        double distance = user.getLocation().distance(pd.getTarget());
        if (pd.getTarget() != null && pd.getTarget().getWorld().equals(user.getWorld())) {
            progress = Math.min(1D, distance / pd.getStartingDistance()); // Max is 1
        }
        bar.setProgress(progress);
        long numBlocksToGo = Math.round(distance);
        String translation = user.getTranslationOrNothing("lostsheep.bossbar.status", "[togo]",
                String.valueOf(numBlocksToGo), "[total]", String.valueOf(pd.getStartingDistance()), "[done]",
                String.valueOf(pd.getStartingDistance() - distance), "[[percent]", Math.round(progress * 100) + "%");
        bar.setTitle(translation);
        // Add to user if they don't have it already
        Player player = Bukkit.getPlayer(uuid);
        if (!bar.getPlayers().contains(player)) {
            bar.addPlayer(player);
        }
        // Save the boss bar for later reference (e.g., when updating or removing)
        islandBossBars.put(uuid, bar);

    }

    public void removeBar(User user) {
        BossBar bossBar = islandBossBars.get(user.getUniqueId());
        if (bossBar != null) {
            bossBar.removePlayer(user.getPlayer());
            if (bossBar.getPlayers().isEmpty()) {
                // Clean up
                islandBossBars.remove(user.getUniqueId());
            }
        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        this.tryToShowBossBar(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        // Clean up boss bars
        islandBossBars.values().stream().forEach(bb -> bb.removePlayer(e.getPlayer()));
        islandBossBars.values().removeIf(bb -> bb.getPlayers().isEmpty());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getTo().toVector().equals(e.getFrom().toVector()))
            return;
        this.tryToShowBossBar(e.getPlayer().getUniqueId());
    }

}
