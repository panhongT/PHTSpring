package com.pht.security.password;

import com.pht.framework.util.CodecUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;


/**
 * MD5 密码匹配器
 *
 * @author pht
 * @since 1.0.0
 */
public class Md5CredentialsMatcher implements CredentialsMatcher {

    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String submitted = String.valueOf(((UsernamePasswordToken) token).getPassword());
        String encrypted = String.valueOf(info.getCredentials());
        return CodecUtil.md5(submitted).equals(encrypted);
    }
}
