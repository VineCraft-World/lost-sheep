package world.vinecraft.lostsheep;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;



public class LostSheepPladdon extends Pladdon
{
    Addon addon;
    @Override
    public Addon getAddon()
    {
        if (addon == null) {
            addon = new LostSheepAddon();
        }
        return addon;
    }
}
