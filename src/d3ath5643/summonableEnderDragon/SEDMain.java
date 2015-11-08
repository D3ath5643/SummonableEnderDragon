package d3ath5643.summonableEnderDragon;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

/**
 * @author d3ath5643
 * @version: 1.0
 */
public class SEDMain extends JavaPlugin{
    public Permission allPermissions;
    public boolean endOnly, preventDragonDamage, dropEgg;
    public PotionEffectType requiredEffect;
    public int summonLimit;
    
    @Override
    public void onEnable()
    {
        SEDUtil.setDefaults(this);
        new SEDListener(this);
        SEDUtil.createConfig(this);
        SEDUtil.loadConfig(this);
    }
    
    @Override
    public void onDisable()
    {
        
    }
}
