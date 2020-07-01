package indi.vicliu.juaner.authorization.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Auther: liuweikai
 * @Date: 2019-07-19 22:10
 * @Description:
 */
@Data
public class AccessTokenResp extends BaseResp {

    /**
     * 授权用户唯一标识
     */
    private String openid;
    /**
     * 用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回
     */
    private String unionid;
    /**
     * 会话密钥
     */
    @JSONField(name = "session_key")
    private String sessionKey;
}
