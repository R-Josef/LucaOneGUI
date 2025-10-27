package moe.feo.lucaonegui.ui;

import moe.feo.lucaonegui.polling.PollingManager;
import moe.feo.lucaonegui.util.I18n;
import moe.feo.lucaonegui.util.LanguageUtil;
import moe.feo.lucaonegui.util.TextUpdatable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainWindow extends JFrame {
    private JToolBar toolBar;
    private JPanel contentPanel;
    private SubmitPanel submitPanel;
    private HistoryPanel historyPanel;
    private JComboBox<String> languageBox;
    private List<TextUpdatable> updatables = new ArrayList<>();

    public MainWindow() {
        setTitle("LucaOne GUI");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initToolbar();
        initContent();

        setVisible(true);
    }

    private void initToolbar() {
        toolBar = new JToolBar();

        JButton submitButton = new JButton(I18n.get("toolbar.submit"));
        JButton historyButton = new JButton(I18n.get("toolbar.history"));
        JButton loginButton = new JButton(I18n.get("toolbar.login"));

        submitButton.addActionListener(e -> switchPanel("submit"));
        historyButton.addActionListener(e -> switchPanel("history"));
        loginButton.addActionListener(e -> new SettingWindow(() -> {
            JOptionPane.showMessageDialog(this, I18n.get("setting.updated"));
        }).setVisible(true));

        toolBar.add(submitButton);
        toolBar.add(historyButton);
        toolBar.add(loginButton);

        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(new JLabel(I18n.get("language.language") + ": "));

        List<String> langs = LanguageUtil.getAvailableLanguages();
        languageBox = new JComboBox<>(langs.toArray(new String[0]));
        languageBox.setSelectedItem(I18n.getCurrentLocale().getLanguage());

        languageBox.addActionListener(e -> {
            String selectedLang = (String) languageBox.getSelectedItem();
            Locale newLocale = new Locale(selectedLang);
            I18n.setLocale(newLocale);
            refreshTexts();
        });

        toolBar.add(languageBox);
        add(toolBar, BorderLayout.NORTH);
    }

    private void initContent() {
        contentPanel = new JPanel(new CardLayout());
        submitPanel = new SubmitPanel(PollingManager.getInstance());
        historyPanel = new HistoryPanel(PollingManager.getInstance());

        updatables.add(submitPanel);
        updatables.add(historyPanel);

        contentPanel.add(submitPanel, "submit");
        contentPanel.add(historyPanel, "history");

        add(contentPanel, BorderLayout.CENTER);
        switchPanel("submit");
    }

    private void switchPanel(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
    }

    private void refreshTexts() {
        ((JButton) toolBar.getComponent(0)).setText(I18n.get("toolbar.submit"));
        ((JButton) toolBar.getComponent(1)).setText(I18n.get("toolbar.history"));
        ((JButton) toolBar.getComponent(2)).setText(I18n.get("toolbar.login"));
        ((JLabel) toolBar.getComponent(4)).setText(I18n.get("language.language") + ": ");

        for (TextUpdatable panel : updatables) {
            panel.updateText();
        }
    }

    public HistoryPanel getHistoryPanel() {
    	return historyPanel;
    }
}
