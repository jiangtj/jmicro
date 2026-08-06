package com.jiangtj.micro.business.config

interface SystemConfigLoader {

    fun load(): List<SystemItem>

}