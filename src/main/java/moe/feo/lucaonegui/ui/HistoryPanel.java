package moe.feo.lucaonegui.ui;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import moe.feo.lucaonegui.polling.PollingManager;
import moe.feo.lucaonegui.util.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class HistoryPanel extends JPanel implements TextUpdatable {
    private JList<String> taskList;
    private DefaultListModel<String> taskListModel;
    private JTextArea taskDetailArea;
    private JLabel statusLabel;

    private final Map<String, JSONObject> taskMap = new LinkedHashMap<>();
    private final PollingManager pollingManager;
    private String selectedMainId;

    @Override
    public void updateText() {
        statusLabel.setText(I18n.get("history.staskstatus"));
    }

    public HistoryPanel(PollingManager pollingManager) {
        this.pollingManager = pollingManager;
        setLayout(new BorderLayout());

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedMainId = taskList.getSelectedValue().split(" ")[0];
                showTaskDetail();
            }
        });

        JScrollPane listScroll = new JScrollPane(taskList);
        listScroll.setPreferredSize(new Dimension(200, 0));
        add(listScroll, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        taskDetailArea = new JTextArea();
        taskDetailArea.setEditable(false);
        rightPanel.add(new JScrollPane(taskDetailArea), BorderLayout.CENTER);

        statusLabel = new JLabel(I18n.get("history.staskstatus"));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.GRAY);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setPreferredSize(new Dimension(100, 30));
        rightPanel.add(statusLabel, BorderLayout.NORTH);

        add(rightPanel, BorderLayout.CENTER);

        loadHistory();

        // 定时刷新轮询任务状态
        new Timer(1000, e -> refreshPollingStatus()).start();
    }

    private void loadHistory() {
        File file = new File("history.json");
        if (!file.exists()) return;

        try {
            String json = Files.readString(file.toPath());
            JSONArray history = JSONArray.parseArray(json);
            for (int i = 0; i < history.size(); i++) {
                JSONObject record = history.getJSONObject(i);
                String mainId = record.getString("main_id");
                String time = record.getString("time");
                taskListModel.addElement(mainId + " [" + time + "]");
                taskMap.put(mainId, record);
            }
        } catch (Exception e) {
            LogUtil.log("Failed to load history: " + e.getMessage());
        }
    }

    private void showTaskDetail() {
        if (selectedMainId == null || !taskMap.containsKey(selectedMainId)) return;

        JSONObject record = taskMap.get(selectedMainId);
        JSONObject subTasks = record.getJSONObject("sub_tasks");

        boolean isPolling = pollingManager.isPollingActive() &&
                selectedMainId.equals(pollingManager.getCurrentMainId());

        Map<String, String> statusMap = isPolling ? pollingManager.getStatusMap() : null;

        StringBuilder sb = new StringBuilder();
        for (String key : subTasks.keySet()) {
            String taskId = subTasks.getString(key);
            String status = isPolling
                    ? statusMap.getOrDefault(key, "⏳ " + I18n.get("history.loading"))
                    : "📦 " + I18n.get(("history.history"));

            sb.append(I18n.get("history.chunk")).append(" ").append(key).append(": ").append(taskId)
                    .append(" → ").append(status).append("\n");
        }

        taskDetailArea.setText(sb.toString());

        statusLabel.setText(isPolling ? I18n.get("history.taskrunning") : I18n.get("history.taskover"));
        statusLabel.setBackground(isPolling ? Color.GREEN : Color.GRAY);
    }

    private void refreshPollingStatus() {
        if (selectedMainId != null) {
            showTaskDetail();
        }
    }

    public void refreshHistory() {
        taskListModel.clear();
        taskMap.clear();
        loadHistory();
    }
}
