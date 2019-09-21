package indi.vicliu.juaner.authorization.exception;

/**
 * @Auther: liuweikai
 * @Date: 2019/3/15 21:02
 * @Description:
 */
public class ErrorCode {
    private final int code;
    private final String message;
    private final String detailMessage;

    public ErrorCode(int code, String message, String detailMessage) {
        this.code = code;
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public ErrorCode(String message, String detailMessage) {
        this.code = 0;
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}
