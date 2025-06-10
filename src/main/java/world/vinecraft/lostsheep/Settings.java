package world.vinecraft.lostsheep;


import java.util.ArrayList;
import java.util.List;

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
    @ConfigComment("World where the adventure will be held.")
    @ConfigEntry(path = "world-name")
    private String worldName = "lost-sheep-world";

    @ConfigComment("")
    @ConfigComment("Belt of truth name. The 'belt' is a leash")
    @ConfigEntry(path = "belt-name")
    private String beltName = "Belt Of Truth";

    @ConfigComment("")
    @ConfigComment("Range beyond which the sheep must be. Default is 5000 blocks.")
    @ConfigEntry(path = "range")
    private int range = 5000;

    @ConfigComment("")
    @ConfigComment("Range where monsters show their true names.")
    @ConfigEntry(path = "name-range")
    private double nameRange = 10;

    @ConfigComment("")
    @ConfigComment("Lies that become monster names")
    @ConfigEntry(path = "lies")
    private List<String> lies = new ArrayList<>();

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

    /**
     * @return the worldName
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * @param worldName the worldName to set
     */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    /**
     * @return the lies
     */
    public List<String> getLies() {
        return lies;
    }

    /**
     * @param lies the lies to set
     */
    public void setLies(List<String> lies) {
        this.lies = lies;
    }

    /**
     * @return the nameRange
     */
    public double getNameRange() {
        return nameRange;
    }

    /**
     * @param nameRange the nameRange to set
     */
    public void setNameRange(double nameRange) {
        this.nameRange = nameRange;
    }

    /**
     * @return the beltName
     */
    public String getBeltName() {
        return beltName;
    }

    /**
     * @param beltName the beltName to set
     */
    public void setBeltName(String beltName) {
        this.beltName = beltName;
    }
    
}
