package com.jiangtj.micro.auth.context;

import org.jspecify.annotations.Nullable;

/**
 * 授权上下文转换器接口
 */
public interface AuthContextConverter {

    /**
     * 将AuthRequest转换为AuthContext对象。
     *
     * @param request 要转换的AuthRequest对象
     * @return 转换后的AuthContext对象，如果转换失败则返回null
     */
    @Nullable
    AuthContext convert(AuthRequest request);

}
