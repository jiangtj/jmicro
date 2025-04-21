package com.jiangtj.micro.pic.upload;

import com.jiangtj.micro.common.utils.FileNameUtils;
import com.jiangtj.micro.web.AnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 图片上传服务
 */
@Slf4j
public class PicUploadService {

    private final PicUploadProperties properties;
    private final Map<String, PicUploadProvider> providers;

    public PicUploadService(PicUploadProperties properties, List<PicUploadProvider> providers) {
        this.properties = properties;
        this.providers = providers.stream()
            .collect(Collectors.toMap(provider ->
                    AnnotationUtils.findAnnotation(provider.getClass(), PicUploadType.class)
                        .map(PicUploadType::value)
                        .orElse(provider.getClass().getName()),
                Function.identity()));
    }

    /**
     * 上传图片
     *
     * @param file 上传的文件
     * @return 上传结果，包含文件路径等信息
     * @throws IOException 如果文件操作失败
     */
    public PicUploadResult upload(String type, MultipartFile file) throws IOException {
        PicUploadProperties.Dir dir = properties.getDirs().get(type);
        if (dir == null) {
            throw new PicUploadException("不存在上传类型定义，请在配置文件中添加: micro.pic.upload." + type);
        }

        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new PicUploadException("上传的文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > dir.getMaxFileSize()) {
            throw new PicUploadException("文件大小超过限制");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = FileNameUtils.getFileExtension(originalFilename);

        // 检查文件类型
        if (!isAllowedExtension(dir, extension)) {
            throw new PicUploadException("不支持的文件类型");
        }

        PicUploadProvider provider = providers.get(properties.getProvider());
        if (provider == null) {
            throw new PicUploadException("不存在的图片上传服务");
        }

        PicUploadResult result = provider.upload(dir, file);
        result.setOriginalFileName(originalFilename);
        log.info("图片上传成功: {}", result);
        return result;
    }

    /**
     * 检查文件类型是否允许
     */
    private boolean isAllowedExtension(PicUploadProperties.Dir dir, String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        return Arrays.asList(dir.getAllowedExtensions()).contains(extension.toLowerCase());
    }
}