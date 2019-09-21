package indi.vicliu.juaner.common.core.exception;


/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:35
 * @Description:
 */
public enum ErrorType {

    NO_ERROR("000000", "处理成功"),
    SYSTEM_ERROR("-1", "系统异常"),
    OAUTH_ERROR("-2", "认证异常"),
    AUTH_ERROR("-3", "鉴权异常"),
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
