package com.jiangtj.micro.sql.jooq;

import org.springframework.data.domain.Pageable;

/**
 * 结果步骤处理器接口。
 */
public interface ResultStepHandler<R, C, P> {

    /**
     * 处理查询结果和计数结果，构建分页结果。
     *
     * @param result   查询结果
     * @param count    计数结果
     * @param pageable 分页参数
     * @return 处理后的结果
     */
    P handle(R result, C count, Pageable pageable);

}
