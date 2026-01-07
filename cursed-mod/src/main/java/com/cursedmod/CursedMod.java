package com.cursedmod;

import com.cursedmod.events.DeathHandler;
import com.cursedmod.events.WorldHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CursedMod.MOD_ID)
public class CursedMod {
    public static final String MOD_ID = "cursedmod";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static DeathTracker deathTracker;
    public static ScreenEffects screenEffects;
    public static SoundManager soundManager;
    
    public CursedMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        
        SoundManager.SOUNDS.register(modEventBus);
        
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void clientSetup(FMLClientSetupEvent event) {
        deathTracker = new DeathTracker();
        screenEffects = new ScreenEffects();
        soundManager = new SoundManager();
        
        MinecraftForge.EVENT_BUS.register(new DeathHandler());
        MinecraftForge.EVENT_BUS.register(new WorldHandler());
        MinecraftForge.EVENT_BUS.register(screenEffects);
        
        LOGGER.info("Cursed Mod загружен... удачи :)");
    }
}