package indi.vicliu.juaner.common.core.exception;


/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:35
 * @Description:
 */
public enum ErrorType implements Error {

    NO_ERROR("000000", "处理成功"),
    SYSTEM_ERROR("-1", "系统异常"),
    OAUTH_ERROR("-2", "认证异常"),
    AUTH_ERROR("-3", "鉴权异常"),
    TOO_MANY_REQUEST_ERROR("-4", "请求过于频繁"),
    DECRYPT_ERROR("-5","密文包体解密出错"),
    NOT_FOUND("404", "访问的资源不存在");

    /**
     * 错误类型码
     */
    private String code;
    /**
     * 错误类型描述信息
     */
    private String message;

    ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
