package com.focela.platform.system.service.member;

/**
 * Member Service interface
 */
public interface MemberService {

    /**
     * Get the mobile number of a member user
     *
     * @param id member user ID
     * @return mobile number
     */
    String getMemberUserMobile(Long id);

    /**
     * Get the email of a member user
     *
     * @param id member user ID
     * @return email
     */
    String getMemberUserEmail(Long id);

}
