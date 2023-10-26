package top.lunarclient.ui;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import top.lunarclient.files.Config;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.cubewhy.lunarcn.utils.RenderUtils.fontRenderer;
import static top.lunarclient.LunarClient.config;

public class GuiOutdated extends GuiScreen {

    private final String versionLocal;
    private final JsonObject remoteData;
    private final String releaseUrl;

    public GuiOutdated(JsonObject remoteData, String versionLocal) {
        this.remoteData = remoteData;
        this.versionLocal = versionLocal;
        this.releaseUrl = remoteData.get("html_url").getAsString();
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2, this.height / 2, 20, 50, "下载更新"));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 50, this.height / 2, 20, 50, "关闭窗口"));

        this.buttonList.add(new GuiButton(2, this.width / 2, this.height / 2 + 25, "永久关闭这个提示(可能导致错过重要更新)"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float v) {
        // Render Title
        this.drawDefaultBackground();
        this.drawCenteredString(fontRenderer, "LunarCN 有新版本!", 0, 40, 0xffffff); // white
        this.drawCenteredString(fontRenderer, "最新版本: " + releaseUrl, -100, 80, 0xffffff);
        this.drawCenteredString(fontRenderer, "如果你觉得这个提示烦人, 可以在config.json中关闭 ", -100, 90, 0xffffff);
        super.drawScreen(mouseX, mouseY, v);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        switch (guiButton.id) {
            case 0 -> {
                // Download Update
                try {
                    Desktop.getDesktop().browse(new URL(this.releaseUrl).toURI());
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                mc.displayGuiScreen(null);
            }
            case 1 -> {
                // close Window
                mc.displayGuiScreen(null);
            }
            case 2 -> {
                // Disable this warning forever
                config.setValue("check-update", false); // turn to false
                mc.displayGuiScreen(null);
            }
        }
    }
}
