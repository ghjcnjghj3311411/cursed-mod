package com.cursedmod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScreenEffects {
    private final Random random = new Random();
    private final Minecraft mc = Minecraft.getInstance();
    
    private float shakeOffsetX = 0;
    private float shakeOffsetY = 0;
    private List<GlitchBar> glitchBars = new ArrayList<>();
    private long lastGlitchTime = 0;
    private long lastScaryEventTime = 0;
    
    private static class GlitchBar {
        float y, height, offsetX;
        long createdAt;
        int duration;
        
        GlitchBar(float y, float height, float offsetX, int duration) {
            this.y = y;
            this.height = height;
            this.offsetX = offsetX;
            this.duration = duration;
            this.createdAt = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - createdAt > duration;
        }
    }
    
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (mc.player == null) return;
        if (CursedMod.deathTracker.isCompleted()) return;
        
        int effectLevel = CursedMod.deathTracker.getEffectLevel();
        if (effectLevel == 0) return;
        
        MatrixStack matrixStack = event.getMatrixStack();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        
        if (effectLevel >= 1) renderVignette(matrixStack, width, height, effectLevel);
        if (effectLevel >= 2) applyScreenShake(matrixStack, effectLevel);
        if (effectLevel >= 3) renderGlitchBars(matrixStack, width, height, effectLevel);
        if (effectLevel >= 4) renderStaticNoise(matrixStack, width, height, effectLevel);
        if (effectLevel >= 5) renderColorAberration(matrixStack, width, height);
        if (effectLevel >= 6) renderFlicker(matrixStack, width, height);
        if (effectLevel >= 7) renderCorruption(matrixStack, width, height, effectLevel);
        if (effectLevel >= 8) renderScaryFlashes(matrixStack, width, height);
        if (effectLevel >= 9) renderDistortion(matrixStack, width, height);
        
        if (CursedMod.deathTracker.isMemzMode()) {
            renderMemzEffects(matrixStack, width, height);
        }
        
        triggerRandomScaryEvents(effectLevel);
    }
    
    // Уровень 1: Тёмная виньетка
    private void renderVignette(MatrixStack matrixStack, int width, int height, int level) {
        int alpha = Math.min(150, 30 + level * 12);
        int color = (alpha << 24);
        
        int edgeWidth = width / 4;
        int edgeHeight = height / 4;
        
        // Левый край
        fillGradient(matrixStack, 0, 0, edgeWidth, height, color, 0x00000000);
        // Правый край
        fillGradient(matrixStack, width - edgeWidth, 0, width, height, 0x00000000, color);
        // Верх
        fillGradient(matrixStack, 0, 0, width, edgeHeight, color, 0x00000000);
        // Низ
        fillGradient(matrixStack, 0, height - edgeHeight, width, height, 0x00000000, color);
    }
    
    // Уровень 2: Тряска экрана
    private void applyScreenShake(MatrixStack matrixStack, int level) {
        float intensity = level * 0.5f;
        
        if (random.nextFloat() < 0.3f) {
            shakeOffsetX = (random.nextFloat() - 0.5f) * intensity * 2;
            shakeOffsetY = (random.nextFloat() - 0.5f) * intensity * 2;
        }
        
        matrixStack.translate(shakeOffsetX, shakeOffsetY, 0);
    }
    
    // Уровень 3: Глитч-полосы
    private void renderGlitchBars(MatrixStack matrixStack, int width, int height, int level) {
        long now = System.currentTimeMillis();
        
        if (now - lastGlitchTime > (500 / level)) {
            lastGlitchTime = now;
            
            int numBars = random.nextInt(level) + 1;
            for (int i = 0; i < numBars; i++) {
                float y = random.nextFloat() * height;
                float barHeight = 2 + random.nextFloat() * (5 + level);
                float offsetX = (random.nextFloat() - 0.5f) * 50 * level;
                int duration = 50 + random.nextInt(150);
                
                glitchBars.add(new GlitchBar(y, barHeight, offsetX, duration));
            }
        }
        
        glitchBars.removeIf(GlitchBar::isExpired);
        
        for (GlitchBar bar : glitchBars) {
            int color = random.nextBoolean() ? 0x8800FF00 : 0x88FF0000;
            fill(matrixStack, (int)bar.offsetX, (int)bar.y, 
                 width + (int)bar.offsetX, (int)(bar.y + bar.height), color);
        }
    }
    
    // Уровень 4: Статический шум
    private void renderStaticNoise(MatrixStack matrixStack, int width, int height, int level) {
        int noiseCount = level * 50;
        
        for (int i = 0; i < noiseCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int size = 1 + random.nextInt(3);
            int gray = random.nextInt(256);
            int alpha = 50 + random.nextInt(100);
            int color = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
            
            fill(matrixStack, x, y, x + size, y + size, color);
        }
    }
    
    // Уровень 5: Хроматическая аберрация
    private void renderColorAberration(MatrixStack matrixStack, int width, int height) {
        fill(matrixStack, 0, 0, 5, height, 0x33FF0000);
        fill(matrixStack, width - 5, 0, width, height, 0x330000FF);
        
        if (random.nextFloat() < 0.1f) {
            int y = random.nextInt(height);
            int h = 5 + random.nextInt(20);
            int color = random.nextBoolean() ? 0x44FF0000 : 0x440000FF;
            fill(matrixStack, 0, y, width, y + h, color);
        }
    }
    
    // Уровень 6: Мерцание
    private void renderFlicker(MatrixStack matrixStack, int width, int height) {
        if (random.nextFloat() < 0.05f) {
            int alpha = 50 + random.nextInt(100);
            fill(matrixStack, 0, 0, width, height, (alpha << 24) | 0xFFFFFF);
        }
        
        if (random.nextFloat() < 0.02f) {
            fill(matrixStack, 0, 0, width, height, 0xCC000000);
        }
    }
    
    // Уровень 7: Коррупция
    private void renderCorruption(MatrixStack matrixStack, int width, int height, int level) {
        int numCorruptions = (level - 6) * 3;
        
        for (int i = 0; i < numCorruptions; i++) {
            if (random.nextFloat() < 0.1f) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int w = 20 + random.nextInt(100);
                int h = 5 + random.nextInt(30);
                
                int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                int color = 0x88000000 | (r << 16) | (g << 8) | b;
                
                fill(matrixStack, x, y, x + w, y + h, color);
            }
        }
    }
    
    // Уровень 8: Страшные вспышки
    private void renderScaryFlashes(MatrixStack matrixStack, int width, int height) {
        if (random.nextFloat() < 0.02f) {
            fill(matrixStack, 0, 0, width, height, 0x66FF0000);
            CursedMod.soundManager.playRandomGlitchSound();
        }
    }
    
    // Уровень 9: Искажение
    private void renderDistortion(MatrixStack matrixStack, int width, int height) {
        for (int y = 0; y < height; y += 10) {
            float offset = (float)Math.sin(System.currentTimeMillis() * 0.01 + y * 0.1) * 10;
            
            if (random.nextFloat() < 0.3f) {
                fill(matrixStack, (int)offset, y, width + (int)offset, y + 2, 0x22000000);
            }
        }
    }
    
    // MEMZ MODE
    private void renderMemzEffects(MatrixStack matrixStack, int width, int height) {
        long time = System.currentTimeMillis();
        
        renderTunnelEffect(matrixStack, width, height, time);
        renderMeltEffect(matrixStack, width, height);
        renderRandomIcons(matrixStack, width, height);
        renderIntenseShake(matrixStack);
        renderScaryMessages(matrixStack, width, height);
        renderBrokenMonitor(matrixStack, width, height);
    }
    
    private void renderTunnelEffect(MatrixStack matrixStack, int width, int height, long time) {
        float pulse = (float)Math.sin(time * 0.005) * 0.1f + 0.9f;
        
        for (int i = 0; i < 5; i++) {
            float scale = pulse - (i * 0.05f);
            int alpha = 20 + i * 10;
            
            int x = (int)((width - width * scale) / 2);
            int y = (int)((height - height * scale) / 2);
            int w = (int)(width * scale);
            int h = (int)(height * scale);
            
            int color = (alpha << 24) | 0x00FF00;
            
            // Рамка
            fill(matrixStack, x, y, x + w, y + 1, color);
            fill(matrixStack, x, y + h - 1, x + w, y + h, color);
            fill(matrixStack, x, y, x + 1, y + h, color);
            fill(matrixStack, x + w - 1, y, x + w, y + h, color);
        }
    }
    
    private void renderMeltEffect(MatrixStack matrixStack, int width, int height) {
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(width);
            int startY = random.nextInt(height / 2);
            int length = 50 + random.nextInt(150);
            
            for (int y = startY; y < Math.min(startY + length, height); y += 3) {
                int alpha = 255 - ((y - startY) * 255 / length);
                int color = (alpha << 24) | (random.nextInt(0xFFFFFF));
                fill(matrixStack, x, y, x + 2, y + 3, color);
            }
        }
    }
    
    private void renderRandomIcons(MatrixStack matrixStack, int width, int height) {
        String[] symbols = {"X", "!", "?", "#", "@", "*", "0", "1"};
        
        if (random.nextFloat() < 0.3f) {
            String symbol = symbols[random.nextInt(symbols.length)];
            int x = random.nextInt(width - 20);
            int y = random.nextInt(height - 20);
            int color = random.nextBoolean() ? 0xFFFF0000 : 0xFF00FF00;
            
            mc.font.draw(matrixStack, symbol, x, y, color);
        }
    }
    
    private void renderIntenseShake(MatrixStack matrixStack) {
        float intensity = 8.0f;
        shakeOffsetX = (random.nextFloat() - 0.5f) * intensity * 2;
        shakeOffsetY = (random.nextFloat() - 0.5f) * intensity * 2;
        matrixStack.translate(shakeOffsetX, shakeOffsetY, 0);
    }
    
    private void renderScaryMessages(MatrixStack matrixStack, int width, int height) {
        String[] messages = {
            "HELP ME", "HE COMES", "DON'T TURN OFF",
            "I SEE YOU", "TOO LATE", "ERROR",
            "FATAL EXCEPTION", "SYSTEM CORRUPTED",
            "DELETE SYSTEM32?", "YOU ARE NEXT"
        };
        
        if (random.nextFloat() < 0.05f) {
            String msg = messages[random.nextInt(messages.length)];
            int x = random.nextInt(width - 100);
            int y = random.nextInt(height - 20);
            int color = random.nextBoolean() ? 0xFFFF0000 : 0xFF000000;
            
            mc.font.draw(matrixStack, msg, x, y, color);
        }
    }
    
    private void renderBrokenMonitor(MatrixStack matrixStack, int width, int height) {
        for (int i = 0; i < 10; i++) {
            if (random.nextFloat() < 0.2f) {
                int y = random.nextInt(height);
                int h = 1 + random.nextInt(5);
                int offset = random.nextInt(50) - 25;
                int color = 0xFF000000 | random.nextInt(0xFFFFFF);
                
                fill(matrixStack, offset, y, width + offset, y + h, color);
            }
        }
        
        for (int i = 0; i < 5; i++) {
            if (random.nextFloat() < 0.1f) {
                int x = random.nextInt(width);
                int w = 2 + random.nextInt(10);
                
                fill(matrixStack, x, 0, x + w, height, 0x88000000);
            }
        }
    }
    
    private void triggerRandomScaryEvents(int level) {
        long now = System.currentTimeMillis();
        int eventChance = 10000 / level;
        
        if (now - lastScaryEventTime > eventChance) {
            if (random.nextFloat() < 0.1f) {
                lastScaryEventTime = now;
                CursedMod.soundManager.playRandomCreepySound();
            }
        }
    }
    
    public void reset() {
        glitchBars.clear();
        shakeOffsetX = 0;
        shakeOffsetY = 0;
    }
    
    // Хелперы для отрисовки
    private void fill(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int color) {
        AbstractGui.fill(matrixStack, x1, y1, x2, y2, color);
    }
    
    private void fillGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, 
                              int colorFrom, int colorTo) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        fill(matrixStack, x1, y1, x2, y2, colorFrom);
        
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}