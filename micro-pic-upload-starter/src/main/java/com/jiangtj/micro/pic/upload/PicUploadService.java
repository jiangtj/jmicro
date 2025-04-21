package com.jiangtj.micro.pic.upload;

import com.jiangtj.micro.common.utils.FileNameUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

/**
 * 图片上传服务
 */
@Slf4j
@RequiredArgsConstructor
public class PicUploadService {

    private final PicUploadProperties properties;

    /**
     * 上传图片
     *
     * @param file 上传的文件
     * @return 上传结果，包含文件路径等信息
     * @throws IOException 如果文件操作失败
     */
    public PicUploadResult upload(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > properties.getMaxFileSize()) {
            throw new IllegalArgumentException("文件大小超过限制");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        // 检查文件类型
        if (!isAllowedExtension(extension)) {
            throw new IllegalArgumentException("不支持的文件类型");
        }

        // 创建上传目录
        Path uploadDir = Paths.get(properties.getUploadPath());
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成随机文件名
        String newFileName = FileNameUtils.getRandomFileNameWithSuffix(extension);
        Path targetPath = uploadDir.resolve(newFileName);

        // 保存文件
        file.transferTo(targetPath.toFile());

        // 构建结果
        PicUploadResult result = new PicUploadResult();
        result.setOriginalFileName(originalFilename);
        result.setFileName(newFileName);
        result.setFilePath(targetPath.toString());
        result.setFileSize(file.getSize());
        result.setFileType(file.getContentType());

        log.info("图片上传成功: {}", result);
        return result;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * 检查文件类型是否允许
     */
    private boolean isAllowedExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        return Arrays.asList(properties.getAllowedExtensions()).contains(extension.toLowerCase());
    }
}