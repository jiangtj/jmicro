package com.jiangtj.micro.auth.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(WeChatMaProperties.class)
public class WeChatAuthContextAutoConfiguration {

    @Bean
    public WxMaService wxMaService(WeChatMaProperties properties) {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getAppid());
        config.setSecret(properties.getSecret());
        config.setToken(properties.getToken());
        config.setAesKey(properties.getAesKey());
        config.setMsgDataFormat(properties.getMsgDataFormat());
        WxMaService maService = new WxMaServiceImpl();
        maService.setWxMaConfig(config);
        return maService;
    }

    @Bean
    public WeChatLoginService weChatLoginService(WxMaService wxMaService) {
        return new WeChatLoginService(wxMaService);
    }

    @Bean
    @ConditionalOnMissingBean
    public WeChatAuthContextConverter weChatAuthContextConverter() {
        return new WeChatAuthContextConverter();
    }

}
