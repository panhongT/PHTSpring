package com.pht.prepare5;

import java.util.Set;
import com.pht.framework.helper.DatabaseHelper;
import com.pht.security.SmartSecurity;

/**
 * 应用安全控制
 *
 * @author pht
 * @since 1.0.0
 */
public class AppSecurity implements SmartSecurity {

    public String getPassword(String username) {
        String sql = "SELECT password FROM user WHERE username = ?";
        return DatabaseHelper.query(sql, username);
    }

    public Set<String> getRoleNameSet(String username) {
        String sql = "SELECT r.role_name FROM user u, user_role ur, role r WHERE u.id = ur.user_id AND r.id = ur.role_id AND u.username = ?";
        return DatabaseHelper.querySet(sql, username);
    }

    public Set<String> getPermissionNameSet(String roleName) {
        String sql = "SELECT p.permission_name FROM role r, role_permission rp, permission p WHERE r.id = rp.role_id AND p.id = rp.permission_id AND r.role_name = ?";
        return DatabaseHelper.querySet(sql, roleName);
    }
}
