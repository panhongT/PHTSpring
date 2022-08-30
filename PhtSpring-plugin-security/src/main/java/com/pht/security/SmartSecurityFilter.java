package com.pht.security;

import java.util.LinkedHashSet;
import java.util.Set;

import com.pht.security.realm.SmartCustomRealm;
import com.pht.security.realm.SmartJdbcRealm;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.ShiroFilter;

/**
 * 安全过滤器
 *
 * @author pht
 * @since 1.0.0
 */
public class SmartSecurityFilter extends ShiroFilter {

    @Override
    public void init() throws Exception {
        super.init();
        WebSecurityManager webSecurityManager = super.getSecurityManager();
        //设置realm，可同时支持多个realm，并按照先后顺序用逗号隔开。
        setRealms(webSecurityManager);
        //设置cache，用于减少数据库查询次数，降低I/O访问
        setCache(webSecurityManager);
    }

    private void setRealms(WebSecurityManager webSecurityManager) {
        //读取com.pht.security.realm配置项
        String securityRealms = SecurityConfig.getRealms();
        if (securityRealms != null) {
            //根据逗号进行拆分
            String[] securityRealmArray = securityRealms.split(",");
            if (securityRealmArray.length > 0) {
                //使realm具有唯一性和顺序性
                Set<Realm> realms = new LinkedHashSet<Realm>();
                for (String securityRealm : securityRealmArray) {
                    if (securityRealm.equalsIgnoreCase(SecurityConstant.REALMS_JDBC)) {
                        //添加基于JDBC的realm，需配置相关SQL查询语句
                        addJdbcRealm(realms);
                    } else if (securityRealm.equalsIgnoreCase(SecurityConstant.REALMS_CUSTOM)) {
                        //添加基于定制化的realm，需实现SmartSecurity接口
                        addCustomRealm(realms);
                    }
                }
                RealmSecurityManager realmSecurityManager = (RealmSecurityManager) webSecurityManager;
                realmSecurityManager.setRealms(realms);//设置realm
            }
        }
    }

    private void addJdbcRealm(Set<Realm> realms) {
        //添加自己实现的基于JDBC的realm
        SmartJdbcRealm smartJdbcRealm = new SmartJdbcRealm();
        realms.add(smartJdbcRealm);
    }

    private void addCustomRealm(Set<Realm> realms) {
        SmartSecurity smartSecurity = SecurityConfig.getSmartSecurity();
        SmartCustomRealm smartCustomRealm = new SmartCustomRealm(smartSecurity);
        realms.add(smartCustomRealm);
    }

    private void setCache(WebSecurityManager webSecurityManager) {
        if (SecurityConfig.isCache()) {
            CachingSecurityManager cachingSecurityManager = (CachingSecurityManager) webSecurityManager;
            CacheManager cacheManager = new MemoryConstrainedCacheManager();
            cachingSecurityManager.setCacheManager(cacheManager);
        }
    }
}
