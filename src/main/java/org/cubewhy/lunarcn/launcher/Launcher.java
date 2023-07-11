package org.cubewhy.lunarcn.launcher;

import org.cubewhy.lunarcn.JavaAgent;

import static org.cubewhy.lunarcn.Main.config;

public class Launcher {
    public static StringBuilder buildArgs(String[] args) {
        String javaExec;
        if (config.getValue("jre").getAsString().isEmpty()) {
            javaExec = System.getProperty("java.home") + "/bin/javaw";
        } else {
            javaExec = config.getValue("jre").getAsString();
        }
        StringBuilder exec = new StringBuilder(javaExec);

        String argString = String.join(" ", args);
        JavaAgent[] javaAgents = JavaAgents.search();
        exec.append(" ");
        exec.append(config.getValue("jvm-args").getAsString());
        exec.append(" ");

        for (JavaAgent agent : javaAgents) {
            exec.append(agent.getJvmArgs()).append(" ");
        }

        exec.append(argString);
        exec.append(" ");
        exec.append(config.getValue("args").getAsString());
        return exec;
    }
}
