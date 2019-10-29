package indi.vicliu.juaner.upms.dto;

import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import lombok.Data;

@Data
public class UserInfoDTO extends TblUserInfo {
    private String accountId;

    private String centerOpenId;
}
