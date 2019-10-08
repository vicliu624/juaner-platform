package indi.vicliu.juaner.authorization.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Table(name = "oauth_approvals")
@Data
public class OauthApprovals implements Serializable {
    private String userid;

    private String clientid;

    private String scope;

    private String status;

    private Date expiresat;

    private Date lastmodifiedat;

    private static final long serialVersionUID = 1L;
}