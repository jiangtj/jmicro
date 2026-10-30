package com.jiangtj.micro.demobackend.config

import com.jiangtj.micro.business.config.JMicroConfigService
import com.jiangtj.micro.business.config.MicroConfigException
import com.jiangtj.micro.business.config.PUBLIC_TAG
import com.jiangtj.micro.demobackend.jooq.tables.daos.SysConfigDao
import com.jiangtj.micro.test.JMicroTest
import com.jiangtj.micro.test.WithMockUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * 系统配置 Service 集成测试（真实 Spring 上下文 + H2，不经过 Controller）。
 *
 * 配置项定义见 [DemoConfigLoader]，覆盖值持久化在 `sys_config` 表（见 [DemoConfigSaver]）。
 * 管理接口仅 admin 可用，用 [WithMockUser] 表达调用者身份；
 * 写库操作由 Spring Test 的 [Transactional] 在用例结束后自动回滚。
 */
@JMicroTest
@Transactional
@WithMockUser(roles = ["admin"])
class JMicroConfigServiceTest {

    @Autowired
    lateinit var configService: JMicroConfigService

    @Autowired
    lateinit var sysConfigDao: SysConfigDao

    /**
     * 事务回滚只回滚数据库，[JMicroConfigService] 的配置缓存需要显式清理，避免影响其它用例。
     */
    @AfterEach
    fun clearConfigCache() {
        configService.refreshConfig()
    }

    @Test
    fun `default value is served when nothing is persisted`() {
        val item = configService.getAllConfig().first { it.key == "demo.site.name" }
        assertThat(item.value).isEqualTo("JMicro Demo")
        assertThat(item.isModified).isFalse()
        assertThat(configService.getValue("demo.site.name")).isEqualTo("JMicro Demo")
    }

    @Test
    fun `update persists and delete restores default`() {
        configService.updateConfig("demo.site.name", "我的站点")

        assertThat(configService.getValue("demo.site.name")).isEqualTo("我的站点")
        assertThat(sysConfigDao.fetchOneByConfigKey("demo.site.name")?.configValue).isEqualTo("我的站点")

        val item = configService.getAllConfig().first { it.key == "demo.site.name" }
        assertThat(item.value).isEqualTo("我的站点")
        assertThat(item.isModified).isTrue()
        assertThat(configService.generateConfig("demo.site.name", item.value))
            .isEqualTo("jmicro.config.kv.demo.site.name=我的站点")

        // 删除覆盖值 -> 回退到默认值
        configService.deleteConfig("demo.site.name")
        assertThat(configService.getValue("demo.site.name")).isEqualTo("JMicro Demo")
        assertThat(sysConfigDao.fetchOneByConfigKey("demo.site.name")).isNull()
        val reset = configService.getAllConfig().first { it.key == "demo.site.name" }
        assertThat(reset.value).isEqualTo("JMicro Demo")
        assertThat(reset.isModified).isFalse()
    }

    @Test
    fun `invalid value is rejected`() {
        val ex = assertThrows<MicroConfigException> {
            configService.updateConfig("demo.site.maintenance", "not-a-boolean")
        }
        assertThat(ex.status).isEqualTo(500)
        assertThat(ex.title).isEqualTo("配置错误")
        assertThat(ex.detail).isEqualTo("配置项值不合法: 仅支持 true / false")
        assertThat(configService.getValue("demo.site.maintenance")).isEqualTo("false")
    }

    @Test
    fun `unknown key is rejected`() {
        val ex = assertThrows<MicroConfigException> {
            configService.updateConfig("demo.site.unknown", "any")
        }
        assertThat(ex.status).isEqualTo(500)
        assertThat(ex.detail).isEqualTo("配置项不存在")
    }

    @Test
    fun `public configs are exposed by tag`() {
        assertThat(configService.getConfigByTag(PUBLIC_TAG).map { it.key }).containsExactly(
            "demo.site.name",
            "demo.site.description",
            "demo.site.social-links",
        )
    }

    @Test
    fun `json config is readable as map`() {
        val links = configService.getMap("demo.site.social-links", String::class.java)
        assertThat(links).containsEntry("github", "https://github.com/jiangtj/jmicro")
    }
}
