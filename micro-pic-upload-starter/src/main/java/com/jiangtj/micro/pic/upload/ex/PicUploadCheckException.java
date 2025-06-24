package com.jiangtj.micro.pic.upload.ex;

import com.jiangtj.micro.common.exceptions.MicroProblemDetailException;

public class PicUploadCheckException extends MicroProblemDetailException {
    public PicUploadCheckException(String msg) {
        super(400, "参数校验错误", msg);
    }
}
