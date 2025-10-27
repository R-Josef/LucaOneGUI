package moe.feo.lucaonegui.polling;

import com.alibaba.fastjson.JSONObject;
import moe.feo.lucaonegui.api.LucaOneClient;
import moe.feo.lucaonegui.util.ConfigUtil;
import moe.feo.lucaonegui.util.I18n;
import moe.feo.lucaonegui.util.LogUtil;

import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 独立轮询模块（单例）：负责轮询任务状态并维护结果
 */
public class PollingManager {
    private static final PollingManager INSTANCE = new PollingManager();

    private String currentMainId;
    private JSONObject currentSubTasks;
    private final Map<String, String> statusMap = new ConcurrentHashMap<>();
    private boolean pollingActive = false;

    private PollingManager() {}

    public static PollingManager getInstance() {
        return INSTANCE;
    }

    public synchronized void startPolling(String mainId, JSONObject subTasks) {
        if (pollingActive) {
            LogUtil.log("Already has a polling mission, please wait: " + mainId);
            return;
        }

        this.currentMainId = mainId;
        this.currentSubTasks = subTasks;
        this.statusMap.clear();
        this.pollingActive = true;

        new Thread(() -> {
            LucaOneClient client = new LucaOneClient();
            File outputDir = new File(currentMainId);
            outputDir.mkdir();

            while (true) {
                boolean allDone = true;

                for (String key : currentSubTasks.keySet()) {
                    if ("succeeded".equalsIgnoreCase(statusMap.get(key))) continue;

                    String taskId = currentSubTasks.getString(key);
                    JSONObject result = client.pollStatus(taskId, ConfigUtil.getWorkflowId(), ConfigUtil.getAliasId());

                    String status = I18n.get("history.unknown");
                    String downloadUrl = null;

                    if (result != null) {
                        JSONObject data = result.getJSONObject("data");
                        if (data != null) {
                            status = data.getString("status");
                            if ("succeeded".equalsIgnoreCase(status)) {
                                downloadUrl = data.getString("currentImage");
                            }
                        }
                    }

                    if ("succeeded".equalsIgnoreCase(status) && downloadUrl != null && !downloadUrl.isBlank()) {
                        try (InputStream in = new URL(downloadUrl).openStream();
                             OutputStream out = new FileOutputStream(new File(outputDir, taskId + ".zip"))) {
                            in.transferTo(out);
                            statusMap.put(key, status);
                            LogUtil.log("Succeeded to download task " + taskId);
                        } catch (Exception e) {
                            statusMap.put(key, "download_failed");
                            LogUtil.log("Failed to download task " + taskId + ": " + e.getMessage());
                            allDone = false;
                        }
                    } else {
                        statusMap.put(key, status != null ? status : I18n.get("history.unknown"));
                        if (!"succeeded".equalsIgnoreCase(status)) {
                            allDone = false;
                        }
                    }
                }

                if (allDone) {
                    pollingActive = false;
                    LogUtil.log("Polling finished: " + currentMainId);
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    public String getCurrentMainId() {
        return currentMainId;
    }

    public Map<String, String> getStatusMap() {
        return statusMap;
    }

    public String getStatus(String chunkKey) {
        return statusMap.getOrDefault(chunkKey, I18n.get("history.unknown"));
    }

    public boolean isPollingActive() {
        return pollingActive;
    }

    public Set<String> getChunkKeys() {
        return statusMap.keySet();
    }
}
