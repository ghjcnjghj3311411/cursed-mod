package com.cursedmod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class ScreamerScreen extends Screen {
    private static final ResourceLocation SCREAMER_TEXTURE = 
        new ResourceLocation(CursedMod.MOD_ID, "textures/screamer.png");
    
    private final long startTime;
    private final int duration;
    private final Screen previousScreen;
    
    public ScreamerScreen(int durationSeconds) {
        super(new StringTextComponent(""));
        this.startTime = System.currentTimeMillis();
        this.duration = durationSeconds * 1000;
        this.previousScreen = Minecraft.getInstance().screen;
    }
    
    @Override
    protected void init() {
        super.init();
        CursedMod.soundManager.playScreamer();
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (System.currentTimeMillis() - startTime > duration) {
            close();
            return;
        }
        
        // Чёрный фон
        AbstractGui.fill(matrixStack, 0, 0, this.width, this.height, 0xFF000000);
        
        // Текстура скримера
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bind(SCREAMER_TEXTURE);
        
        blit(matrixStack, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
        
        // Тряска
        if (Math.random() < 0.3) {
            int shakeX = (int)((Math.random() - 0.5) * 20);
            int shakeY = (int)((Math.random() - 0.5) * 20);
            matrixStack.translate(shakeX, shakeY, 0);
        }
        
        // Красные вспышки
        if (Math.random() < 0.1) {
            AbstractGui.fill(matrixStack, 0, 0, this.width, this.height, 0x44FF0000);
        }
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    private void close() {
        Minecraft.getInstance().setScreen(previousScreen);
    }
}