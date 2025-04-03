package com.jiangtj.micro.auth.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
public class WeChatLoginService {

    private final WxMaService wxMaService;

    public WeChatLoginService(WxMaService wxMaService) {
        this.wxMaService = wxMaService;
    }

    @NonNull
    public String login(@NonNull String code) throws WxErrorException {
        WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
        log.info(session.getSessionKey());
        String openid = session.getOpenid();
        log.info(openid);
        return Jwts.builder()
            .subject(openid)
            .expiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
            .signWith(KeyHolder.getKey())
            .compact();
    }

}
