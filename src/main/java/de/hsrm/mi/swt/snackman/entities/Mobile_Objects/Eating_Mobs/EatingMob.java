package de.hsrm.mi.swt.snackman.entities.Mobile_Objects.Eating_Mobs;

import de.hsrm.mi.swt.snackman.entities.Mobile_Objects.Mob;

public interface EatingMob extends Mob{
    
    public void gainKcal();
    public void loseKcal();
}
