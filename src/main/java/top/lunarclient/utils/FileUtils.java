package top.lunarclient.utils;

import java.io.File;

public class FileUtils {
    public static String getWorkingDir() {
        return new File(".").getAbsolutePath();
    }
}
