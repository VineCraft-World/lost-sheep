/**
 * Player database object
 */
package world.vinecraft.lostsheep.dataobjects;

import org.bukkit.Location;

import com.google.gson.annotations.Expose;

import world.bentobox.bentobox.database.objects.DataObject;

/**
 * Player data database object
 */
public class PlayerData implements DataObject {

    @Expose
    String uniqueId = "";
    @Expose
    long elapsedTime;
    @Expose
    Location target;
    @Expose
    long startingDistance;

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * @return the elapsedTime
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * @param elapsedTime the elapsedTime to set
     */
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    /**
     * @return the target
     */
    public Location getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Location target) {
        this.target = target;
    }

    /**
     * @return the startingDistance
     */
    public long getStartingDistance() {
        return startingDistance;
    }

    /**
     * @param startingDistance the startingDistance to set
     */
    public void setStartingDistance(long startingDistance) {
        this.startingDistance = startingDistance;
    }

}