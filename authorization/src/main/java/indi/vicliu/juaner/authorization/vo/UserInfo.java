package indi.vicliu.juaner.authorization.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserInfo implements Serializable {
    /**
     * 用户编号
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 帐号是否未过期 1-是 0-否
     */
    private Boolean accountNonExpired;

    /**
     * 登录凭据是否未过期 1-是 0-否
     */
    private Boolean credentialsNonExpired;

    /**
     * 帐号是否未锁定 1-是 0-否
     */
    private Boolean accountNonLocked;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户是否启用 1-启用 0-不启用
     */
    private Boolean enabled;

    private static final long serialVersionUID = 1L;
}