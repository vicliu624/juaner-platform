package indi.vicliu.juaner.authorization.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
public class RoleInfo implements Serializable {
    /**
     * 角色编号
     */
    private Long id;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String roleDesc;

    private static final long serialVersionUID = 1L;
}