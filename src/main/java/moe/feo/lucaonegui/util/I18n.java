package moe.feo.lucaonegui.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
    private static Locale currentLocale = ConfigUtil.getLocale();
    private static ResourceBundle bundle = ResourceBundle.getBundle("lang.messages", currentLocale);

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        ConfigUtil.setLocale(locale);
        bundle = ResourceBundle.getBundle("lang.messages", currentLocale);
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}

