package com.jiangtj.micro.sql.jooq;

import org.springframework.data.domain.Pageable;

public interface ResultStepHandler<R,C,P> {

    P handle(R result, C count, Pageable pageable);

}
