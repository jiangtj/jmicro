package com.jiangtj.micro.pic.upload.ex;

import com.jiangtj.micro.common.exceptions.MicroProblemDetailException;
import org.jspecify.annotations.Nullable;

public class PicUploadInternalException extends MicroProblemDetailException {
    public PicUploadInternalException(String msg, @Nullable Throwable cause) {
        super(500, "图片上传错误", msg, cause);
    }
}
