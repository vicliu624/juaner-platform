package indi.vicliu.juaner.authorization.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Auther: liuweikai
 * @Date: 2019-07-19 22:09
 * @Description:
 */
@Data
public class BaseResp {

    /**
     * 正确返回时为0。给默认值是方便判断，因为很多接口正确返回时没有这两个字段，
     * 但是错误返回一定会有。
     */
    @JSONField(name = "errcode")
    private Integer errorCode = 0;
    @JSONField(name = "errmsg")
    private String errorMsg;
}
