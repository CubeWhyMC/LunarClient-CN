package org.cubewhy.lunarcn;

import lombok.Getter;
import net.minecraft.crash.CrashReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cubewhy.lunarcn.loader.ModLoader;
import org.cubewhy.lunarcn.loader.api.ModInitializer;
import org.cubewhy.lunarcn.loader.utils.GitUtils;
import org.cubewhy.lunarcn.utils.RenderUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.Display;
import org.cubewhy.lunarcn.files.Config;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.swing.*;
import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static org.cubewhy.lunarcn.utils.MinecraftInstance.mc;

public class LunarClient {
    public static final File configDir = new File(System.getProperty("configDir", System.getProperty("user.home") + "/.cubewhy" + "/lunarcn")); // config folder
    public static final String CLIENT_NAME = "LunarCN";
    // API source: https://github.com/CubeWhyMC/website
    public static final String CLIENT_API = System.getProperty("lunarcn.api", "https://api.lunarclient.top"); // No "/" in the end
    public static final String CLIENT_VERSION = GitUtils.gitInfo.getProperty("git.build.version");
    public static final Logger logger = LogManager.getLogger(CLIENT_NAME);
    public static final Config config = new Config(new File(configDir, "loader.json"));
    //    public static final boolean checkUpdate = config.getValue("check-update").getAsBoolean();
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
//        if (checkUpdate) {
//            Thread checkUpdateThread = new Thread(() -> {
//                try {
//                    checkUpdate();
//                } catch (Exception e) {
//                    logger.info("Version check failed");
//                    logger.catching(e);
//                }
//            });
//            checkUpdateThread.start(); // start thread
//        }
    }

    public void onCrash(@NotNull CrashReport crashReportIn) {
        ModLoader.INSTANCE.getInitializers().forEach(it -> it.onCrash(crashReportIn)); // call mod's crash event
        String message = crashReportIn.getCauseStackTraceOrString();
        String file = "Not Dumped";
        if (crashReportIn.getFile() != null) {
            file = crashReportIn.getFile().getPath();
        }
        JOptionPane.showMessageDialog(null, "Game crashed!\n" +
                        "\nDon't report this to Moonsworth\n" +
                        "Error dumped: " + file + "\n" +
                        "Please create a issue: \n" + GitUtils.remote.toString().split("\\.git")[0] + "/issues/new\n" +
                        "Please make a screenshot of this screen and send it to the LCCN developers\n"
                        + message,
                "oops, game crashed!", JOptionPane.ERROR_MESSAGE);
    }

    public void onStop() {
        logger.info("Stopping!");
        ModLoader.INSTANCE.getInitializers().forEach(ModInitializer::onStop);
    }

//    /**
//     * Check LunarCN version
//     */
//    public void checkUpdate() throws IOException {
//        // Can be disabled in config.json (key checkUpdate)
//        logger.info("Checking " + CLIENT_NAME + " version...");
//        // For Chinese developers: 因为GitHub在中国访问困难, 只好通过调用LunarCN API进行检查更新操作
//        try (Response response = HttpUtils.get(CLIENT_API + "/api/web/latest").execute()) {
//            if (response.body() != null) {
//                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
//                if (json.get("code").getAsInt() != 200) {
//                    return; // error
//                }
//                JsonObject data = json.getAsJsonObject("data");
//                String version = data.get("name").getAsString();
//                // assert build version and latest releases
//                DefaultArtifactVersion versionRemote = new DefaultArtifactVersion(version);
//                DefaultArtifactVersion versionLocal = new DefaultArtifactVersion(CLIENT_VERSION);
//                if (versionLocal.compareTo(versionRemote) < 0) {
//                    // Outdated
//                    mc.displayGuiScreen(new GuiOutdated(data, CLIENT_VERSION));
//                }
//            }
//        }
//    }
}
