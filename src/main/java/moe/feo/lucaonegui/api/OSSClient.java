package moe.feo.lucaonegui.api;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import moe.feo.lucaonegui.util.ConfigUtil;

import java.io.File;

/**
 * 阿里云 OSS 上传工具类
 */
public class OSSClient {

    /**
     * 上传文件到指定 OSS 路径
     * @param localFile 本地文件
     * @param objectKey OSS 中的对象路径（如 lucaone/task123/file1.txt）
     * @return OSS 文件访问链接
     */
    public static String upload(File localFile, String objectKey) {
        String endpoint = ConfigUtil.getOssEndpoint();
        String accessKeyId = ConfigUtil.getOssAccessKeyId();
        String accessKeySecret = ConfigUtil.getOssAccessKeySecret();
        String bucketName = ConfigUtil.getBucketName();

        OSS oss = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, localFile);
            oss.putObject(request);
        } finally {
            oss.shutdown(); // 释放资源
        }

        return "https://" + bucketName + "." + endpoint + "/" + objectKey;
    }
}
