package com.focela.platform.system.api.mail;

import com.focela.platform.system.api.mail.dto.MailSendSingleToUserRpcRequest;

import jakarta.validation.Valid;

/**
 * Mail send API interface
 */
public interface MailSendApi {

    /**
     * Send a single email to an Admin user
     *
     * When mail is empty, use userId to load the email of the corresponding Admin
     *
     * @param request send request
     * @return send log ID
     */
    Long sendSingleMailToAdmin(@Valid MailSendSingleToUserRpcRequest request);

    /**
     * Send a single email to a Member user
     *
     * When mail is empty, use userId to load the email of the corresponding Member
     *
     * @param request send request
     * @return send log ID
     */
    Long sendSingleMailToMember(@Valid MailSendSingleToUserRpcRequest request);

}
