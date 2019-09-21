package indi.vicliu.juaner.upms.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Table(name = "tbl_user_role_map")
@Data
public class TblUserRoleMap implements Serializable {
    /**
     * 用户编号
     */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 角色编号
     */
    @Id
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 创建者
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新者
     */
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}