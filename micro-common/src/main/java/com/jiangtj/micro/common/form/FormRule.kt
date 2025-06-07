package com.jiangtj.micro.common.form

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * [表单校验规则](https://github.com/yiminghe/async-validator)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class FormRule (
    var type: String? = null,
    var required: Boolean? = null,
    var pattern: String? = null,
    var min: Int? = null,
    var max: Int? = null,
    var len: Int? = null,
    @get:JsonProperty("enum")
    var enumAttrs: MutableList<String?>? = null,
    var whitespace: Boolean? = null,
    var fields: MutableMap<String?, MutableList<FormRule?>?>? = null,
    var message: String? = null,
    var trigger: String? = null
)
