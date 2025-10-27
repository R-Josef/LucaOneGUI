package moe.feo.lucaonegui.api;

import com.alibaba.cloudapi.sdk.client.ApacheHttpClient;
import com.alibaba.cloudapi.sdk.constant.SdkConstant;
import com.alibaba.cloudapi.sdk.enums.HttpMethod;
import com.alibaba.cloudapi.sdk.enums.ParamPosition;
import com.alibaba.cloudapi.sdk.enums.Scheme;
import com.alibaba.cloudapi.sdk.model.ApiRequest;
import com.alibaba.cloudapi.sdk.model.ApiResponse;
import com.alibaba.cloudapi.sdk.model.HttpClientBuilderParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import moe.feo.lucaonegui.util.ConfigUtil;
import moe.feo.lucaonegui.util.LogUtil;

import java.util.Map;

/**
 * LucaOne 接口客户端（自动读取配置，无需初始化）
 */
public class LucaOneClient {

    private static final String HOST = "openai.edu-aliyun.com";
    private static final String SUBMIT_PATH = "/scc/comfy_prompt";
    private static final String GET_PATH = "/scc/comfy_get_progress";

    private ApacheHttpClient buildClient() {
        HttpClientBuilderParams params = new HttpClientBuilderParams();
        params.setAppKey(ConfigUtil.getLucaOneAk());
        params.setAppSecret(ConfigUtil.getLucaOneSk());
        params.setHost(HOST);
        params.setScheme(Scheme.HTTPS);

        ApacheHttpClient client = new ApacheHttpClient() {};
        client.init(params);
        return client;
    }

    private String postJson(Object jsonBody, Map<String, String> headers, String path) {
        try {
            ApacheHttpClient client = buildClient();

            ApiRequest request = new ApiRequest(HttpMethod.POST_BODY, path);
            byte[] body = JSON.toJSONString(jsonBody).getBytes(SdkConstant.CLOUDAPI_ENCODING);
            request.setBody(body);

            request.addHeader("Content-Type", "application/json; charset=UTF-8");
            request.addHeader("Accept", "application/json");
            request.addHeader("X-Ca-Stage", "RELEASE");

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.addParam(entry.getKey(), entry.getValue(), ParamPosition.HEAD, false);
                }
            }

            LogUtil.log("Request contents: " + JSON.toJSONString(jsonBody));
            ApiResponse response = client.sendSyncRequest(request);
            LogUtil.log("Response status code: " + response.getCode());
            LogUtil.log("Response contents: " + (response.getBody() != null ? new String(response.getBody(), SdkConstant.CLOUDAPI_ENCODING) : "null"));

            if (response != null && response.getBody() != null) {
                return new String(response.getBody(), SdkConstant.CLOUDAPI_ENCODING);
            } else {
                System.err.println("Response empty or failed, status code: " + (response != null ? response.getCode() : "null"));
                return null;
            }
        } catch (Exception e) {
            System.err.println("Failed to request: " + e.getMessage());
            return null;
        }
    }

    public String submitEmbeddingTask(String inputFile,
                                      String seqType,
                                      String embeddingType,
                                      String vectorType,
                                      String truncType,
                                      int truncationLength,
                                      boolean matrixAddSpecialToken,
                                      boolean embeddingComplete,
                                      int embeddingFixedLenATime) {

        JSONObject payload = new JSONObject();
        payload.put("workflow_id", ConfigUtil.getWorkflowId());
        payload.put("alias_id", ConfigUtil.getAliasId());

        JSONObject inputs = new JSONObject();
        inputs.put("input_file", inputFile != null ? inputFile : "");
        inputs.put("seq_type", seqType != null ? seqType : "");
        inputs.put("embedding_type", embeddingType != null ? embeddingType : "");
        inputs.put("vector_type", vectorType != null ? vectorType : "");
        inputs.put("trunc_type", truncType != null ? truncType : "");
        inputs.put("truncation_seq_length", truncationLength);
        inputs.put("matrix_add_special_token", String.valueOf(matrixAddSpecialToken));
        inputs.put("embedding_complete", String.valueOf(embeddingComplete));
        inputs.put("embedding_fixed_len_a_time", embeddingFixedLenATime);
        inputs.put("id_idx", 0);
        inputs.put("seq_idx", 1);

        payload.put("inputs", inputs);

        String response = postJson(payload, null, SUBMIT_PATH);
        if (response == null || response.trim().isEmpty()) return null;

        try {
            JSONObject result = JSON.parseObject(response);
            JSONObject data = result.getJSONObject("data");
            return data != null ? data.getString("taskId") : null;
        } catch (Exception e) {
            System.err.println("Failed to parse response: " + e.getMessage());
            return null;
        }
    }

    public JSONObject pollStatus(String taskId, String workflowId, String aliasId) {
        JSONObject payload = new JSONObject();
        payload.put("taskId", taskId);
        payload.put("workflow_id", workflowId);
        payload.put("alias_id", aliasId);

        String response = postJson(payload, null, GET_PATH);
        return response != null ? JSON.parseObject(response) : null;
    }

    public JSONObject fetchResult(String taskId, String workflowId, String aliasId) {
        JSONObject payload = new JSONObject();
        payload.put("taskId", taskId);
        payload.put("workflow_id", workflowId);
        payload.put("alias_id", aliasId);

        String response = postJson(payload, null, GET_PATH);
        return response != null ? JSON.parseObject(response) : null;
    }
}
