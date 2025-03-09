package com.jiangtj.micro.web.aop;

import com.jiangtj.micro.web.aop.anno.AnnoM;
import com.jiangtj.micro.web.aop.anno.AnnoTM;

public class AnnotatedMethod {

    @AnnoM
    public void doSome1() {}

    @AnnoTM
    public void doSome2() {}

}
