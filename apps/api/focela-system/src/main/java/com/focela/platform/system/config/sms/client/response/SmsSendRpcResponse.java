package com.focela.platform.system.config.sms.client.response;

import lombok.Data;

/**
 * SMS send response
 */
@Data
public class SmsSendRpcResponse {

    /**
     * Whether succeeded
     */
    private Boolean success;

    /**
     * API request ID
     */
    private String apiRequestId;

    // ==================== Fields when succeeded ====================

    /**
     * Serial number returned by the SMS API
     */
    private String serialNo;

    // ==================== Fields when failed ====================

    /**
     * API returned error code
     *
     * Use String type because third-party error codes may be strings
     */
    private String apiCode;
    /**
     * API returned message
     */
    private String apiMsg;

}
