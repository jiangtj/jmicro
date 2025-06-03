package com.jiangtj.micro.common.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * <a href="https://github.com/yiminghe/async-validator">表单校验规则</a>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormRule {
    private String type;
    private Boolean required;
    private String pattern;
    private Integer min;
    private Integer max;
    private Integer len;
    @JsonProperty("enum")
    private List<String> enumAttrs;
    private Boolean whitespace;
    private Map<String, List<FormRule>> fields;
    private String message;
    private String trigger;
}
