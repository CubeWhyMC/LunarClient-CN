package org.cubewhy.lunarcn.launcher;

import com.google.gson.JsonObject;

import java.io.File;

import static org.cubewhy.lunarcn.Main.configDir;
import static org.cubewhy.lunarcn.Main.config;

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
        if (!config.getConfig().getAsJsonObject("java-agents").has(agentName)) {
            JsonObject agentConfig = config.getConfig().getAsJsonObject("java-agents");
            agentConfig.addProperty(agentName, "");
            config.setValue("java-agents", agentConfig);
        }
        return config.getConfig().getAsJsonObject("java-agents").get(agentName).getAsString();
    }
}
