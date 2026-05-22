package com.focela.platform.system.service.member;

import com.focela.platform.system.service.member.client.MemberUserClient;
import com.focela.platform.test.core.support.BaseMockitoUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class DefaultMemberServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DefaultMemberService memberService;

    @Mock
    private MemberUserClient memberUserClient;

    @Test
    public void getMemberUserMobile_nullId() {
        // invoke
        String result = memberService.getMemberUserMobile(null);
        // assert
        assertNull(result);
        verifyNoInteractions(memberUserClient);
    }

    @Test
    public void getMemberUserMobile_success() {
        // prepare parameters
        Long userId = 1L;
        String mobile = "15601691300";
        when(memberUserClient.getMobile(eq(userId))).thenReturn(mobile);

        // invoke
        String result = memberService.getMemberUserMobile(userId);
        // assert
        assertEquals(mobile, result);
        verify(memberUserClient).getMobile(eq(userId));
    }

    @Test
    public void getMemberUserEmail_nullId() {
        // invoke
        String result = memberService.getMemberUserEmail(null);
        // assert
        assertNull(result);
        verifyNoInteractions(memberUserClient);
    }

    @Test
    public void getMemberUserEmail_success() {
        // prepare parameters
        Long userId = 1L;
        String email = "member@example.com";
        when(memberUserClient.getEmail(eq(userId))).thenReturn(email);

        // invoke
        String result = memberService.getMemberUserEmail(userId);
        // assert
        assertEquals(email, result);
        verify(memberUserClient).getEmail(eq(userId));
    }

}
