package indi.vicliu.juaner.authorization.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Table(name = "tbl_weixin_mini_program_client_config")
@Data
public class TblWeixinMiniProgramClientConfig implements Serializable {
    /**
     * 托管渠道编号
     */
    @Id
    @Column(name = "channel_id")
    private String channelId;

    /**
     * 托管的微信平台appId
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 托管的微信平台appSecret
     */
    @Column(name = "app_secret")
    private String appSecret;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 创建者
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 更新者
     */
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * 1-启用 2-不启用
     */
    @Column(name = "is_valid")
    private Integer isValid;

    private static final long serialVersionUID = 1L;
}