package com.jiangtj.micro.business.pic.upload

/**
 * 图片上传结果
 */
class PicUploadResult(
    /**
     * 原始文件名
     */
    var originalFileName: String? = null,
    /**
     * 新文件名
     */
    var fileName: String? = null,
    /**
     * 文件Url
     */
    var fileUrl: String? = null,
    /**
     * 缩略图Url（如果生成）
     */
    var thumbnailUrl: String? = null,
) {

    class Builder {
        private var originalFileName: String? = null
        private var fileName: String? = null
        private var fileUrl: String? = null
        private var thumbnailUrl: String? = null

        fun originalFileName(originalFileName: String?): Builder = apply { this.originalFileName = originalFileName }

        fun fileName(fileName: String?): Builder = apply { this.fileName = fileName }

        fun fileUrl(fileUrl: String?): Builder = apply { this.fileUrl = fileUrl }

        fun thumbnailUrl(thumbnailUrl: String?): Builder = apply { this.thumbnailUrl = thumbnailUrl }

        fun build(): PicUploadResult =
            PicUploadResult(originalFileName, fileName, fileUrl, thumbnailUrl)
    }

    override fun toString(): String =
        "PicUploadResult(originalFileName=$originalFileName, fileName=$fileName, " +
            "fileUrl=$fileUrl, thumbnailUrl=$thumbnailUrl)"

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
