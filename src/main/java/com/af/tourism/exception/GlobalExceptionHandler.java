package com.af.tourism.exception;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

/**
 * 全局异常处理，统一返回错误码与响应结构。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            ValidationException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            BindException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleValidationException(Exception ex) {
        log.warn("参数校验失败", ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.PARAM_INVALID, extractValidationMessage(ex));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("请求体反序列化失败", ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.PARAM_INVALID, "请求体格式错误");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.warn("上传文件大小超限", ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.PARAM_INVALID, "文件大小超出限制");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFound(NoHandlerFoundException ex) {
        log.warn("请求路径不存在: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "请求路径不存在");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("请求方法不支持: {}", ex.getMethod(), ex);
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.PARAM_INVALID, "请求方法不支持");
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.info("业务异常: {}", ex.getMessage());
        return buildResponse(resolveHttpStatus(ex.getErrorCode()), ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex) {
        log.error("未捕获异常", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_ERROR,
                ErrorCode.INTERNAL_ERROR.getDefaultMessage());
    }

    private ResponseEntity<ApiResponse<Void>> buildResponse(HttpStatus httpStatus, ErrorCode errorCode, String message) {
        String responseMessage = message;
        if (responseMessage == null || responseMessage.trim().isEmpty()) {
            responseMessage = errorCode != null ? errorCode.getDefaultMessage() : ErrorCode.INTERNAL_ERROR.getDefaultMessage();
        }
        return ResponseEntity.status(httpStatus).body(ApiResponse.fail(errorCode, responseMessage));
    }

    private String extractValidationMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) ex;
            if (exception.getBindingResult().getFieldError() != null) {
                return exception.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        if (ex instanceof BindException) {
            BindException exception = (BindException) ex;
            if (exception.getBindingResult().getFieldError() != null) {
                String field = exception.getBindingResult().getFieldError().getField();
                if ("sort".equals(field)) {
                    return "排序类型不支持";
                }

                return exception.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException exception = (ConstraintViolationException) ex;
            if (!exception.getConstraintViolations().isEmpty()) {
                return exception.getConstraintViolations().iterator().next().getMessage();
            }
        }
        if (ex instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException exception = (MissingServletRequestParameterException) ex;
            return "缺少必要参数: " + exception.getParameterName();
        }
        if (ex instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException exception = (MethodArgumentTypeMismatchException) ex;
            return "参数类型错误: " + exception.getName();
        }
        return ex.getMessage() != null ? ex.getMessage() : ErrorCode.PARAM_INVALID.getDefaultMessage();
    }

    private HttpStatus resolveHttpStatus(ErrorCode errorCode) {
        if (errorCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        switch (errorCode) {
            case PARAM_INVALID:
                return HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED:
                return HttpStatus.UNAUTHORIZED;
            case FORBIDDEN:
            case USER_DISABLED:
                return HttpStatus.FORBIDDEN;
            case NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case CONFLICT:
                return HttpStatus.CONFLICT;
            case BUSINESS_ERROR:
                return HttpStatus.UNPROCESSABLE_ENTITY;
            case FILE_UPLOAD_ERROR:
            case INTERNAL_ERROR:
                return HttpStatus.INTERNAL_SERVER_ERROR;
            default:
                return HttpStatus.BAD_REQUEST;
        }
    }
}
