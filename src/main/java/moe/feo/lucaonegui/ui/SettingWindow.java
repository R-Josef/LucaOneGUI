package moe.feo.lucaonegui.ui;

import moe.feo.lucaonegui.util.ConfigUtil;
import moe.feo.lucaonegui.util.I18n;

import javax.swing.*;
import java.awt.*;

public class SettingWindow extends JDialog {
    private JTextField ossAccessKeyIDField;
    private JTextField ossAccessKeySecretField;
    private JTextField ossEndpointField;
    private JTextField ossRegionField;
    private JTextField bucketNameField;
    private JTextField lucaOneAkField;
    private JTextField lucaOneSkField;
    private JTextField workflowIdField;
    private JTextField aliasIdField;

    public SettingWindow(Runnable onSaveCallback) {
        setTitle(I18n.get("setting.title"));
        setModal(true);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 输入区域
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        ossAccessKeyIDField = new JTextField(ConfigUtil.getOssAccessKeyId());
        ossAccessKeySecretField = new JTextField(ConfigUtil.getOssAccessKeySecret());
        ossEndpointField = new JTextField(ConfigUtil.getOssEndpoint());
        ossRegionField = new JTextField(ConfigUtil.getOssRegion());
        bucketNameField = new JTextField(ConfigUtil.getBucketName());
        lucaOneAkField = new JTextField(ConfigUtil.getLucaOneAk());
        lucaOneSkField = new JTextField(ConfigUtil.getLucaOneSk());
        workflowIdField = new JTextField(ConfigUtil.getWorkflowId());
        aliasIdField = new JTextField(ConfigUtil.getAliasId());

        addRow(inputPanel, gbc, I18n.get("setting.ossaccesskeyid"), ossAccessKeyIDField);
        addRow(inputPanel, gbc, I18n.get("setting.ossaccesskeysecret"), ossAccessKeySecretField);
        addRow(inputPanel, gbc, I18n.get("setting.ossendpoint"), ossEndpointField);
        addRow(inputPanel, gbc, I18n.get("setting.ossregion"), ossRegionField);
        addRow(inputPanel, gbc, I18n.get("setting.ossbucketname"), bucketNameField);
        addRow(inputPanel, gbc, I18n.get("setting.lucaoneak"), lucaOneAkField);
        addRow(inputPanel, gbc, I18n.get("setting.lucaonesk"), lucaOneSkField);
        addRow(inputPanel, gbc, I18n.get("setting.workflowid"), workflowIdField);
        addRow(inputPanel, gbc, I18n.get("setting.aliasid"), aliasIdField);

        add(inputPanel, BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton(I18n.get("setting.save"));
        JButton cancelButton = new JButton(I18n.get("setting.cancel"));

        saveButton.addActionListener(e -> {
            String key = ossAccessKeyIDField.getText().trim();
            String secret = ossAccessKeySecretField.getText().trim();
            String endpoint = ossEndpointField.getText().trim();
            String region = ossRegionField.getText().trim();
            String bucket = bucketNameField.getText().trim();
            String lucaOneAk = lucaOneAkField.getText().trim();
            String lucaOneSk = lucaOneSkField.getText().trim();
            String workflowId = workflowIdField.getText().trim();
            String aliasId = aliasIdField.getText().trim();

            if (!key.isEmpty()) ConfigUtil.setOssAccessKeyId(key);
            if (!secret.isEmpty()) ConfigUtil.setOssAccessKeySecret(secret);
            if (!endpoint.isEmpty()) ConfigUtil.setOssEndpoint(endpoint);
            if (!region.isEmpty()) ConfigUtil.setOssRegion(region);
            if (!bucket.isEmpty()) ConfigUtil.setBucketName(bucket);
            if (!lucaOneAk.isEmpty()) ConfigUtil.setLucaOneAk(lucaOneAk);
            if (!lucaOneSk.isEmpty()) ConfigUtil.setLucaOneSk(lucaOneSk);
            if (!workflowId.isEmpty()) ConfigUtil.setWorkflowId(workflowId);
            if (!aliasId.isEmpty()) ConfigUtil.setAliasId(aliasId);

            ConfigUtil.save();

            onSaveCallback.run();
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        field.setHorizontalAlignment(SwingConstants.LEFT); // text inside field is left-aligned

        // Label: left side of row, left-aligned
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);

        // Field: right side of row, component right-aligned
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(field, gbc);

        gbc.gridy++;
    }
}
