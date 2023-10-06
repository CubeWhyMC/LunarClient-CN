package org.cubewhy.lunarcn.loader.injection.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import org.cubewhy.lunarcn.loader.utils.GitUtils;
import org.cubewhy.lunarcn.utils.FileUtils;
import org.cubewhy.lunarcn.utils.ImageUtils;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.lunarclient.LunarClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.cubewhy.lunarcn.loader.ModLoader.clientLogo;
import static top.lunarclient.LunarClient.logger;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "startGame", at = @At("HEAD"))
    public void startGameHead(CallbackInfo ci) {
        LunarClient.getInstance().onInit(); // init
    }

    @Inject(method = "startGame", at = @At("RETURN"))
    public void startGameReturn(CallbackInfo ci) {
        LunarClient.getInstance().onStart(); // start
    }

    /**
     * @author CubeWhy
     * @reason Set window icon
     */
    @Overwrite
    public void setWindowIcon() {
        try {
            BufferedImage image = ImageIO.read(FileUtils.getFile(clientLogo));
            ByteBuffer bytebuffer = ImageUtils.readImageToBuffer(ImageUtils.resizeImage(image, 16, 16));
            Display.setIcon(new ByteBuffer[]{
                    bytebuffer,
                    ImageUtils.readImageToBuffer(image)
            });
        } catch (IOException e) {
            logger.catching(e);
        }
    }

    @Inject(method = "displayCrashReport", at = @At(value = "INVOKE", target = "Lnet/minecraft/crash/CrashReport;getFile()Ljava/io/File;"))
    public void displayCrashReport(CrashReport crashReportIn, CallbackInfo ci) {
        LunarClient.getInstance().onCrash(crashReportIn);
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo ci) {
        LunarClient.getInstance().onStop(); // stop
    }
}
