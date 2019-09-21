package indi.vicliu.juaner.authentication.vo;

import lombok.Data;

@Data
public class PermissionInfo {
    /**
     * 权限编号
     */
    private Long id;

    /**
     * 权限名称
     */
    private String permName;

    /**
     * 权限类型 0-action
     */
    private Integer permType;

    /**
     * http method
     */
    private String method;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 权限url
     */
    private String permUrl;

}
