package org.cubewhy.lunarcn.loader.api;

import org.cubewhy.lunarcn.loader.ModLoader;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.jar.JarFile;

@SuppressWarnings("unused")
public class OpenLoader {
    private OpenLoader() {
    }

    /**
     * Load mod
     * @param jar Mod file
     * */
    public static void loadMod(JarFile jar) {
        ModLoader.INSTANCE.loadMod(jar);
    }

    /**
     * Get mod folder
     * @return path of ModFolder
     * */
    public static Path getModFolder() {
        return ModLoader.INSTANCE.getOrCreateModDirectory();
    }

    /**
     * Get the instrumentation instance
     * @return inst
     * */
    public static Instrumentation getInstrumentation() {
        return ModLoader.instrumentation;
    }
}
