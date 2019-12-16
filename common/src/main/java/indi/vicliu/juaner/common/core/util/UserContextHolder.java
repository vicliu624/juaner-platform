package indi.vicliu.juaner.common.core.util;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 14:05
 * @Description:
 */
public class UserContextHolder {
    private ThreadLocal<Map<String, Object>> threadLocal;

    private UserContextHolder() {
        this.threadLocal = new ThreadLocal<>();
    }

    /**
     * 创建实例
     *
     * @return
     */
    public static UserContextHolder getInstance() {
        return SingletonHolder.sInstance;
    }

    /**
     * 静态内部类单例模式
     * 单例初使化
     */
    private static class SingletonHolder {
        private static final UserContextHolder sInstance = new UserContextHolder();
    }

    /**
     * 用户上下文中放入信息
     *
     * @param map
     */
    public void setContext(Map<String, Object> map) {
        threadLocal.set(map);
    }

    /**
     * 获取上下文中的信息
     *
     * @return
     */
    public Map<String, Object> getContext() {
        return threadLocal.get();
    }

    /**
     * 获取上下文中的用户名
     *
     * @return
     */
    public String getUsername() {
        Object username = Optional.ofNullable(threadLocal.get()).orElse(Maps.newHashMap()).get("user_name");
        if (null == username) {
            return null;
        }
        return username.toString();
    }

    public String getClientId() {
        Object clientId = Optional.ofNullable(threadLocal.get()).orElse(Maps.newHashMap()).get("client_id");
        if (null == clientId) {
            return null;
        }
        return clientId.toString();
    }

    public List<String> getAuthorities() {
        Object authorities = Optional.ofNullable(threadLocal.get()).orElse(Maps.newHashMap()).get("authorities");
        if (authorities instanceof List) {
            return (List<String>) authorities;
        }
        return null;
    }

    /**
     * 清空上下文
     */
    public void clear() {
        threadLocal.remove();
    }
}
