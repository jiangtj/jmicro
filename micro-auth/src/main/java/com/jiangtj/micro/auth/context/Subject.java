package com.jiangtj.micro.auth.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Subject {
    String id;
    String name;
    String displayName;
    String avatar;
    String type;
    String issuer;
}
