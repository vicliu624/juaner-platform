package indi.vicliu.juaner.upms.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Table(name = "tbl_user_info")
@Data
public class TblUserInfo implements Serializable {
    /**
     * 用户编号
     */
    @Id
    private Long id;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 帐号是否未过期 1-是 0-否
     */
    @Column(name = "account_non_expired")
    private Boolean accountNonExpired;

    /**
     * 登录凭据是否未过期 1-是 0-否
     */
    @Column(name = "credentials_non_expired")
    private Boolean credentialsNonExpired;

    /**
     * 帐号是否未锁定 1-是 0-否
     */
    @Column(name = "account_non_locked")
    private Boolean accountNonLocked;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 创建人
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * 修改人
     */
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * 用户是否启用 1-启用 0-不启用
     */
    private Boolean enabled;

    private static final long serialVersionUID = 1L;
}