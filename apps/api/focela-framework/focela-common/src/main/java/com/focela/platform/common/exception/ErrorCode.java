package com.focela.platform.common.exception;

import com.focela.platform.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.common.exception.enums.ServiceErrorCodeRange;
import lombok.Data;

/**
 * Error code object.
 *
 * Global error codes occupy [0, 999], see {@link GlobalErrorCodeConstants}.
 * Business exception error codes occupy [1 000 000 000, +inf), see {@link ServiceErrorCodeRange}.
 *
 * TODO Error codes are modeled as objects to leave room for future i18n support.
 */
@Data
public class ErrorCode {

    /**
     * Error code
     */
    private final Integer code;
    /**
     * Error message
     */
    private final String msg;

    public ErrorCode(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

}
