package com.jiangtj.micro.pic.upload.hw;

import com.jiangtj.micro.pic.upload.*;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@PicUploadType("hw")
public class ObsService implements PicUploadProvider {

    private final ObsProperties properties;

    public ObsService(ObsProperties properties) {
        this.properties = properties;
    }

    public String putObject(String key, InputStream input) {

        // 创建ObsClient实例
        // 使用永久AK/SK初始化客户端
        try (ObsClient obsClient = new ObsClient(properties.getKey(), properties.getSecret(), properties.getEndPoint())) {

            PutObjectResult result = obsClient.putObject(properties.getBucketName(), key, input);
            // log.error(JsonUtil.toJson(result));
            return result.getObjectUrl();

        } catch (ObsException e) {
            log.error("putObject failed", e);
            // 请求失败,打印http状态码
            log.error("HTTP Code:{}", e.getResponseCode());
            // 请求失败,打印服务端错误码
            log.error("Error Code:{}", e.getErrorCode());
            // 请求失败,打印详细错误信息
            log.error("Error Message:{}", e.getErrorMessage());
            // 请求失败,打印请求id
            log.error("Request ID:{}", e.getErrorRequestId());
            log.error("Host ID:{}", e.getErrorHostId());
        } catch (Exception e) {
            log.error("putObject failed", e);
        }
        throw new PicUploadException("文件上传错误!");
    }

    @Override
    public PicUploadResult upload(PicUploadProperties.Dir dir, MultipartFile file) {
        String name = PicUploadUtils.generateNewFileName(file);
        try {
            String url = putObject(dir.resolve(name), file.getInputStream());
            return PicUploadResult.builder()
                .fileName(name)
                .fileUrl(url)
                .build();
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new PicUploadException("上传失败！");
        }
    }
}
