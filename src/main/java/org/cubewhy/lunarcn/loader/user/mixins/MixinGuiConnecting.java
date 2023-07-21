package org.cubewhy.lunarcn.loader.user.mixins;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import org.cubewhy.lunarcn.utils.RenderUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

@Mixin(GuiConnecting.class)
public abstract class MixinGuiConnecting extends GuiScreen {
    /**
     * @author CubeWhy
     * @reason 重写加载屏幕
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        RenderUtils.drawLoadingCircle((float) this.width / 2, (float) this.height / 4 + 70);

        String ip = "Unknown";

        final ServerData serverData = mc.getCurrentServerData();
        if(serverData != null)
            ip = serverData.serverIP;

        this.drawCenteredString(mc.fontRendererObj, "Connecting to", this.width / 2, this.height / 4 + 110, 0xFFFFFF);
        this.drawCenteredString(mc.fontRendererObj, ip, this.width / 2, this.height / 4 + 120, 0x5281FB);
        String text = "You're playing on " + mc.getSession().getUsername();
        int textWidth = fontRendererObj.getStringWidth(text);
        this.drawCenteredString(mc.fontRendererObj, text, 30 + textWidth, 30, new Color(150, 50, 50).getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
