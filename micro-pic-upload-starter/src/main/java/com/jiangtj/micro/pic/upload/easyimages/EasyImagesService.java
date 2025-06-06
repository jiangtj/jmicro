package com.jiangtj.micro.pic.upload.easyimages;

import com.jiangtj.micro.pic.upload.PicUploadProperties;
import com.jiangtj.micro.pic.upload.PicUploadProvider;
import com.jiangtj.micro.pic.upload.PicUploadResult;
import com.jiangtj.micro.pic.upload.PicUploadType;
import com.jiangtj.micro.pic.upload.ex.PicUploadInternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@PicUploadType("easyimages")
@RequiredArgsConstructor
public class EasyImagesService implements PicUploadProvider {
    private final EasyImagesProperties config;
    private final RestClient webClient;

    public EasyImagesService(EasyImagesProperties config) {
        this.config = config;
        this.webClient = RestClient.builder()
                .baseUrl(config.getUrl())
                .build();
    }

    public EasyImagesResponse uploadImage(MultipartFile file) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("token", config.getToken());
        parts.add("image", file.getResource());

        try {
            EasyImagesResponse body = webClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(parts)
                .retrieve()
                .body(EasyImagesResponse.class);
            if (body != null && body.getCode() != 200) {
                throw new PicUploadInternalException("EasyImages 错误码" + body.getCode(), null);
            }
            return body;
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new PicUploadInternalException("EasyImages 异常", e);
        }
    }

    @Override
    public PicUploadResult upload(PicUploadProperties.Dir dir, MultipartFile file) {
        EasyImagesResponse response = uploadImage(file);
        String url = response.getUrl();
        String[] split = url.split("/");
        return PicUploadResult.builder()
            .fileName(split[split.length - 1])
            .fileUrl(url)
            .thumbnailUrl(response.getThumb())
            .build();
    }
}