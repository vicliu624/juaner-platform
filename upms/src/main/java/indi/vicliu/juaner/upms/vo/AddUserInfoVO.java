package indi.vicliu.juaner.upms.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddUserInfoVO {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不可为空")
    private String userName;

    /**
     * 密码
     */
    @NotBlank(message = "用户密码不可以为空")
    private String password;

    /**
     * 昵称
     */
    private String nickName;


    /**
     * 帐号是否未过期 1-是 0-否
     */
    @NotNull(message = "帐号是否未过期不可以为空")
    private Boolean accountNonExpired;

    /**
     * 登录凭据是否未过期 1-是 0-否
     */
    @NotNull(message = "登录凭据是否未过期不可以为空")
    private Boolean credentialsNonExpired;

    /**
     * 帐号是否未锁定 1-是 0-否
     */
    @NotNull(message = "帐号是否未锁定不可以为空")
    private Boolean accountNonLocked;

    /**
     * 手机号
     */
    @NotBlank(message = "用户手机号码不可以为空")
    private String phone;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 修改人
     */
    private Long updateBy;

    /**
     * 用户是否启用 1-启用 0-不启用
     */
    @NotNull(message = "用户是否启用不可以为空")
    private Boolean enabled;

    @NotNull(message = "用户角色不可以为空")
    private Long roleId;

}
