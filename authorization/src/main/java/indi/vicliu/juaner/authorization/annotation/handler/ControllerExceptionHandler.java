package indi.vicliu.juaner.authorization.annotation.handler;

import indi.vicliu.juaner.common.core.exception.ErrorType;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2020-04-21 16:07
 * @Description:
 */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        logger.info("messageNotReadable excetpion:", ex);
        return new ResponseEntity<>(Result.fail(ErrorType.SYSTEM_ERROR, "数据类型转换错误"), HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

//        //Get all errors
//        List<String> errors = ex.getBindingResult()
//                                .getFieldErrors()
//                                .stream()
//                                .map(x -> x.getDefaultMessage())
//                                .collect(Collectors.toList());
        logger.info("argumentNotValid exception:", ex);
        List<FieldError> errorList = ex.getBindingResult().getFieldErrors();
        String errors = errorList.isEmpty() ? "" : errorList.get(0).getDefaultMessage();

        return new ResponseEntity<>(Result.fail(ErrorType.SYSTEM_ERROR, errors), headers, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex) {
        logger.warn("uncatched exception", ex);
        return new ResponseEntity<>(Result.fail(ex.getMessage()), HttpStatus.OK);
    }
}
