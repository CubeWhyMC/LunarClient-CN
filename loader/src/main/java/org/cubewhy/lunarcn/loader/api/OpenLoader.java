package org.cubewhy.lunarcn.loader.api;

import org.cubewhy.lunarcn.loader.ModLoader;

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
     * */
    public static Path getModFolder() {
        return ModLoader.INSTANCE.getOrCreateModDirectory();
    }

    /**
     * Write access for LunarClient instance
     * */
    public static void writeAccess(String code) {
        ModLoader.INSTANCE.writeAccess(code);
    }
}
