package top.lunarclient;

import lombok.Getter;
import net.minecraft.crash.CrashReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cubewhy.lunarcn.loader.ModLoader;
import org.cubewhy.lunarcn.loader.api.ModInitializer;
import org.cubewhy.lunarcn.loader.utils.GitUtils;
import org.lwjgl.opengl.Display;

import javax.swing.*;

public class LunarClient {
    public static final String CLIENT_NAME = "LunarCN";
    public static final String CLIENT_VERSION = GitUtils.gitInfo.getProperty("git.build.version");
    @Getter
    private static final LunarClient instance = new LunarClient();
    public static final Logger logger = LogManager.getLogger(CLIENT_NAME);

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
    }

    public void onCrash(CrashReport crashReportIn) {
        ModLoader.INSTANCE.getInitializers().forEach(ModInitializer::onCrash);
        String message = crashReportIn.getCauseStackTraceOrString();
        JOptionPane.showMessageDialog(null, "Game crashed!\n" +
                        "\nDon't report this to Moonsworth\n" +
                        "Please create a issue: \n" + GitUtils.remote.toString().split("\\.git")[0] + "/issues/new\n" +
                        "Please make a screenshot of this screen and send it to developers\n"
                        + message,
                "oops, game crashed!", JOptionPane.ERROR_MESSAGE);
    }

    public void onStop() {
        logger.info("Stopping!");
        ModLoader.INSTANCE.getInitializers().forEach(ModInitializer::onStop);
    }
}
