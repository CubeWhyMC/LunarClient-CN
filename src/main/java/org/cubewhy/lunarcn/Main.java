package org.cubewhy.lunarcn;

import com.google.gson.JsonObject;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.cubewhy.launcher.LunarClient;
import org.cubewhy.launcher.LunarDownloader;
import org.cubewhy.launcher.game.MinecraftArgs;
import org.cubewhy.lunarcn.files.Config;
import org.cubewhy.lunarcn.gui.Gui;
import org.cubewhy.lunarcn.launcher.JavaAgents;
import org.cubewhy.lunarcn.launcher.Launcher;
import org.cubewhy.lunarcn.loader.utils.GitUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;

public class Main {

    public static final File mcDir = new File(System.getenv("%APPDATA%"), ".minecraft"); // TODO only work in windows
    public static final File configDir = new File(System.getProperty("user.home") + "/.cubewhy" + "/lunarcn"); // 配置文件目录
    public static final File launchScript = new File(configDir, "launch.bat");

    public static Config config; // configFile
    public static String clientLogo = "lunarcn/lunarcn.png";
    public static String version = GitUtils.gitInfo.getProperty("git.build.version");

    public Main() {
        if (config.getConfig().isEmpty()) {
            // init values
            config.setValue("jre", ""); // custom java runtime
            config.setValue("jvm-args", ""); // custom jvm args
            config.setValue("args", ""); // custom game args
            config.setValue("java-agents", new JsonObject()); // JavaAgents config
            config.save();
        }

        JavaAgents.init(); // 初始化JavaAgents文件夹
    }

    public static void main(@NotNull String[] args) throws IOException {
        config = new Config(configDir + "/config.json");
        config.load();
        if (args.length == 0) {
            Gui.showMessageDialog("本程序无法直接双击启动, 你可以使用如下参数\n--offline 离线启动\n--inject 运行时热注入");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("--offline")) {
            if (launchScript.exists()) {
                // 离线启动
                try {
                    Runtime.getRuntime().exec("cmd /Q /c start \"" + launchScript.getAbsolutePath() + "\"");
                } catch (IOException e) {
                    Gui.showErrorDialog("离线启动失败, 可能你没有使用Windows系统");
                    System.exit(1);
                }
            } else {
                Gui.showMessageDialog("你之前没有启动过LunarCN, 请正常启动一次LunarCN再使用此功能");
                System.exit(1);
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("--no-launcher")) {
            new Main().noLauncher(System.getProperty("version", "1.8.9"), System.getProperty("branch", "master"), System.getProperty("module", "lunar"));
        } else if (args.length == 1 && args[0].equalsIgnoreCase("--inject")) {
            System.exit(new Main().attach(args));
        } else {
            System.exit(new Main().start(args));
        }
    }

    private int start(String[] args) {
        StringBuilder execArgs = Launcher.buildArgs(args);
        try {
            if (!launchScript.exists()) {
                launchScript.createNewFile();
            }
            FileWriter writer = new FileWriter(launchScript);
            writer.write("@echo off\n");
            writer.write("cd " + System.getProperty("user.dir") + "\n");
            writer.write(execArgs + "\n");
            writer.write("pause\n");
            writer.close();

            Process process = Runtime.getRuntime().exec(execArgs.toString());
        } catch (IOException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            Gui.showErrorDialog("Launch failed!\nDon't report this to Moonsworth\n" + writer);
            return 1;
        }
        return 0;
    }

    private void noLauncher(String minecraftVersion, String branch, String module) throws IOException {
        JsonObject artifacts = LunarDownloader.getLunarArtifacts(minecraftVersion, branch, module);
        File baseDir = new File(configDir, "offline");
        MinecraftArgs mcArgs = new MinecraftArgs(mcDir.getAbsolutePath(), mcDir + "/textures", 300, 400);
        String javaExec;
        if (config.getValue("jre").getAsString().isEmpty()) {
            javaExec = System.getProperty("java.home") + "/bin/java";
        } else {
            javaExec = config.getValue("jre").getAsString();
        }
        String[] jvmArgs = config.getValue("jvm-args").getAsString().split(" ");
        String[] programArgs = config.getValue("args").getAsString().split(" ");
        JavaAgent[] agents = JavaAgents.search();
//        LunarDownloader.downloadLunarArtifacts(new File(configDir, "offline"), artifacts);
        LunarClient.launch(minecraftVersion, module, branch, baseDir.getAbsolutePath(), mcArgs, javaExec, jvmArgs, programArgs, agents);
    }

    private int attach(String[] args) {
        try {
            List<VirtualMachineDescriptor> virtualMachineDescriptors = VirtualMachine.list();
            boolean successful = false;
            for (VirtualMachineDescriptor descriptor :
                    virtualMachineDescriptors) {
                if (descriptor.displayName().split(" ")[0].equals("com.moonsworth.lunar.genesis.Genesis")) {
                    VirtualMachine vm = VirtualMachine.attach(descriptor.id());
                    vm.loadAgent(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
                    System.out.println("Load agent at pid " + descriptor.id());
                    successful = true;
                    break;
                }
            }

            if (!successful) {
                throw new Throwable("Lunar Client not found");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }
}