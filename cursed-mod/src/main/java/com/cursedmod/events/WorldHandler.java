package com.cursedmod.events;

import com.cursedmod.CursedMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class WorldHandler {
    
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld() instanceof ClientWorld) {
            ClientWorld clientWorld = (ClientWorld) event.getWorld();
            String worldId = generateWorldId(clientWorld);
            
            CursedMod.deathTracker.setCurrentWorld(worldId);
            
            int deaths = CursedMod.deathTracker.getDeaths();
            boolean completed = CursedMod.deathTracker.isCompleted();
            
            CursedMod.LOGGER.info("Мир: " + worldId + ", смертей: " + deaths);
            
            if (completed) {
                Minecraft.getInstance().execute(() -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendMessage(
                            new StringTextComponent("\u00A7aУдачи :)"),
                            Minecraft.getInstance().player.getUUID()
                        );
                    }
                });
            }
        }
    }
    
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof ClientWorld) {
            CursedMod.screenEffects.reset();
            CursedMod.deathTracker.clearCurrentWorld();
            CursedMod.LOGGER.info("Мир выгружен");
        }
    }
    
    @SubscribeEvent
    public void onAdvancement(AdvancementEvent event) {
        String advancementId = event.getAdvancement().getId().toString();
        
        // Убийство дракона
        if (advancementId.equals("minecraft:end/kill_dragon")) {
            Minecraft mc = Minecraft.getInstance();
            
            if (mc.player != null && event.getPlayer() == mc.player) {
                CursedMod.deathTracker.setCompleted();
                CursedMod.screenEffects.reset();
                
                CursedMod.LOGGER.info("Победа! Проклятие снято!");
                
                mc.player.sendMessage(
                    new StringTextComponent("\u00A7a\u00A7lПроклятие снято!"),
                    mc.player.getUUID()
                );
                mc.player.sendMessage(
                    new StringTextComponent("\u00A7aУдачи :)"),
                    mc.player.getUUID()
                );
            }
        }
    }
    
    private String generateWorldId(ClientWorld level) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.getSingleplayerServer() != null) {
            return "local_" + mc.getSingleplayerServer().getWorldData().getLevelName();
        } else if (mc.getCurrentServer() != null) {
            return "server_" + mc.getCurrentServer().ip + "_" + 
                   level.dimension().location().toString();
        }
        
        return "unknown_" + System.currentTimeMillis();
    }
}