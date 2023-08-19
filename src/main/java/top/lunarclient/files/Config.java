package top.lunarclient.files;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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

    public Config setValue(String key, String value) {
        this.config.addProperty(key, value);
        return this.save();
    }

    public Config setValue(String key, char value) {
        this.config.addProperty(key, value);
        return this.save();
    }

    public Config setValue(String key, int value) {
        this.config.addProperty(key, value);
        return this.save();
    }

    public Config setValue(String key, boolean value) {
        this.config.addProperty(key, value);
        return this.save();
    }

    public Config setValue(String key, JsonObject value) {
        this.config.add(key, value);
        return this.save();
    }

    public Config initValue(String key, JsonElement value) {
        if (!this.config.has(key)) {
            this.config.add(key, value);
        }
        return this;
    }

    public JsonPrimitive getValue(String key) {
        return this.config.getAsJsonPrimitive(key);
    }

    public Config save() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
            bufferedWriter.write(config.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }


    public Config load() {
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
        return this;
    }

    public File getConfigFile() {
        return configFile;
    }


    public JsonObject getConfig() {
        return config;
    }
}
