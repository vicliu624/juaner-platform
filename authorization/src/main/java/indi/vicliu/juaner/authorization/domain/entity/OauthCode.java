package indi.vicliu.juaner.authorization.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Table(name = "oauth_code")
@Data
public class OauthCode implements Serializable {
    private String code;

    private byte[] authentication;

    private static final long serialVersionUID = 1L;
}