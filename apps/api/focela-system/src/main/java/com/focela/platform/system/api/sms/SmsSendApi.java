package com.focela.platform.system.api.sms;

import com.focela.platform.system.api.sms.dto.send.SmsSendSingleToUserRpcRequest;

import jakarta.validation.Valid;

/**
 * SMS send API interface
 */
public interface SmsSendApi {

    /**
     * Send a single SMS to an Admin user
     *
     * When mobile is empty, use userId to load the mobile number of the corresponding Admin
     *
     * @param reqDTO send request
     * @return send log ID
     */
    Long sendSingleSmsToAdmin(@Valid SmsSendSingleToUserRpcRequest reqDTO);

    /**
     * Send a single SMS to a Member user
     *
     * When mobile is empty, use userId to load the mobile number of the corresponding Member
     *
     * @param reqDTO send request
     * @return send log ID
     */
    Long sendSingleSmsToMember(@Valid SmsSendSingleToUserRpcRequest reqDTO);

}
