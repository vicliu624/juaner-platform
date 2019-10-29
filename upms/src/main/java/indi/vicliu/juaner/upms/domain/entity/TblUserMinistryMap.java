package indi.vicliu.juaner.upms.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Table(name = "tbl_user_ministry_map")
@Data
public class TblUserMinistryMap implements Serializable {
    @Id
    private Integer id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "open_id")
    private String openId;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}