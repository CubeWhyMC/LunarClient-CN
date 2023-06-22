package org.cubewhy.lunarcn;

import com.google.gson.JsonObject;
import org.cubewhy.lunarcn.files.Config;
import org.cubewhy.lunarcn.gui.Gui;
import org.cubewhy.lunarcn.launcher.JavaAgents;
import org.cubewhy.lunarcn.launcher.Launcher;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static final File configDir = new File(System.getProperty("user.home") + "/.cubewhy" + "/lunarcn"); // 配置文件目录
    public static final File launchScript = new File(configDir, "launch.bat");

    public static Config config; // 配置文件

    public Main() {
        if (config.getConfig().isEmpty()) {
            // 初始化值
            config.setValue("jre", ""); // 自定义JRE
            config.setValue("jvm-args", ""); // 自定义JVM参数
            config.setValue("args", ""); // 自定义游戏参数
            config.setValue("java-agents", new JsonObject());
        }

        JavaAgents.init(); // 初始化JavaAgents文件夹
    }

    private int start(String[] args) {
        StringBuilder execArgs = Launcher.buildArgs(args);
        System.out.println(execArgs);
        try {
            if (!launchScript.exists()) {
                launchScript.createNewFile();
            }
            FileWriter writer = new FileWriter(launchScript);
            writer.write("@echo off\n");
            writer.write("cd " + System.getProperty("user.dir") + "\n");
            writer.write(execArgs.toString() + "\n");
            writer.write("exit\n");
            writer.close();

            Process process = Runtime.getRuntime().exec(execArgs.toString());
        } catch (IOException e) {
            Gui.showErrorDialog("Game crashed!\nLunarCN只是检测到了报错, 并不能解决报错, 请勿向LunarClient提交错误报告时说明你在使用LunarCN!\n你也可以去我们的官方群组询问 -> 780154857");
            return 1;
        }
        return 0;
    }

    public static void main(@NotNull String[] args) {
        config = new Config(configDir + "/config.json");
        if (args.length == 0) {
            if (launchScript.exists()) {
                // 离线启动
                try {
                    Runtime.getRuntime().exec("cmd /Q /c start \"" + launchScript.getAbsolutePath() + "\"");
                } catch (IOException e) {
                    Gui.showErrorDialog("离线启动失败, 可能你没有使用Windows系统");
                    System.exit(1);
                }
            } else {
                Gui.showMessageDialog("该程序需要搭配LunarClient CN进行使用!\n下载地址: lunarcn.top");
                System.exit(1);
            }
        } else {
            System.exit(new Main().start(args));
        }
    }
}