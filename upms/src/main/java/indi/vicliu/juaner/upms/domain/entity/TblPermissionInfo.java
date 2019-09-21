package indi.vicliu.juaner.upms.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Table(name = "tbl_permission_info")
@Data
public class TblPermissionInfo implements Serializable {
    /**
     * 权限编号
     */
    @Id
    private Long id;

    /**
     * 权限名称
     */
    @Column(name = "perm_name")
    private String permName;

    /**
     * 记录创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 记录修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

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
    @Column(name = "perm_url")
    private String permUrl;

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

    private static final long serialVersionUID = 1L;
}