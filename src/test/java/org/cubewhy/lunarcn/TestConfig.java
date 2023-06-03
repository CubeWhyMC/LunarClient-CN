package org.cubewhy.lunarcn;

import org.cubewhy.lunarcn.files.Config;

public class TestConfig {
    public void test() {
        Config config = new Config(Main.configDir + "/test.json");
        config.setValue("test", "1");
        System.out.println(config.getConfigFile());
    }

    public static void main(String[] args) {
        new TestConfig().test();
    }
}
