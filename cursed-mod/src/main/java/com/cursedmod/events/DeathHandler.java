package com.cursedmod.events;

import com.cursedmod.CursedMod;
import com.cursedmod.ScreamerScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class DeathHandler {
    
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.player != null && event.getEntity() == mc.player) {
            
            if (CursedMod.deathTracker.isCompleted()) {
                return;
            }
            
            CursedMod.deathTracker.addDeath();
            int deaths = CursedMod.deathTracker.getDeaths();
            
            CursedMod.LOGGER.info("Смерть! Всего: " + deaths);
            
            // Показываем скример на 5 секунд
            mc.execute(() -> {
                mc.setScreen(new ScreamerScreen(5));
            });
            
            if (deaths >= 10) {
                CursedMod.LOGGER.info("MEMZ MODE!");
            }
        }
    }
}