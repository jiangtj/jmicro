package com.jiangtj.micro.business.config

interface JMicroConfigLoader {

    fun load(): List<SystemItemInfo>

}