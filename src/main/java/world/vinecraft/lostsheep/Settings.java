package world.vinecraft.lostsheep;


import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;


@StoreAt(filename = "config.yml", path = "addons/LostSheep")
@ConfigComment("Lost Sheep Configuration [version]")
@ConfigComment("This config file is dynamic and saved when the server is shutdown.")
public class Settings implements ConfigObject
{
    @ConfigComment("")
    @ConfigComment("Range beyond which the sheep must be. Default is 5000 blocks.")
    @ConfigEntry(path = "range")
    private int range = 5000;

    /**
     * @return the range
     */
    public int getRange() {
        return range;
    }

    /**
     * @param range the range to set
     */
    public void setRange(int range) {
        this.range = range;
    }
    
    
}
