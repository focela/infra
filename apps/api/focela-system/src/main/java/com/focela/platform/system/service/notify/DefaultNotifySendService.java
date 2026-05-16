package com.focela.platform.system.service.notify;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.system.entity.notify.NotifyTemplateEntity;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Objects;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.ErrorCodeConstants.*;

/**
 * In-site notification send Service implementation class
 */
@Service
@Validated
@Slf4j
public class DefaultNotifySendService implements NotifySendService {

    @Resource
    private NotifyTemplateService notifyTemplateService;

    @Resource
    private NotifyMessageService notifyMessageService;

    @Override
    public Long sendSingleNotifyToAdmin(Long userId, String templateCode, Map<String, Object> templateParams) {
        return sendSingleNotify(userId, UserTypeEnum.ADMIN.getValue(), templateCode, templateParams);
    }

    @Override
    public Long sendSingleNotifyToMember(Long userId, String templateCode, Map<String, Object> templateParams) {
        return sendSingleNotify(userId, UserTypeEnum.MEMBER.getValue(), templateCode, templateParams);
    }

    @Override
    public Long sendSingleNotify(Long userId, Integer userType, String templateCode, Map<String, Object> templateParams) {
        // validate template
        NotifyTemplateEntity template = validateNotifyTemplate(templateCode);
        if (Objects.equals(template.getStatus(), CommonStatusEnum.DISABLE.getStatus())) {
            log.info("[sendSingleNotify][template ({})is closed, cannot to user ({}/{})send]", templateCode, userId, userType);
            return null;
        }
        // validate parameters
        validateTemplateParams(template, templateParams);

        // send in-site notification
        String content = notifyTemplateService.formatNotifyTemplateContent(template.getContent(), templateParams);
        return notifyMessageService.createNotifyMessage(userId, userType, template, content, templateParams);
    }

    @VisibleForTesting
    public NotifyTemplateEntity validateNotifyTemplate(String templateCode) {
        // get notification template; for efficiency, fetch from cache
        NotifyTemplateEntity template = notifyTemplateService.getNotifyTemplateByCodeFromCache(templateCode);
        // template does not exist
        if (template == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
        return template;
    }

    /**
     * Validate that the in-site notification template parameters are present
     *
     * @param template email template
     * @param templateParams parameter list
     */
    @VisibleForTesting
    public void validateTemplateParams(NotifyTemplateEntity template, Map<String, Object> templateParams) {
        template.getParams().forEach(key -> {
            Object value = templateParams.get(key);
            if (value == null) {
                throw exception(NOTIFY_SEND_TEMPLATE_PARAM_MISS, key);
            }
        });
    }
}
