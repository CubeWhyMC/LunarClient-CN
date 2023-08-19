package top.lunarclient;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import top.lunarclient.files.Config;
import top.lunarclient.launcher.JavaAgent;
import top.lunarclient.launcher.JavaAgents;
import top.lunarclient.utils.FileUtils;
import top.lunarclient.utils.GitUtils;

import javax.swing.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main {
    public static final File configDir = new File(System.getProperty("configDir", System.getProperty("user.home") + "/.cubewhy" + "/lunarcn")); // 配置文件目录
    public static String os = System.getProperty("os.name");
    public static final File launchScript = new File(configDir, (os.equals("Windows")) ? "launch.bat" : "launch.sh");

    public static Config config = new Config(new File(configDir, "config.json")); // configFile
    public static String version = GitUtils.buildVersion;
    public static Locale locale = Locale.getDefault();
    public static ResourceBundle formatter = ResourceBundle.getBundle("launcher", locale);
    public static String userLanguage = locale.getLanguage();

    public static void main(String[] args) throws IOException {
        System.setProperty("file.encoding", "UTF-8"); // unicode support
        System.out.println("LunarCN Launcher " + version);
        System.out.println("[Debug] OS: " + os);
        System.out.println("[Debug] Display language: " + userLanguage);
        System.out.println("[Debug] Test display language(default to English): " + formatter.getString("hello"));
        System.out.println("Star us -> " + GitUtils.gitRemote);
        // do init values
        config
                .initValue("jre", new JsonPrimitive(""))
                .initValue("jvm-args", new JsonPrimitive(""))
                .initValue("args", new JsonPrimitive(""))
                .initValue("inject-loader", new JsonPrimitive(true))
                .initValue("java-agents", new JsonObject());
        JavaAgents.init(); // init folder
        boolean loaderState = config.getValue("inject-loader").getAsBoolean();

        if (args.length == 0) {
            JOptionPane.showMessageDialog(null, formatter.getString("err_noarg"), "LunarCN", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                String execArgs = buildArgs(args, loaderState).toString();
                System.out.println(execArgs);
                // dump args
                if (launchScript.createNewFile()) {
                    System.out.println(launchScript + " created successful");
                }
                FileWriter writer = new FileWriter(launchScript);
                if (os.contains("Windows")) {
                    writer.write("@echo off\n");
                    writer.write("cd /d " + FileUtils.getWorkingDir() + "\n");
                    writer.write(execArgs + "\n");
                    writer.write("pause\n");
                } else {
                    writer.write("# " + formatter.getString("script-info") + "\n");
                    writer.write("cd " + FileUtils.getWorkingDir() + "\n");
                    writer.write(execArgs + "\n");
                }
                writer.close();
                // start game
                Runtime.getRuntime().exec(execArgs);
            } catch (IOException e) {
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                JOptionPane.showMessageDialog(null, MessageFormat.format(formatter.getString("launch-error-message"), writer), "LunarCN | " + formatter.getString("launch-error-title"), JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }

    @NotNull
    public static StringBuilder buildArgs(String[] args, boolean loaderState) {
        String javaExec;
        // get java executable
        if (config.getValue("jre").getAsString().isEmpty()) {
            javaExec = System.getProperty("java.home") + ((os.contains("Windows")) ? "/bin/javaw.exe" : "/bin/java");
        } else {
            javaExec = config.getValue("jre").getAsString();
        }
        StringBuilder exec = new StringBuilder(javaExec);

        String argString = String.join(" ", args);
        // search JavaAgents on javaagents folder
        JavaAgent[] javaAgents = JavaAgents.search();
        exec.append(" ");
        exec.append(config.getValue("jvm-args").getAsString());
        exec.append(" ");
        // add LunarCN Loader
        String loaderPath = System.getProperty("loader", null);
        if (loaderState && loaderPath != null || new File(Objects.requireNonNull(loaderPath)).exists()) {
            exec.append(new JavaAgent(loaderPath).getJvmArgs());
        }
        // add JavaAgents
        for (JavaAgent agent : javaAgents) {
            exec.append(agent.getJvmArgs()).append(" ");
        }
        // Add custom game args
        exec.append(argString);
        exec.append(" ");
        exec.append(config.getValue("args").getAsString());
        return exec;
    }
}
