package com.jiangtj.micro.web.aop;

import com.jiangtj.micro.web.aop.anno.AnnoM;
import com.jiangtj.micro.web.aop.anno.AnnoT;
import com.jiangtj.micro.web.aop.anno.AnnoTM;

@AnnoT
@AnnoTM
public class AnnotatedClass {

    @AnnoM
    public void doSome1() {}

    @AnnoTM
    public void doSome2() {}

}
