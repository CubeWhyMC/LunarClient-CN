package org.cubewhy.lunarcn.files;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.*;

public class Config {
    private JsonObject config;
    private final File configFile;

    public Config(String configFilePath) {
        this.configFile = new File(configFilePath);
        this.load();
    }

    public Config(File configFile) {
        this.configFile = configFile;
        this.load();
    }

    public void setValue(String key, String value) {
        this.config.addProperty(key, value);
        this.save();
    }

    public void setValue(String key, char value) {
        this.config.addProperty(key, value);
        this.save();
    }

    public void setValue(String key, int value) {
        this.config.addProperty(key, value);
        this.save();
    }

    public void setValue(String key, boolean value) {
        this.config.addProperty(key, value);
        this.save();
    }

    public void setValue(String key, JsonObject value) {
        this.config.add(key, value);
        this.save();
    }

    public JsonPrimitive getValue(String key) {
        return this.config.getAsJsonPrimitive(key);
    }

    public void save() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
            bufferedWriter.write(config.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void load() {
        Gson gson = new Gson();
        BufferedReader bufferedReader;
        boolean successful = false;

        while (!successful) {
            try {
                bufferedReader = new BufferedReader(new FileReader(configFile));
                config = gson.fromJson(bufferedReader, JsonObject.class);
                if (config == null) {
                    config = new JsonObject();
                }
                successful = true;
            } catch (FileNotFoundException e) {

                try {
                    if (!configFile.getParentFile().exists()) {
                        configFile.getParentFile().mkdirs();
                    }
                    configFile.createNewFile();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }

    public File getConfigFile() {
        return configFile;
    }


    public JsonObject getConfig() {
        return config;
    }
}
