package d3ath5643.summonableEnderDragon;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * @author d3ath5643
 * @version: 1.0
 */
public class SEDListener implements Listener{
    private SEDMain plugin;
    
    public SEDListener(SEDMain plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPotionSplashEvent(PotionSplashEvent e)
    {
        Collection<PotionEffect> effects = e.getPotion().getEffects();
        Collection<LivingEntity> entities = e.getAffectedEntities();
        boolean hasReqEff = false;
        int summonedCounter = 0;
        
        if(e.getEntity().getShooter() instanceof Player)
        {
            Player p = (Player)(e.getEntity().getShooter());
            if(!SEDUtil.hasSummonPermission(p))
            {
                p.sendMessage(ChatColor.RED + "The Ender Dragon does not" +
                              " give you permission to summon him.");
                return;
            }
        }
        
        for(PotionEffect eff: effects)
            if(eff.getType() == plugin.requiredEffect)
            {
                hasReqEff = true;
                break;
            }
        if(hasReqEff)
        {
            Location portalLocation = null;
            boolean removePortalFrames = false;
            
            for(LivingEntity ent: entities)
                if(ent.getType() == EntityType.ARMOR_STAND)
                {
                    ItemStack helm = ((ArmorStand)ent).getHelmet();
                    if(helm.getType() == Material.SKULL_ITEM && 
                            helm.getData().getData() == (byte)1)
                    {
                        if(plugin.endOnly && ent.getWorld().getEnvironment() != World.Environment.THE_END)
                        {
                            if(e.getEntity().getShooter() instanceof Player)
                            {
                                Player p = (Player)(e.getEntity().getShooter());
                                p.sendMessage(ChatColor.GOLD + "The Ender Dragon cannot see " +
                                              "you summoning him. Try performing the summoning in" + 
                                               " his layer");
                            }
                            continue;
                        }
                        
                        Location loc = ent.getLocation();
                        summonedCounter++;
                        ent.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
                        ent.remove();
                     
                        if(loc.getWorld().getEnvironment() == World.Environment.THE_END)
                        {
                            removePortalFrames = true;
                            portalLocation = new Location(loc.getWorld(), 
                                                          0, 
                                                          loc.getWorld().getHighestBlockYAt(0, 0), 
                                                          0);
                        }
                        
                        if(plugin.summonLimit > 0 && summonedCounter >= plugin.summonLimit)
                            break;
                         
                    }
                }
            
            if(removePortalFrames && portalLocation != null)
                SEDUtil.removePortalFrames(portalLocation);
        }
    }
    
    @EventHandler
    public void onEntityChnageBlockEvent(EntityChangeBlockEvent e)
    {
        if(e.getEntityType() == EntityType.ENDER_DRAGON && plugin.preventDragonDamage)
            e.setCancelled(true);
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e)
    {
        if(e.getEntityType() == EntityType.ENDER_DRAGON)
        {
           if(e.getEntity().getWorld().getEnvironment() != World.Environment.THE_END && 
              plugin.dropEgg)
               e.getDrops().add(new ItemStack(Material.DRAGON_EGG));
           else
           {
               World theEnd = e.getEntity().getWorld();
               Location centerBlock = new Location(theEnd, 0, theEnd.getHighestBlockYAt(0, 0), 0);
               
               if(plugin.dropEgg)
                   SEDUtil.createEgg(centerBlock);
               
               if(theEnd.getEntitiesByClass(EntityType.ENDER_DRAGON.getEntityClass()).size() == 1)
                   SEDUtil.createPortalFrames(centerBlock);
           }
        }
    }
    
    @EventHandler
    public void onPlayerTelport(PlayerTeleportEvent e)
    {
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL &&
           e.getTo().getWorld().getEnvironment() == World.Environment.THE_END)
        {
            World theEnd = e.getTo().getWorld();
            boolean portalGenerated = false;
            
            for(int i = theEnd.getHighestBlockYAt(0, 0); i >= 0; i--)
                if(theEnd.getBlockAt(0, i, 0).getType() == Material.BEDROCK){
                    portalGenerated = true;
                    break;
                }
            
            if(!portalGenerated)
                SEDUtil.createPortal(new Location(theEnd, 0, theEnd.getHighestBlockYAt(0, 0), 0));
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(e.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END)
        {
            World theEnd = e.getPlayer().getWorld();
            boolean portalGenerated = false;
            
            for(int i = theEnd.getHighestBlockYAt(0, 0); i >= 0; i--)
                if(theEnd.getBlockAt(0, i, 0).getType() == Material.BEDROCK){
                    portalGenerated = true;
                    break;
                }
            
            if(!portalGenerated)
                SEDUtil.createPortal(new Location(theEnd, 0, theEnd.getHighestBlockYAt(0, 0), 0));
        }
    }
    
    @EventHandler
    public void onEntityCreatePortal(EntityCreatePortalEvent e)
    {
        if(e.getEntityType() == EntityType.ENDER_DRAGON)
            e.setCancelled(true);
    }
}
