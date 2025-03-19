package com.jiangtj.micro.auth.casdoor;

import com.jiangtj.micro.auth.context.DefaultAuthContext;
import com.jiangtj.micro.auth.context.Subject;
import lombok.Getter;
import org.casbin.casdoor.entity.User;

@Getter
public class CasdoorUserContextImpl extends DefaultAuthContext {

    private final User casdoorUser;

    public CasdoorUserContextImpl(String issuer, User user) {
        super(new Subject());
        this.casdoorUser = user;
        Subject subject = this.subject();
        subject.setId(user.id);
        subject.setName(user.name);
        subject.setDisplayName(user.displayName);
        subject.setAvatar(user.avatar);
        subject.setIssuer(issuer);
        subject.setType("casdoor");
    }
}
