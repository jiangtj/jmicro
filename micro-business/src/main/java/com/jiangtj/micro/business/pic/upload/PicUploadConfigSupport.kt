package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.SystemItemType

/**
 * 图片上传服务商的系统配置项描述
 */
interface PicUploadConfigItem {

    /**
     * 系统配置项键
     */
    val key: String

    /**
     * 配置项显示名称
     */
    val label: String

    /**
     * 默认值
     */
    val defaultValue: String

    /**
     * 是否为敏感配置（在配置列表中脱敏展示）
     */
    val secret: Boolean get() = false

    /**
     * 配置项表单类型
     */
    val type: SystemItemType get() = SystemItemType.TEXT
}

/**
 * 将配置项描述转换为系统配置项默认值
 */
fun List<PicUploadConfigItem>.toSystemConfigItems(group: SystemGroup, tag: String): List<SystemItemInfo> =
    mapIndexed { index, item ->
        SystemItemInfo(
            key = item.key,
            name = item.label,
            description = item.label,
            value = item.defaultValue,
            secret = item.secret,
            group = group,
            type = item.type.name,
            order = index,
            tag = listOf(tag),
        )
    }
