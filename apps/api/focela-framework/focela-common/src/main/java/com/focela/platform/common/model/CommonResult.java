package com.focela.platform.common.model;

import cn.hutool.core.lang.Assert;
import com.focela.platform.common.exception.ErrorCode;
import com.focela.platform.common.exception.ServiceException;
import com.focela.platform.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.common.exception.utils.ServiceExceptionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * Common result wrapper.
 *
 * @param <T> data generic type
 */
@Data
public class CommonResult<T> implements Serializable {

    /**
     * Error code
     *
     * @see ErrorCode#getCode()
     */
    private Integer code;
    /**
     * Error message, human-readable.
     *
     * @see ErrorCode#getMsg() ()
     */
    private String msg;
    /**
     * Returned data
     */
    private T data;

    /**
     * Convert the given result object into another generic result object.
     *
     * Because the CommonResult returned by method A does not match what its caller B needs to return,
     * a conversion is required.
     *
     * @param result the source result object
     * @param <T> returned generic type
     * @return new CommonResult object
     */
    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMsg());
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        Assert.notEquals(GlobalErrorCodeConstants.SUCCESS.getCode(), code, "code must be an error!");
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.msg = message;
        return result;
    }

    public static <T> CommonResult<T> error(ErrorCode errorCode, Object... params) {
        Assert.notEquals(GlobalErrorCodeConstants.SUCCESS.getCode(), errorCode.getCode(), "code must be an error!");
        CommonResult<T> result = new CommonResult<>();
        result.code = errorCode.getCode();
        result.msg = ServiceExceptionUtils.doFormat(errorCode.getCode(), errorCode.getMsg(), params);
        return result;
    }

    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.data = data;
        result.msg = "";
        return result;
    }

    public static boolean isSuccess(Integer code) {
        return Objects.equals(code, GlobalErrorCodeConstants.SUCCESS.getCode());
    }

    @JsonIgnore // avoid jackson serialization
    public boolean isSuccess() {
        return isSuccess(code);
    }

    @JsonIgnore // avoid jackson serialization
    public boolean isError() {
        return !isSuccess();
    }

    // ========= Integration with Exception system =========

    /**
     * Check whether there is an error. If so, throw a {@link ServiceException}.
     */
    public void checkError() throws ServiceException {
        if (isSuccess()) {
            return;
        }
        // business exception
        throw new ServiceException(code, msg);
    }

    /**
     * Check whether there is an error. If so, throw a {@link ServiceException};
     * otherwise return {@link #data}.
     */
    @JsonIgnore // avoid jackson serialization
    public T getCheckedData() {
        checkError();
        return data;
    }

    public static <T> CommonResult<T> error(ServiceException serviceException) {
        return error(serviceException.getCode(), serviceException.getMessage());
    }

}
