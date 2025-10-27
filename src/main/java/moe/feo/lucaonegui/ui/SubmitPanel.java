package moe.feo.lucaonegui.ui;

import moe.feo.lucaonegui.Main;
import moe.feo.lucaonegui.api.LucaOneClient;
import moe.feo.lucaonegui.api.OSSClient;
import moe.feo.lucaonegui.polling.PollingManager;
import moe.feo.lucaonegui.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class SubmitPanel extends JPanel implements TextUpdatable {
    private JComboBox<String> embeddingTypeBox;
    private JComboBox<String> vectorTypeBox;
    private JCheckBox matrixAddSpecialTokenBox;
    private JCheckBox embeddingCompleteBox;
    private JComboBox<String> truncTypeBox;
    private JTextField truncationLengthField;
    private JComboBox<String> seqTypeBox;
    private JTextField fixedLenField;

    private JButton fileSelectButton;
    private JLabel fileLabel;
    private JButton submitButton;
    private JTextArea statusArea;

    private File selectedFile;
    private final PollingManager pollingManager;

    public SubmitPanel(PollingManager pollingManager) {
        this.pollingManager = pollingManager;
        setLayout(new BorderLayout());

        JPanel configPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        configPanel.setBorder(BorderFactory.createTitledBorder(I18n.get("submit.config")));

        embeddingTypeBox = new JComboBox<>(new String[]{"matrix", "vector"});
        vectorTypeBox = new JComboBox<>(new String[]{"mean", "max", "cls"});
        matrixAddSpecialTokenBox = new JCheckBox(I18n.get("submit.matrix_add_special_token"), true);
        embeddingCompleteBox = new JCheckBox(I18n.get("submit.embedding_complete"), false);
        truncTypeBox = new JComboBox<>(new String[]{"right", "left"});
        truncationLengthField = new JTextField("2048");
        seqTypeBox = new JComboBox<>(new String[]{"gene", "prot"});
        fixedLenField = new JTextField("1024");

        embeddingTypeBox.addActionListener(e -> updateFieldStates());
        embeddingCompleteBox.addActionListener(e -> updateFieldStates());

        configPanel.add(new JLabel("embedding_type"));
        configPanel.add(embeddingTypeBox);
        configPanel.add(new JLabel("vector_type"));
        configPanel.add(vectorTypeBox);
        configPanel.add(new JLabel("matrix_add_special_token"));
        configPanel.add(matrixAddSpecialTokenBox);
        configPanel.add(new JLabel("embedding_complete"));
        configPanel.add(embeddingCompleteBox);
        configPanel.add(new JLabel("trunc_type"));
        configPanel.add(truncTypeBox);
        configPanel.add(new JLabel("truncation_seq_length"));
        configPanel.add(truncationLengthField);
        configPanel.add(new JLabel("seq_type"));
        configPanel.add(seqTypeBox);
        configPanel.add(new JLabel("embedding_fixed_len_a_time"));
        configPanel.add(fixedLenField);

        fileSelectButton = new JButton(I18n.get("submit.selectfile"));
        fileLabel = new JLabel(I18n.get("submit.didntselectfile"));
        fileSelectButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                fileLabel.setText(selectedFile.getName());
            }
        });

        submitButton = new JButton(I18n.get("submit.submit"));
        submitButton.addActionListener(e -> handleSubmit());

        JPanel filePanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(fileSelectButton);
        leftPanel.add(fileLabel);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(submitButton);
        filePanel.add(leftPanel, BorderLayout.WEST);
        filePanel.add(rightPanel, BorderLayout.EAST);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(configPanel);
        topPanel.add(filePanel);

        add(topPanel, BorderLayout.NORTH);

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        add(new JScrollPane(statusArea), BorderLayout.CENTER);

        updateFieldStates();
    }

    private void updateFieldStates() {
        String type = (String) embeddingTypeBox.getSelectedItem();
        boolean complete = embeddingCompleteBox.isSelected();

        vectorTypeBox.setEnabled("vector".equals(type));
        matrixAddSpecialTokenBox.setEnabled("matrix".equals(type));
        truncTypeBox.setEnabled(!complete);
        truncationLengthField.setEnabled(!complete);
    }

    @Override
    public void updateText() {
        embeddingCompleteBox.setText(I18n.get("submit.embedding_complete"));
        fileSelectButton.setText(I18n.get("submit.selectfile"));
        submitButton.setText(I18n.get("submit.submit"));
        fileLabel.setText(selectedFile == null ? I18n.get("submit.didntselectfile") : selectedFile.getName());
        ((TitledBorder) ((JPanel) ((JPanel) getComponent(0)).getComponent(0)).getBorder()).setTitle(I18n.get("submit.config"));
        repaint();
    }

    private void handleSubmit() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, I18n.get("submit.didntselectfile"));
            return;
        }

        if (pollingManager.isPollingActive()) {
            JOptionPane.showMessageDialog(this, I18n.get("submit.alreadyrunning"));
            return;
        }

        String embeddingType = (String) embeddingTypeBox.getSelectedItem();
        String vectorType = (String) vectorTypeBox.getSelectedItem();
        boolean matrixAddSpecialToken = matrixAddSpecialTokenBox.isSelected();
        boolean complete = embeddingCompleteBox.isSelected();
        String truncType = (String) truncTypeBox.getSelectedItem();
        String seqType = (String) seqTypeBox.getSelectedItem();

        int truncLength;
        int fixedLen;
        try {
            truncLength = Integer.parseInt(truncationLengthField.getText());
            fixedLen = Integer.parseInt(fixedLenField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, I18n.get("sublit.formaterror"));
            return;
        }

        statusArea.setText(I18n.get("status.reading") + "\n");
        List<String> chunks = FastaUtil.splitFasta(selectedFile, 500);
        statusArea.append(MessageFormat.format(I18n.get("status.splitcount"), chunks.size()) + "\n");

        String mainId = UUID.randomUUID().toString().substring(0, 8);
        Map<String, String> subTasks = new LinkedHashMap<>();
        JSONObject subTasksJson = new JSONObject();
        LucaOneClient client = new LucaOneClient();

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            statusArea.append(MessageFormat.format(I18n.get("status.submitting"), i) + "\n");

            File chunkFile = new File("chunk_" + i + ".fasta");
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(chunkFile), StandardCharsets.UTF_8))) {
                writer.write(chunk);
            } catch (IOException ex) {
                statusArea.append(I18n.get("status.failedsplit") + "\n");
                continue;
            }

            String objectKey = "lucaone/" + mainId + "/chunk_" + i + ".fasta";
            String ossUrl = OSSClient.upload(chunkFile, objectKey);
            chunkFile.delete();

            String taskId = client.submitEmbeddingTask(
                    ossUrl,
                    seqType != null ? seqType : "gene",
                    embeddingType != null ? embeddingType : "matrix",
                    vectorType != null ? vectorType : "mean",
                    truncType != null ? truncType : "right",
                    truncLength > 0 ? truncLength : 2048,
                    matrixAddSpecialToken,
                    complete,
                    fixedLen > 0 ? fixedLen : 1024
            );

            if (taskId != null) {
                subTasks.put(String.valueOf(i), taskId);
                subTasksJson.put(String.valueOf(i), taskId);
                statusArea.append(MessageFormat.format(I18n.get("status.success"), taskId) + "\n");
            } else {
                statusArea.append(I18n.get("status.failure") + "\n");
            }
        }

        TaskHistoryUtil.saveTask(mainId, subTasks);
        Main.MAINWINDOW.getHistoryPanel().refreshHistory();
        statusArea.append(MessageFormat.format(I18n.get("status.recorded"), mainId) + "\n");

        // 启动轮询任务（使用单例 PollingManager）
        moe.feo.lucaonegui.polling.PollingManager.getInstance().startPolling(mainId, subTasksJson);
    }
}