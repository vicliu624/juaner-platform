package indi.vicliu.juaner.authorization.domain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "tbl_weixin_app_config")
@Data
public class TblWeixinAppConfig implements Serializable {
    @Id
    private String id;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "app_secret")
    private String appSecret;

    private static final long serialVersionUID = 1L;
}