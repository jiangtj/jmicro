package com.jiangtj.micro.business.pic.upload

/**
 * 标识图片上传服务商类型，注解值即 `jmicro.pic.upload.dirs.*.provider` 的取值。
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PicUploadType(val value: String)
