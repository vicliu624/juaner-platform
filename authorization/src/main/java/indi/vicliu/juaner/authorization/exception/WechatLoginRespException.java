package indi.vicliu.juaner.authorization.exception;

/**
 * @Auther: liuweikai
 * @Date: 2019-10-04 21:33
 * @Description:
 */
public class WechatLoginRespException extends RuntimeException {
    private static final long serialVersionUID = -5035018726425963469L;

    public WechatLoginRespException(String reasonPhrase) {
        super(reasonPhrase);
    }
}
