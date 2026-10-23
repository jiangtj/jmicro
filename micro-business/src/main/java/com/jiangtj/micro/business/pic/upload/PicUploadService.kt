package com.jiangtj.micro.business.pic.upload;

import com.jiangtj.micro.business.config.SystemConfigService;
import com.jiangtj.micro.business.pic.upload.ex.PicUploadCheckException;
import com.jiangtj.micro.common.utils.FileNameUtils;
import com.jiangtj.micro.web.AnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 图片上传服务
 *
 * 上传目录映射优先读取系统配置项 {@code jmicro.pic.upload.dirs}（JSON），
 * 未配置时回退到 {@code jmicro.pic.upload.dirs.*} 的启动绑定值。
 */
@Slf4j
public class PicUploadService {

    private final PicUploadProperties properties;
    private final Map<String, PicUploadProvider> providers;

    @Nullable
    private final SystemConfigService systemConfigService;

    public PicUploadService(PicUploadProperties properties, List<PicUploadProvider> providers) {
        this(properties, providers, null);
    }

    public PicUploadService(PicUploadProperties properties, List<PicUploadProvider> providers,
                            @Nullable SystemConfigService systemConfigService) {
        this.properties = properties;
        this.systemConfigService = systemConfigService;
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
     */
    public PicUploadResult upload(String target, MultipartFile file) {
        PicUploadProperties.Dir dir = getDir(target);

        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new PicUploadCheckException("上传的文件不能为空");
        }

        // 检查文件大小
        if (dir.getMaxFileSize() != null && file.getSize() > dir.getMaxFileSize().toBytes()) {
            throw new PicUploadCheckException("文件大小超过限制");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = FileNameUtils.getFileExtension(originalFilename);

        // 检查文件类型
        if (!isAllowedExtension(dir, extension)) {
            throw new PicUploadCheckException("不支持的文件类型");
        }

        PicUploadProvider provider = providers.get(dir.getProvider());
        if (provider == null) {
            throw new PicUploadCheckException("不存在的图片上传服务");
        }

        PicUploadResult result = provider.upload(dir, file);
        result.setOriginalFileName(originalFilename == null ? "未知文件" : originalFilename);
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

    public PicUploadProperties.Dir getDir(String target) {
        if (!StringUtils.hasText(target)) {
            throw new PicUploadCheckException("请提供上传目标");
        }
        PicUploadProperties.Dir dir = resolveDirs().get(target);
        if (dir == null) {
            throw new PicUploadCheckException("不存在上传的目标定义，请在配置中添加: jmicro.pic.upload.dirs." + target);
        }
        applyDefaults(dir);
        return dir;
    }

    /**
     * 目录映射来源：系统配置优先，未配置时回退到启动绑定值。
     */
    private Map<String, PicUploadProperties.Dir> resolveDirs() {
        if (systemConfigService != null) {
            Map<String, PicUploadProperties.Dir> configured = systemConfigService.getMap(
                PicUploadDirsConfigLoader.KEY, PicUploadProperties.Dir.class);
            if (!configured.isEmpty()) {
                return configured;
            }
        }
        return properties.getDirs();
    }

    private void applyDefaults(PicUploadProperties.Dir dir) {
        if (!StringUtils.hasText(dir.getProvider())) {
            dir.setProvider(properties.getProvider());
        }
        if (dir.getMaxFileSize() == null) {
            dir.setMaxFileSize(properties.getMaxFileSize());
        }
        if (dir.getAllowedExtensions() == null || dir.getAllowedExtensions().length == 0) {
            dir.setAllowedExtensions(properties.getAllowedExtensions());
        }
    }
}
