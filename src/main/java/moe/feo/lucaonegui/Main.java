package moe.feo.lucaonegui;

import moe.feo.lucaonegui.ui.MainWindow;
import moe.feo.lucaonegui.util.I18n;

import javax.swing.*;
import java.util.Locale;

public class Main {

    public static MainWindow MAINWINDOW = null;
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        // 设置默认语言（从配置文件加载）
        Locale locale = I18n.getCurrentLocale();
        I18n.setLocale(locale);

        // 设置 Swing 使用系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Cannot set system layout: " + e.getMessage());
        }

        // 启动主窗口

        SwingUtilities.invokeLater(() -> MAINWINDOW = new MainWindow());
    }
}
