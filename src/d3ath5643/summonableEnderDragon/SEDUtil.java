package d3ath5643.summonableEnderDragon;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.potion.PotionEffectType;

/**
 * @author d3ath5643
 * @version: 1.0
 */
public class SEDUtil {
    public static final String PLUGIN_NAME = "SummonableEnderDragon",
                               ALL_PERMISSIONS = "*",
                               SUMMON_PERMISSIONS = "summon";
    
    public static void setDefaults(SEDMain plugin)
    {
        plugin.allPermissions = new Permission(PLUGIN_NAME+"."+ALL_PERMISSIONS);
        plugin.endOnly = true;
        plugin.preventDragonDamage = true;
        plugin.dropEgg = true;
        plugin.requiredEffect = PotionEffectType.REGENERATION;
        plugin.summonLimit = 0;
        
        addPermissions(plugin);
    }
    
    public static void addPermissions(SEDMain plugin)
    {
        Map<String, Boolean> children = plugin.allPermissions.getChildren();
        children.put(PLUGIN_NAME + "."+ SUMMON_PERMISSIONS, true);
        plugin.allPermissions.recalculatePermissibles();
        
        plugin.getServer().getPluginManager().addPermission(plugin.allPermissions);
    }
    
    public static void createConfig(SEDMain plugin)
    {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }
    
    public static void loadConfig(SEDMain plugin)
    {
        PotionEffectType potionEffect = PotionEffectType.getByName(plugin.getConfig().getString("summonPotionEffect"));
        plugin.endOnly = !(plugin.getConfig().getBoolean("summonableOutsideEnd"));
        plugin.preventDragonDamage = plugin.getConfig().getBoolean("preventDragonGrief");
        plugin.dropEgg = plugin.getConfig().getBoolean("dropEgg");
        plugin.summonLimit = plugin.getConfig().getInt("summonLimit");
        if(potionEffect != null)
            plugin.requiredEffect = potionEffect;
        else
            plugin.getLogger().warning(plugin.getConfig().getString("summonPotionEffect") + 
                                       " is not a valid Potion Effect. The required potion" + 
                                       " effect will be set to REGENERATION.");
    }

    public static void createPortal(Location loc) 
    {
        World world = loc.getWorld();
        
        for(int x = -1; x <= 1; x++)
            for(int z = -1; z <= 1; z++)
                world.getBlockAt(loc.getBlockX()-x, 
                                 loc.getBlockY()-1, 
                                 loc.getBlockZ()-z).setType(Material.BEDROCK);
        for(int x = -2; x <= 2; x+=4)
            for(int z = -1; z <= 1; z++)
                world.getBlockAt(loc.getBlockX()-x, 
                        loc.getBlockY() - 1, 
                        loc.getBlockZ()-z).setType(Material.BEDROCK);
        for(int x = -1; x <= 1; x++)
            for(int z = -2; z <= 2; z+=4)
                world.getBlockAt(loc.getBlockX()-x, 
                        loc.getBlockY() - 1, 
                        loc.getBlockZ()-z).setType(Material.BEDROCK);
        
        for(int y = 0; y <= 3; y++)
            world.getBlockAt(loc.getBlockX(), 
                             loc.getBlockY() + y, 
                             loc.getBlockZ()).setType(Material.BEDROCK);
        
        for(int x = -3; x <= 3; x+=6)
            for(int z = -1; z <= 1; z++)
                world.getBlockAt(loc.getBlockX()-x, 
                        loc.getBlockY(), 
                        loc.getBlockZ()-z).setType(Material.BEDROCK);
        for(int x = -1; x <= 1; x++)
            for(int z = -3; z <= 3; z+=6)
                world.getBlockAt(loc.getBlockX()-x, 
                        loc.getBlockY(), 
                        loc.getBlockZ()-z).setType(Material.BEDROCK);
        for(int x = -2; x <= 2; x+=4)
            for(int z = -2; z <= 2; z+=4)
                world.getBlockAt(loc.getBlockX()-x, 
                        loc.getBlockY(), 
                        loc.getBlockZ()-z).setType(Material.BEDROCK);
    }
    
    public static void createEgg(Location loc)
    {
        while(loc.getWorld().getBlockAt(loc.getBlockX(), 
                               loc.getBlockY(), 
                               loc.getBlockZ()).getType() == Material.DRAGON_EGG || 
              loc.getBlockY() >= 256)
            loc.setY(loc.getY() + 1);
        
        loc.getWorld().getBlockAt(loc.getBlockX(), 
                                  loc.getBlockY(), 
                                  loc.getBlockZ()).setType(Material.DRAGON_EGG);
    }
    
    public static void createPortalFrames(Location loc)
    {
        World world = loc.getWorld();
        
        while(world.getBlockAt(loc.getBlockX(), 
                                    loc.getBlockY(), 
                                    loc.getBlockZ()).getType() != Material.BEDROCK || 
              loc.getBlockY() <= 0)
            loc.setY(loc.getY() - 1);
        
        for(int x = -2; x <= 2; x++)
            for(int z = -2; z <= 2; z++)
                if(world.getBlockAt(loc.getBlockX()-x, 
                                    loc.getBlockY()-3, 
                                    loc.getBlockZ()-z).getType() != Material.BEDROCK)
                    world.getBlockAt(loc.getBlockX()-x, 
                                 loc.getBlockY()-3, 
                                 loc.getBlockZ()-z).setType(Material.ENDER_PORTAL);
    }
    
    public static void removePortalFrames(Location loc)
    {
        World world = loc.getWorld();
        
        while(world.getBlockAt(loc.getBlockX(), 
                                    loc.getBlockY(), 
                                    loc.getBlockZ()).getType() != Material.BEDROCK || 
              loc.getBlockY() <= 0)
            loc.setY(loc.getY() - 1);
        
        for(int x = -2; x <= 2; x++)
            for(int z = -2; z <= 2; z++)
                if(world.getBlockAt(loc.getBlockX()-x, 
                                    loc.getBlockY()-3, 
                                    loc.getBlockZ()-z).getType() != Material.BEDROCK)
                    world.getBlockAt(loc.getBlockX()-x, 
                                 loc.getBlockY()-3, 
                                 loc.getBlockZ()-z).setType(Material.AIR);
    }

    public static boolean hasSummonPermission(Player p) 
    {
        return p.hasPermission(PLUGIN_NAME+"."+ALL_PERMISSIONS) || 
               p.hasPermission(PLUGIN_NAME+"."+SUMMON_PERMISSIONS);
    }
}
