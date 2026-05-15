package com.focela.platform.system.api.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Mail send Request DTO
 */
@Data
public class MailSendSingleToUserRpcRequest {

    /**
     * User ID
     *
     * If not empty, load the corresponding user's email and add it to {@link #toMails}
     */
    private Long userId;

    /**
     * Recipient emails
     */
    private List<@Email String> toMails;
    /**
     * CC emails
     */
    private List<@Email String> ccMails;
    /**
     * BCC emails
     */
    private List<@Email String> bccMails;


    /**
     * Mail template ID
     */
    @NotNull(message = "email template ID must not be blank")
    private String templateCode;
    /**
     * Mail template parameters
     */
    private Map<String, Object> templateParams;

    /**
     * Attachments
     */
    private File[] attachments;

}
