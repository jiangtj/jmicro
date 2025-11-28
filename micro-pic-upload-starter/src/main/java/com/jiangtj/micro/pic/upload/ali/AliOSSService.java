package com.jiangtj.micro.pic.upload.ali;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.jiangtj.micro.pic.upload.*;
import com.jiangtj.micro.pic.upload.ex.PicUploadInternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@PicUploadType("ali")
public class AliOSSService implements PicUploadProvider {

    private final AliOSSProperties properties;
    ClientBuilderConfiguration clientBuilderConfiguration;
    CredentialsProvider credentialsProvider;
//    private final OSS ossClient;

    public AliOSSService(AliOSSProperties properties) {
        this.properties = properties;
        try {
            if (properties.getAccessKeyId() != null && properties.getSecretAccessKey() != null) {
                credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(properties.getAccessKeyId(), properties.getSecretAccessKey());
            } else {
                credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
            }
        } catch (com.aliyuncs.exceptions.ClientException e) {
            log.error("初始化阿里云OSS失败 CredentialsProvider 初始化失败", e);
            throw new RuntimeException(e);
        }

        clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
    }


    public String upload(String key, InputStream input) {
        OSS ossClient = OSSClientBuilder.create()
            .endpoint(properties.getEndpoint())
            .credentialsProvider(credentialsProvider)
            .clientConfiguration(clientBuilderConfiguration)
            .region(properties.getRegion())
            .build();

        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(properties.getBucketName(), key, input);
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            return properties.getUrl() + "/" + key;
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                + "but was rejected with an error response for some reason.");
            log.error("Error Message:{}", oe.getErrorMessage());
            log.error("Error Code:{}", oe.getErrorCode());
            log.error("Request ID:{}", oe.getRequestId());
            log.error("Host ID:{}", oe.getHostId());
            throw new PicUploadInternalException("AliOSS Client call fail!", oe);
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                + "a serious internal problem while trying to communicate with OSS, "
                + "such as not being able to access the network.");
            log.error("Error Message:{}", ce.getMessage());
            throw new PicUploadInternalException("AliOSS Client error!", ce);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public PicUploadResult upload(PicUploadProperties.Dir dir, MultipartFile file) {
        String name = PicUploadUtils.generateNewFileName(file);
        try {
            String url = upload(dir.resolve(name), file.getInputStream());
            return PicUploadResult.builder()
                .fileName(name)
                .fileUrl(url)
                .build();
        } catch (IOException e) {
            log.error("获取图片失败", e);
            throw new PicUploadInternalException("获取图片失败！", e);
        }
    }
}
