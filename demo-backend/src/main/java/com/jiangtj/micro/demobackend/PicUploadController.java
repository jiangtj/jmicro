package com.jiangtj.micro.demobackend;

import com.jiangtj.micro.pic.upload.PicUploadResult;
import com.jiangtj.micro.pic.upload.PicUploadService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("pic")
public class PicUploadController {

    @Resource
    private PicUploadService picUploadService;

    @PostMapping("upload")
    public PicUploadResult upload(MultipartFile file) throws IOException {
        PicUploadResult result = picUploadService.upload(file);
        return result;
    }

}
