package indi.vicliu.juaner.common.core.exception;

/**
 * @Auther: liuweikai
 * @Date: 2019-10-02 15:03
 * @Description:
 */
public interface Error {
    String getCode();

    void setCode(String code);

    String getMessage();

    void setMessage(String message);
}
