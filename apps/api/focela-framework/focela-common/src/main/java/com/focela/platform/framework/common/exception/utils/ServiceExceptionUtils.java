package com.focela.platform.framework.common.exception.utils;

import com.focela.platform.framework.common.exception.ErrorCode;
import com.focela.platform.framework.common.exception.ServiceException;
import com.focela.platform.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for {@link ServiceException}.
 *
 * Its purpose is to format exception messages.
 * Since String.format throws when arguments are incorrect, we use {} as the placeholder
 * and format via {@link #doFormat(int, String, Object...)}.
 */
@Slf4j
public class ServiceExceptionUtils {

    // ========== Integration with ServiceException ==========

    public static ServiceException exception(ErrorCode errorCode) {
        return exception0(errorCode.getCode(), errorCode.getMsg());
    }

    public static ServiceException exception(ErrorCode errorCode, Object... params) {
        return exception0(errorCode.getCode(), errorCode.getMsg(), params);
    }

    public static ServiceException exception0(Integer code, String messagePattern, Object... params) {
        String message = doFormat(code, messagePattern, params);
        return new ServiceException(code, message);
    }

    public static ServiceException invalidParamException(String messagePattern, Object... params) {
        return exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), messagePattern, params);
    }

    // ========== Format method ==========

    /**
     * Format the message corresponding to the given error code using params.
     *
     * @param code           error code
     * @param messagePattern message template
     * @param params         parameters
     * @return formatted message
     */
    @VisibleForTesting
    public static String doFormat(int code, String messagePattern, Object... params) {
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        int i = 0;
        int j;
        int l;
        for (l = 0; l < params.length; l++) {
            j = messagePattern.indexOf("{}", i);
            if (j == -1) {
                log.error("[doFormat][too many params: error code ({})|error content ({})|params({})", code, messagePattern, params);
                if (i == 0) {
                    return messagePattern;
                } else {
                    sbuf.append(messagePattern.substring(i));
                    return sbuf.toString();
                }
            } else {
                sbuf.append(messagePattern, i, j);
                sbuf.append(params[l]);
                i = j + 2;
            }
        }
        if (messagePattern.indexOf("{}", i) != -1) {
            log.error("[doFormat][too few params: error code ({})|error content ({})|params({})", code, messagePattern, params);
        }
        sbuf.append(messagePattern.substring(i));
        return sbuf.toString();
    }

}
