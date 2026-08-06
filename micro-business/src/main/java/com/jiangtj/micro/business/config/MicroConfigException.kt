package com.jiangtj.micro.business.config

import com.jiangtj.micro.common.exceptions.MicroProblemDetailException

class MicroConfigException(msg: String) : MicroProblemDetailException(500, "配置错误", msg)