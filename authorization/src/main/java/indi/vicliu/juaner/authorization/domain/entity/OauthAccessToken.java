package indi.vicliu.juaner.authorization.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Table(name = "oauth_access_token")
@Data
public class OauthAccessToken implements Serializable {
    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "authentication_id")
    private String authenticationId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "refresh_token")
    private String refreshToken;

    private byte[] token;

    private byte[] authentication;

    private static final long serialVersionUID = 1L;
}