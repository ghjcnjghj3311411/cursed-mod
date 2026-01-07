package com.cursedmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

import java.util.Random;

public class SoundManager {
    public static final DeferredRegister<SoundEvent> SOUNDS = 
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CursedMod.MOD_ID);
    
    public static final RegistryObject<SoundEvent> SCREAMER = registerSound("screamer");
    public static final RegistryObject<SoundEvent> GLITCH1 = registerSound("glitch1");
    public static final RegistryObject<SoundEvent> GLITCH2 = registerSound("glitch2");
    public static final RegistryObject<SoundEvent> CREEPY_AMBIENT = registerSound("creepy_ambient");
    public static final RegistryObject<SoundEvent> STATIC = registerSound("static");
    public static final RegistryObject<SoundEvent> WHISPER = registerSound("whisper");
    public static final RegistryObject<SoundEvent> HEARTBEAT = registerSound("heartbeat");
    public static final RegistryObject<SoundEvent> DISTORTED = registerSound("distorted");
    
    private final Random random = new Random();
    private final Minecraft mc = Minecraft.getInstance();
    
    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name, () -> 
            new SoundEvent(new ResourceLocation(CursedMod.MOD_ID, name)));
    }
    
    public void playScreamer() {
        playSound(SCREAMER.get(), 1.0f, 1.0f);
    }
    
    public void playRandomGlitchSound() {
        SoundEvent[] glitchSounds = {
            GLITCH1.get(), GLITCH2.get(), STATIC.get(), DISTORTED.get()
        };
        playSound(glitchSounds[random.nextInt(glitchSounds.length)], 
                 0.5f + random.nextFloat() * 0.5f, 
                 0.8f + random.nextFloat() * 0.4f);
    }
    
    public void playRandomCreepySound() {
        SoundEvent[] creepySounds = {
            CREEPY_AMBIENT.get(), WHISPER.get(), HEARTBEAT.get()
        };
        playSound(creepySounds[random.nextInt(creepySounds.length)], 
                 0.3f + random.nextFloat() * 0.4f, 
                 0.7f + random.nextFloat() * 0.6f);
    }
    
    private void playSound(SoundEvent sound, float volume, float pitch) {
        mc.getSoundManager().play(SimpleSound.forUI(sound, pitch, volume));
    }
}