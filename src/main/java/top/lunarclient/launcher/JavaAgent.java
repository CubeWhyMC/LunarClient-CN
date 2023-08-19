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
     * 获取附加参数
     * @return args
     * */
    public String getArgs() {
        return args;
    }

    /**
     * 获取JavaAgent文件
     * @return file
     * */
    public File getFile() {
        return file;
    }

    /**
     * 获取实际执行时要添加的参数
     * @return args for jvm
     * */
    public String getJvmArgs() {
        String jvmArgs = "-javaagent:\"" + this.file.getAbsolutePath() + "\"";
        if (!this.args.isEmpty()) {
            jvmArgs += "=" + this.args;
        }
        return jvmArgs;
    }
}
