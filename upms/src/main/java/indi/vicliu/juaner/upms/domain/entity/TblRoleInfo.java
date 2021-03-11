package indi.vicliu.juaner.upms.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Table(name = "tbl_role_info")
@Data
public class TblRoleInfo implements Serializable {
    /**
     * 角色编号
     */
    @Id
    private Long id;

    /**
     * 角色名
     */
    @Column(name = "role_name")
    private String roleName;

    /**
     * 角色描述
     */
    @lombok.Getter
    @Column(name = "role_desc")
    private String roleDesc;

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
     * 创建者
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * 修改者
     */
    @Column(name = "update_by")
    private Long updateBy;

    private static final long serialVersionUID = 1L;
}