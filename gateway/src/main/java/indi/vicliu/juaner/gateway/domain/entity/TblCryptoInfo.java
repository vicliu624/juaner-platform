package indi.vicliu.juaner.gateway.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Table(name = "tbl_crypto_info")
@Data
public class TblCryptoInfo implements Serializable {
    @Id
    private Long requester;

    @Id
    @Column(name = "crypto_algorithm")
    private String cryptoAlgorithm;

    @Column(name = "private_key")
    private String privateKey;

    @Column(name = "public_key")
    private String publicKey;

    private static final long serialVersionUID = 1L;
}