package indi.vicliu.juaner.upms.domain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "tbl_user_id_map")
@Data
public class TblUserIdMap implements Serializable {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "wechat_open_id")
    private String wechatOpenId;

    @Column(name = "wechat_union_id")
    private String wechatUnionId;

    private static final long serialVersionUID = 1L;
}