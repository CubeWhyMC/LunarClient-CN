package org.cubewhy.lunarcn.utils;

import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImageUtils {
    /**
     * Reads the image to a byte buffer that works with LWJGL.
     * @author func16
     */
    public static ByteBuffer readImageToBuffer(BufferedImage bufferedImage){
        int[] rgbArray = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * rgbArray.length);
        for(int rgb : rgbArray){
            byteBuffer.putInt(rgb << 8 | rgb >> 24 & 255);
        }
        byteBuffer.flip();

        return byteBuffer;
    }


    public static ByteBuffer readImageToBuffer(File image) throws IOException {
        return readImageToBuffer(ImageIO.read(image));
    }

    public static ByteBuffer readImageToBuffer(InputStream image) throws IOException {
        return readImageToBuffer(ImageIO.read(image));
    }

    public static ByteBuffer readImageToBuffer(ResourceLocation image) throws IOException {
        return readImageToBuffer(FileUtils.getFile(image.getResourcePath()));
    }


    /**
     * Resize the image to the specified width and height.
     *
     * @author liulihaocai
     */
    public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        buffImg.getGraphics().drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        return buffImg;
    }
}
