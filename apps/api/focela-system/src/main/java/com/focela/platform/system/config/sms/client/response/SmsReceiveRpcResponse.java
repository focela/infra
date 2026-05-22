package com.focela.platform.system.config.sms.client.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * SMS receive response
 */
@Data
public class SmsReceiveRpcResponse {

    /**
     * Whether receive succeeded
     */
    private Boolean success;
    /**
     * API receive result code
     */
    private String errorCode;
    /**
     * API receive result description
     */
    private String errorMsg;

    /**
     * Mobile number
     */
    private String mobile;
    /**
     * User receive time
     */
    private LocalDateTime receiveTime;

    /**
     * Serial number returned by the SMS API send
     */
    private String serialNo;
    /**
     * SMS log ID
     *
     * Corresponds to the SMS log ID.
     */
    private Long logId;

}
