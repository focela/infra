package com.focela.platform.module.system.api.mail;

import com.focela.platform.module.system.api.mail.dto.MailSendSingleToUserRpcRequest;

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
     * @param reqDTO send request
     * @return send log ID
     */
    Long sendSingleMailToAdmin(@Valid MailSendSingleToUserRpcRequest reqDTO);

    /**
     * Send a single email to a Member user
     *
     * When mail is empty, use userId to load the email of the corresponding Member
     *
     * @param reqDTO send request
     * @return send log ID
     */
    Long sendSingleMailToMember(@Valid MailSendSingleToUserRpcRequest reqDTO);

}
