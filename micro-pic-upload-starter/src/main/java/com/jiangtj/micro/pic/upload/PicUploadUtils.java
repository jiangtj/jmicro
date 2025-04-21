package com.jiangtj.micro.pic.upload;

import com.jiangtj.micro.common.utils.FileNameUtils;
import org.springframework.web.multipart.MultipartFile;

public interface PicUploadUtils {

    static String generateNewFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = FileNameUtils.getFileExtension(originalFilename);
        return FileNameUtils.getRandomFileNameWithSuffix(extension);
    }
}
