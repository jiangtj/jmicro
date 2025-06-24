package com.jiangtj.micro.pic.upload.local;

import com.jiangtj.micro.common.utils.FileNameUtils;
import com.jiangtj.micro.pic.upload.PicUploadProperties;
import com.jiangtj.micro.pic.upload.PicUploadProvider;
import com.jiangtj.micro.pic.upload.PicUploadResult;
import com.jiangtj.micro.pic.upload.PicUploadType;
import com.jiangtj.micro.pic.upload.ex.PicUploadInternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 图片上传服务
 */
@Slf4j
@PicUploadType("local")
@RequiredArgsConstructor
public class LocalPicUploadService implements PicUploadProvider {

    private final LocalPicUploadProperties properties;

    /**
     * 上传图片
     *
     * @param file 上传的文件
     * @return 上传结果，包含文件路径等信息
     */
    public PicUploadResult upload(PicUploadProperties.Dir dir, MultipartFile file) {
        try {
            // 创建上传目录
            Path baseDir = Paths.get(properties.getUploadPath());
            Path uploadDir = baseDir.resolve(dir.getPath());
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成随机文件名
            String extension = FileNameUtils.getFileExtension(file.getOriginalFilename());
            String newFileName = FileNameUtils.getRandomFileNameWithSuffix(extension);
            Path targetPath = uploadDir.resolve(newFileName);

            // 保存文件
            file.transferTo(targetPath);

            // 构建结果
            PicUploadResult result = new PicUploadResult();
            result.setFileName(newFileName);
            String url = URI.create(properties.getUrl()).resolve(newFileName).toString();
            result.setFileUrl(url);

            log.info("图片上传成功: {}", result);
            return result;
        } catch (IOException e) {
            log.error("获取图片失败", e);
            throw new PicUploadInternalException("获取图片失败！", e);
        }
    }
}