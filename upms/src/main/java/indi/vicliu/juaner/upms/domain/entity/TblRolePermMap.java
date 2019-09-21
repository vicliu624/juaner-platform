package indi.vicliu.juaner.upms.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Table(name = "tbl_role_perm_map")
@Data
public class TblRolePermMap implements Serializable {
    /**
     * 角色编号
     */
    @Id
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 权限编号
     */
    @Id
    @Column(name = "perm_id")
    private Long permId;

    /**
     * 创建人
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 创建者
     */
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * 创建时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}