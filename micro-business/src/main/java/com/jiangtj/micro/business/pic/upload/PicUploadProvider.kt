package com.jiangtj.micro.business.pic.upload;


import org.springframework.web.multipart.MultipartFile;

public interface PicUploadProvider {

    PicUploadResult upload(PicUploadProperties.Dir dir, MultipartFile file);

}
