package world.vinecraft.lostsheep;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.Database;
import world.vinecraft.lostsheep.dataobjects.PlayerData;

public class ScoreManager implements Listener {
    private final LostSheepAddon addon;
    private final HashMap<UUID, PlayerData> playerData = new HashMap<>();
    private final Database<PlayerData> handler;

    public ScoreManager(LostSheepAddon addon) {
        super();
        this.addon = addon;
        handler = new Database<>(addon, PlayerData.class);
    }

    public PlayerData getScore(UUID playerID) {
        if (playerData.containsKey(playerID)) {
            PlayerData pd = playerData.get(playerID);
            handler.saveObjectAsync(pd);
            return pd;
        }
        if (handler.objectExists(playerID.toString())) {
            PlayerData pd = handler.loadObject(playerID.toString());
            playerData.put(playerID, pd);
            handler.saveObjectAsync(pd);
            return pd;
        }
        // Nothing
        PlayerData pd = new PlayerData();
        pd.setUniqueId(playerID.toString());
        playerData.put(playerID, pd);
        handler.saveObjectAsync(pd);
        return pd;
    }

    public void onDisable() {
        if (handler != null) {
            // Save cache
            playerData.values().forEach(handler::saveObject);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        getScore(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        getScore(e.getPlayer().getUniqueId());
        playerData.remove(e.getPlayer().getUniqueId());
    }

    public void setTarget(User user, Location target) {
        PlayerData pd = getScore(user.getUniqueId());
        pd.setTarget(target);
        playerData.put(user.getUniqueId(), pd);
        handler.saveObjectAsync(pd);
    }

    public void setInitialDistance(User user, long distance) {
        PlayerData pd = getScore(user.getUniqueId());
        pd.setStartingDistance(distance);
        playerData.put(user.getUniqueId(), pd);
        handler.saveObjectAsync(pd);
    }

    public PlayerData resetPlayer(User user, Location target) {
        PlayerData pd = new PlayerData();
        pd.setUniqueId(user.getUniqueId().toString());
        pd.setTarget(target);
        pd.setStartingDistance(Math.round(user.getLocation().distance(target)));
        playerData.put(user.getUniqueId(), pd);
        handler.saveObjectAsync(pd);
        return pd;
    }

}
