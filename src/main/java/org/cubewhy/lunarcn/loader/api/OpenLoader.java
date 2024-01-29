package org.cubewhy.lunarcn.loader.api;

import org.cubewhy.lunarcn.loader.ModLoader;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
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
