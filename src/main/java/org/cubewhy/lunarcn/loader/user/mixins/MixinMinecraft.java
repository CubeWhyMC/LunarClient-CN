package org.cubewhy.lunarcn.loader.user.mixins;

import net.minecraft.client.Minecraft;
import org.cubewhy.lunarcn.Main;
import org.cubewhy.lunarcn.utils.FileUtils;
import org.cubewhy.lunarcn.utils.ImageUtils;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "startGame", at = @At("RETURN"))
    public void startGameReturn(CallbackInfo ci) {
        Display.setTitle(Display.getTitle() + " | LunarCN " + Main.version + " | Open source is love");
    }

    /**
     * @author CubeWhy
     * @reason 设置窗口图标
     */
    @Overwrite
    public void setWindowIcon() {
        try {
            BufferedImage image = ImageIO.read(FileUtils.getFile(Main.clientLogo));
            ByteBuffer bytebuffer = ImageUtils.readImageToBuffer(ImageUtils.resizeImage(image, 16, 16));
            Display.setIcon(new ByteBuffer[]{
                    bytebuffer,
                    ImageUtils.readImageToBuffer(image)
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
