package top.lunarclient.launcher;

import com.google.gson.JsonObject;

import java.io.File;

import static top.lunarclient.Main.config;
import static top.lunarclient.Main.configDir;

public class JavaAgents {
    public static final File javaAgentsDir = new File(configDir + "/javaagents");

    public static void init() {
        if (!javaAgentsDir.exists()) {
            javaAgentsDir.mkdirs();
        }
    }

    public static JavaAgent[] search() {
        File[] tree = javaAgentsDir.listFiles();

        if (tree == null) {
            return new JavaAgent[]{};
        }

        JavaAgent[] agents = new JavaAgent[tree.length];
        for (File file : tree) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                agents[agents.length - 1] = new JavaAgent(file.getAbsolutePath(), getAgentArg(file.getName()));
            }
        }
        return agents;
    }

    public static String getAgentArg(String agentName) {
        if (!config.getValue("java-agents").getAsJsonObject().has(agentName)) {
            JsonObject agentConfig = config.getValue("java-agents").getAsJsonObject();
            agentConfig.addProperty(agentName, "");
            config.setValue("java-agents", agentConfig);
        }
        return config.getValue("java-agents").getAsJsonObject().get(agentName).getAsString();
    }
}
