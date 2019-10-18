package indi.vicliu.juaner.authorization.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Table(name = "oauth_refresh_token")
@Data
public class OauthRefreshToken implements Serializable {
    @Column(name = "token_id")
    private String tokenId;

    private byte[] token;

    private byte[] authentication;

    private static final long serialVersionUID = 1L;
}