package top.lunarclient.launcher;

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

    /**
     * Get args
     * @return args
     * */
    public String getArgs() {
        return args;
    }

    /**
     * Get file
     * @return file
     * */
    public File getFile() {
        return file;
    }

    /**
     * Get VM args
     * @return args for jvm
     * */
    public String getJvmArgs() {
        String jvmArgs = "-javaagent:\"" + this.file.getAbsolutePath() + "\"";
        if (!this.args.isEmpty()) {
            jvmArgs += "=\"" + this.args + "\"";
        }
        return jvmArgs;
    }
}
