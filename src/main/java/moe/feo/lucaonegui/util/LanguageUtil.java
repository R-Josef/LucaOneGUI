package moe.feo.lucaonegui.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LanguageUtil {
    public static List<String> getAvailableLanguages() {
        List<String> langs = new ArrayList<>();
        try {
            // 使用 classpath 读取 lang/index.txt（打包后仍有效）
            InputStream in = LanguageUtil.class.getClassLoader().getResourceAsStream("lang/index.txt");
            if (in == null) {
                System.err.println("⚠️ lang/index.txt not found，language file list empty!");
                return langs;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("messages_") && line.endsWith(".properties")) {
                    String code = line.substring("messages_".length(), line.indexOf(".properties"));
                    langs.add(code);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return langs;
    }
}
