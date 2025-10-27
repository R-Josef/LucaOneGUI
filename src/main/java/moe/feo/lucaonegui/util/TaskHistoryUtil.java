package moe.feo.lucaonegui.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class TaskHistoryUtil {
    private static final String HISTORY_FILE = "history.json";

    public static void saveTask(String mainId, Map<String, String> subTasks) {
        JSONArray history = loadHistory();

        JSONObject record = new JSONObject();
        record.put("main_id", mainId);
        record.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        record.put("sub_tasks", JSONObject.parseObject(JSON.toJSONString(subTasks)));

        history.add(record);
        writeHistory(history);
    }

    private static JSONArray loadHistory() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) return new JSONArray();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            return JSONArray.parseArray(json.toString());
        } catch (IOException e) {
            LogUtil.log("Failed to read history file: " + e.getMessage());
            return new JSONArray();
        }
    }

    private static void writeHistory(JSONArray history) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(HISTORY_FILE), StandardCharsets.UTF_8))) {
            writer.write(history.toJSONString());
        } catch (IOException e) {
            LogUtil.log("Failed to wrote history file: " + e.getMessage());
        }
    }
}
