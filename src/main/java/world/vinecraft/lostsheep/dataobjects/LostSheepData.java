/**
 * Lost sheep database object
 */
package world.vinecraft.lostsheep.dataobjects;

import org.bukkit.Location;

import com.google.gson.annotations.Expose;

import world.bentobox.bentobox.database.objects.DataObject;

/**
 * Lost sheep database object
 */
public class LostSheepData implements DataObject {

    @Expose
    String uniqueId = "lostsheep";
    @Expose
    Location sheepLoc;

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * @return the sheepLoc
     */
    public Location getSheepLoc() {
        return sheepLoc;
    }

    /**
     * @param sheepLoc the sheepLoc to set
     */
    public void setSheepLoc(Location sheepLoc) {
        this.sheepLoc = sheepLoc;
    }

}
