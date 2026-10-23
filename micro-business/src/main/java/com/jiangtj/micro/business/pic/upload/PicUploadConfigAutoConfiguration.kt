package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemConfigAutoConfiguration
import com.jiangtj.micro.business.config.SystemConfigService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * 图片上传配置项（通用配置 + 目录映射）的系统配置注册，仅在开启 `jmicro.system.config.enabled=true` 后生效。
 */
@AutoConfiguration(after = [SystemConfigAutoConfiguration::class, PicUploadAutoConfiguration::class])
@ConditionalOnBean(SystemConfigService::class)
class PicUploadConfigAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun picUploadConfigLoader(properties: PicUploadProperties): PicUploadConfigLoader =
        PicUploadConfigLoader(properties)
}
