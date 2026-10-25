package __PKG__.controller

import com.jiangtj.micro.business.pic.upload.PicUploadResult
import com.jiangtj.micro.business.pic.upload.PicUploadService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * 图片上传。
 *
 * `target` 是目录映射 key（对应系统配置项 `jmicro.pic.upload.dirs`），
 * 未匹配时使用默认 provider 与默认限制。
 */
@RestController
@RequestMapping("pic")
class PicUploadController(
    private val picUploadService: PicUploadService,
) {

    @PostMapping("upload")
    fun upload(target: String, file: MultipartFile): PicUploadResult =
        picUploadService.upload(target, file)
}
