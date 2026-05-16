package com.focela.platform.system.api.notify;

import com.focela.platform.system.api.notify.dto.NotifySendSingleToUserRpcRequest;

import jakarta.validation.Valid;

/**
 * Notification message send API interface
 */
public interface NotifyMessageSendApi {

    /**
     * Send a single notification message to an Admin user
     *
     * @param request send request
     * @return sent message ID
     */
    Long sendSingleMessageToAdmin(@Valid NotifySendSingleToUserRpcRequest request);

    /**
     * Send a single notification message to a Member user
     *
     * @param request send request
     * @return sent message ID
     */
    Long sendSingleMessageToMember(@Valid NotifySendSingleToUserRpcRequest request);

}
