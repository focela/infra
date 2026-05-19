package com.focela.platform.system.service.notify;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.notify.dto.message.NotifyMessageMyPageRequest;
import com.focela.platform.system.controller.admin.notify.dto.message.NotifyMessagePageRequest;
import com.focela.platform.system.domain.entity.notify.NotifyMessageEntity;
import com.focela.platform.system.domain.entity.notify.NotifyTemplateEntity;
import com.focela.platform.system.repository.mapper.notify.NotifyMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * In-site notification Service implementation class
 */
@Service
@Validated
@RequiredArgsConstructor
public class DefaultNotifyMessageService implements NotifyMessageService {

    private final NotifyMessageMapper notifyMessageMapper;

    @Override
    public Long createNotifyMessage(Long userId, Integer userType,
                                    NotifyTemplateEntity template, String templateContent, Map<String, Object> templateParams) {
        NotifyMessageEntity message = new NotifyMessageEntity().setUserId(userId).setUserType(userType)
                .setTemplateId(template.getId()).setTemplateCode(template.getCode())
                .setTemplateType(template.getType()).setTemplateNickname(template.getNickname())
                .setTemplateContent(templateContent).setTemplateParams(templateParams).setReadStatus(false);
        notifyMessageMapper.insert(message);
        return message.getId();
    }

    @Override
    public PageResult<NotifyMessageEntity> getNotifyMessagePage(NotifyMessagePageRequest pageRequest) {
        return notifyMessageMapper.selectPage(pageRequest);
    }

    @Override
    public PageResult<NotifyMessageEntity> getMyNotifyMessagePage(NotifyMessageMyPageRequest pageRequest, Long userId, Integer userType) {
        return notifyMessageMapper.selectPage(pageRequest, userId, userType);
    }

    @Override
    public NotifyMessageEntity getNotifyMessage(Long id) {
        return notifyMessageMapper.selectById(id);
    }

    @Override
    public List<NotifyMessageEntity> getUnreadNotifyMessageList(Long userId, Integer userType, Integer size) {
        return notifyMessageMapper.selectUnreadListByUserIdAndUserType(userId, userType, size);
    }

    @Override
    public Long getUnreadNotifyMessageCount(Long userId, Integer userType) {
        return notifyMessageMapper.selectUnreadCountByUserIdAndUserType(userId, userType);
    }

    @Override
    public int updateNotifyMessageRead(Collection<Long> ids, Long userId, Integer userType) {
        return notifyMessageMapper.updateListRead(ids, userId, userType);
    }

    @Override
    public int updateAllNotifyMessageRead(Long userId, Integer userType) {
        return notifyMessageMapper.updateListRead(userId, userType);
    }

}
