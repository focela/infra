package com.focela.platform.system.service.member.client;

/**
 * Client for reading member user contact information from the optional member module.
 */
public interface MemberUserClient {

    /**
     * Get member user mobile.
     *
     * @param id member user ID
     * @return mobile number
     */
    String getMobile(Long id);

    /**
     * Get member user email.
     *
     * @param id member user ID
     * @return email
     */
    String getEmail(Long id);

}
