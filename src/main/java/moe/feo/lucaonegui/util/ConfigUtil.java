package moe.feo.lucaonegui.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

public class ConfigUtil {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties props = new Properties();

    static {
        load();
    }

    private static void load() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return;

        try (InputStream in = new FileInputStream(file)) {
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            LogUtil.log("Failed to read config: " + e.getMessage());
        }
    }

    public static void save() {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(new OutputStreamWriter(out, StandardCharsets.UTF_8), "LucaOne GUI Config");
        } catch (IOException e) {
            LogUtil.log("Failed to save config: " + e.getMessage());
        }
    }

    public static void set(String key, String value) {
        props.setProperty(key, value);
        save();
    }

    public static String get(String key) {
        return props.getProperty(key, "");
    }

    public static Locale getLocale() {
        return new Locale(get("language"));
    }

    public static void setLocale(Locale locale) {
        set("language", locale.getLanguage());
    }

    public static String getOssAccessKeyId() {
        return get("OssAccessKeyId");
    }

    public static void setOssAccessKeyId(String value) {
        set("OssAccessKeyId", value);
    }

    public static String getOssAccessKeySecret() {
        return get("OssAccessKeySecret");
    }

    public static void setOssAccessKeySecret(String value) {
        set("OssAccessKeySecret", value);
    }

    public static String getOssEndpoint() {
        return get("ossEndpoint");
    }

    public static void setOssEndpoint(String value) {
        set("ossEndpoint", value);
    }

    public static String getOssRegion() {
        return get("ossRegion");
    }

    public static void setOssRegion(String value) {
        set("ossRegion", value);
    }

    public static String getBucketName() {
        return get("bucketName");
    }

    public static void setBucketName(String value) {
        set("bucketName", value);
    }

    public static String getLucaOneAk() {
        return get("LucaOneAk");
    }

    public static void setLucaOneAk(String value) {
        set("LucaOneAk", value);
    }

    public static String getLucaOneSk() {
        return get("LucaOneSk");
    }

    public static void setLucaOneSk(String value) {
        set("LucaOneSk", value);
    }

    public static String getWorkflowId() {
        return get("workflowId");
    }

    public static void setWorkflowId(String value) {
        set("workflowId", value);
    }

    public static String getAliasId() {
        return get("aliasId");
    }

    public static void setAliasId(String value) {
        set("aliasId", value);
    }
}
