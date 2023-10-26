package top.lunarclient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.minecraft.crash.CrashReport;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.cubewhy.lunarcn.loader.ModLoader;
import org.cubewhy.lunarcn.loader.api.ModInitializer;
import org.cubewhy.lunarcn.loader.utils.GitUtils;
import org.cubewhy.lunarcn.utils.HttpUtils;
import top.lunarclient.files.Config;
import top.lunarclient.ui.GuiOutdated;
import org.cubewhy.lunarcn.utils.RenderUtils;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static org.cubewhy.lunarcn.utils.MinecraftInstance.mc;

public class LunarClient {
    public static final File configDir = new File(System.getProperty("configDir", System.getProperty("user.home") + "/.cubewhy" + "/lunarcn")); // config folder
    public static final String CLIENT_NAME = "LunarCN";
    // API source: https://github.com/CubeWhyMC/website
    public static final String CLIENT_API = "https://api.lunarclient.top"; // No "/" in the end
    public static final String CLIENT_VERSION = GitUtils.gitInfo.getProperty("git.build.version");
    public static final Logger logger = LogManager.getLogger(CLIENT_NAME);
    public static final Config config = new Config(new File(configDir, "config.json"));
    public static final boolean checkUpdate = config.getValue("check-update").getAsBoolean();
    @Getter
    private static final LunarClient instance = new LunarClient();

    private LunarClient() {
    }

    public void onInit() {
        logger.info("onClientInit");
        ModLoader.INSTANCE.getInitializers().forEach(ModInitializer::onInit);
    }

    public void onStart() {
        logger.info("onClientStart");
        ModLoader.INSTANCE.getInitializers().forEach(ModInitializer::onStart);
        Display.setTitle(Display.getTitle() + " | " + LunarClient.CLIENT_NAME + " " + LunarClient.CLIENT_VERSION);
        // give fontRender
        RenderUtils.fontRenderer = mc.fontRendererObj;
        if (checkUpdate) {
            try {
                checkUpdate();
            } catch (Exception e) {
                logger.info("Version check failed");
                logger.catching(e);
                // check failed
            }
        }
    }

    public void onCrash(CrashReport crashReportIn) {
        ModLoader.INSTANCE.getInitializers().forEach(it -> it.onCrash(crashReportIn)); // call mod's crash event
        String message = crashReportIn.getCauseStackTraceOrString();
        JOptionPane.showMessageDialog(null, "Game crashed!\n" +
                        "\nDon't report this to Moonsworth\n" +
                        "Error dumped: " + crashReportIn.getFile().getPath() + "\n" +
                        "Please create a issue: \n" + GitUtils.remote.toString().split("\\.git")[0] + "/issues/new\n" +
                        "Please make a screenshot of this screen and send it to developers\n"
                        + message,
                "oops, game crashed!", JOptionPane.ERROR_MESSAGE);
    }

    public void onStop() {
        logger.info("Stopping!");
        ModLoader.INSTANCE.getInitializers().forEach(ModInitializer::onStop);
    }

    /**
     * Check LunarCN version
     */
    public void checkUpdate() throws IOException {
        // Can be disabled in config.json (key checkUpdate)
        logger.info("Checking " + CLIENT_NAME + " version...");
        // For Chinese developers: 因为GitHub在中国访问困难, 只好通过调用LunarCN API进行检查更新操作
        try (Response response = HttpUtils.get(CLIENT_API + "/api/web/latest").execute()) {
            if (response.body() != null) {
                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
                if (json.get("code").getAsInt() != 200) {
                    return; // error
                }
                JsonObject data = json.getAsJsonObject("data");
                String version = data.get("name").getAsString();
                // assert build version and latest releases
                DefaultArtifactVersion versionRemote = new DefaultArtifactVersion(version);
                DefaultArtifactVersion versionLocal = new DefaultArtifactVersion(CLIENT_VERSION);
                if (versionLocal.compareTo(versionRemote) < 0) {
                    // Outdated
                    mc.displayGuiScreen(new GuiOutdated(data, CLIENT_VERSION));
                }
            }
        }
    }
}
