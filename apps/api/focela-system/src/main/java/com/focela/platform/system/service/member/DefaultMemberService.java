package com.focela.platform.system.service.member;

import com.focela.platform.system.service.member.client.MemberUserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Member Service implementation class
 */
@Service
@RequiredArgsConstructor
public class DefaultMemberService implements MemberService {

    private final MemberUserClient memberUserClient;

    @Override
    public String getMemberUserMobile(Long id) {
        if (id == null) {
            return null;
        }
        return memberUserClient.getMobile(id);
    }

    @Override
    public String getMemberUserEmail(Long id) {
        if (id == null) {
            return null;
        }
        return memberUserClient.getEmail(id);
    }

}
