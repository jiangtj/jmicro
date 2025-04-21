package com.jiangtj.micro.pic.upload.hw;

import com.jiangtj.micro.common.utils.FileNameUtils;
import com.jiangtj.micro.pic.upload.PicUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ObsPicUploadService {

    private final ObsService obsService;

    private final Map<Integer, ObsProperties.Dir> dirMap;

    public ObsPicUploadService(ObsService obsService, ObsProperties properties) {
        this.obsService = obsService;
        dirMap = properties.getDirs().stream().collect(Collectors.toMap(ObsProperties.Dir::getType, Function.identity()));
    }

    public String upload(MultipartFile file, int type) {

        ObsProperties.Dir dir = getDir(type);
        String directory = dir.getPath();
        String fileType = getFileType(file);

        // 判断是否是制定的文件类型
        List<String> fileStandType = List.of(dir.getFileType().split(","));
        if (!fileStandType.contains(fileType)) {
            throw new PicUploadException("不支持的文件类型！");
        }

        String result;
        try {
            result = obsService.putObject(directory + FileNameUtils.getRandomFileNameWithSuffix(fileType), file.getInputStream());
        } catch (IOException e) {
            log.error("上传文件失败，类型：{}", type, e);
            throw new PicUploadException("上传失败！");
        }
        return result;
    }

    private ObsProperties.Dir getDir(int type) {
        ObsProperties.Dir dir = dirMap.get(type);
        if (dir == null) {
            throw new PicUploadException("不支持的上传类型！");
        }
        return dir;
    }

    private static String getFileType(MultipartFile file) {
        String fileType;
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        if ("blob".equals(originalFilename)) {
            fileType = "." + Objects.requireNonNull(file.getContentType()).split("/")[1];// 兼容二进制流
        } else {
            fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return fileType;
    }
}
