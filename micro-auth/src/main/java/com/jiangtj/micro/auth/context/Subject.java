package com.jiangtj.micro.auth.context;

import lombok.Data;

@Data
public class Subject {
    String id;
    String name;
    String displayName;
    String avatar;
    String issuer;
    String type;
}
