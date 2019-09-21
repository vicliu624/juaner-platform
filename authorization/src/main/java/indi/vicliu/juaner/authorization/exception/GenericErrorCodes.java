package indi.vicliu.juaner.authorization.exception;


/**
 * @Auther: liuweikai
 * @Date: 2019/3/15 21:01
 * @Description:
 */
public class GenericErrorCodes {
    public static ErrorCode GENERIC_API_ERROR_CODE = new ErrorCode(0001, "GENERIC_API_ERROR_CODE", "generic API error message");
    public static ErrorCode GENERIC_UNAUTHORIZED_ERROR_CODE = new ErrorCode(0002, "GENERIC_UNAUTHORIZED_ERROR_CODE", "generic unauthorized error message");
    public static ErrorCode DATA_ACCESS_ERROR_CODE = new ErrorCode(0003, "DATA_ACCESS_ERROR", "database access error");
}
