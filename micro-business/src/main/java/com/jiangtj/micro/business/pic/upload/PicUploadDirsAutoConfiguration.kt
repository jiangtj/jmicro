package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemConfigAutoConfiguration
import com.jiangtj.micro.business.config.SystemConfigService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * 上传目录映射的系统配置项注册，仅在开启 `jmicro.system.config.enabled=true` 后生效。
 */
@AutoConfiguration(after = [SystemConfigAutoConfiguration::class, PicUploadAutoConfiguration::class])
@ConditionalOnBean(SystemConfigService::class)
class PicUploadDirsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun picUploadDirsConfigLoader(properties: PicUploadProperties): PicUploadDirsConfigLoader =
        PicUploadDirsConfigLoader(properties)
}
