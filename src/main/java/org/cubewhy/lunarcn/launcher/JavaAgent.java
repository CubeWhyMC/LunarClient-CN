package org.cubewhy.lunarcn.launcher;

import java.io.File;

public class JavaAgent {
    private String args = "";
    private final File file;

    public JavaAgent(String path, String args) {
        this.file = new File(path);
        this.args = args;
    }

    public JavaAgent(String path) {
        this.file = new File(path);
    }

    public String getArgs() {
        return args;
    }

    public File getFile() {
        return file;
    }

    public String getExecArgs() {
        String jvmArgs = "-javaagent:\"" + this.file.getAbsolutePath() + "\"";
        if (!this.args.isEmpty()) {
            jvmArgs += "=" + this.args;
        }
        return jvmArgs;
    }
}
