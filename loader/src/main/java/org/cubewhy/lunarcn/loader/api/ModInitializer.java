package org.cubewhy.lunarcn.loader.api;

import net.minecraft.crash.CrashReport;

public interface ModInitializer {
    default void onPreInit() {}
    default void onInit() {}
    default void onStart() {}
    default void onCrash(CrashReport crashReport) {}
    default void onStop() {}
}
