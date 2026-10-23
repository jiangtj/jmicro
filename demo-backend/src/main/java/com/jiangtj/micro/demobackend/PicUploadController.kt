package com.jiangtj.micro.demobackend

import com.jiangtj.micro.business.pic.upload.PicUploadResult
import com.jiangtj.micro.business.pic.upload.PicUploadService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("pic")
class PicUploadController(
    private val picUploadService: PicUploadService,
) {

    @PostMapping("upload")
    fun upload(target: String, file: MultipartFile): PicUploadResult = picUploadService.upload(target, file)
}
