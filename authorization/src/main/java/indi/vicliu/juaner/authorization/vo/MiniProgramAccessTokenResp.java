package indi.vicliu.juaner.authorization.vo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Auther: liuweikai
 * @Date: 2019-10-04 20:07
 * @Description:
 */
public class MiniProgramAccessTokenResp extends BaseResp {

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


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
}