package com.focela.platform.system.service.notify;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.notify.dto.message.NotifyMessageMyPageRequest;
import com.focela.platform.system.controller.admin.notify.dto.message.NotifyMessagePageRequest;
import com.focela.platform.system.entity.notify.NotifyMessageEntity;
import com.focela.platform.system.entity.notify.NotifyTemplateEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * In-site notification Service interface
 */
public interface NotifyMessageService {

    /**
     * Create an in-site notification
     *
     * @param userId user ID
     * @param userType user type
     * @param template template info
     * @param templateContent template content
     * @param templateParams template parameters
     * @return notification ID
     */
    Long createNotifyMessage(Long userId, Integer userType,
                             NotifyTemplateEntity template, String templateContent, Map<String, Object> templateParams);

    /**
     * Get in-site notification page
     *
     * @param pageRequest page query
     * @return notification page
     */
    PageResult<NotifyMessageEntity> getNotifyMessagePage(NotifyMessagePageRequest pageRequest);

    /**
     * Get [My] in-site notification page
     *
     * @param pageRequest page query
     * @param userId user ID
     * @param userType user type
     * @return notification page
     */
    PageResult<NotifyMessageEntity> getMyMyNotifyMessagePage(NotifyMessageMyPageRequest pageRequest, Long userId, Integer userType);

    /**
     * Get in-site notification
     *
     * @param id ID
     * @return notification
     */
    NotifyMessageEntity getNotifyMessage(Long id);

    /**
     * Get [My] unread in-site notification list
     *
     * @param userId   user ID
     * @param userType user type
     * @param size     count
     * @return notification list
     */
    List<NotifyMessageEntity> getUnreadNotifyMessageList(Long userId, Integer userType, Integer size);

    /**
     * Count user's unread in-site notifications
     *
     * @param userId   user ID
     * @param userType user type
     * @return unread notification count
     */
    Long getUnreadNotifyMessageCount(Long userId, Integer userType);

    /**
     * Mark in-site notifications as read
     *
     * @param ids    notification ID collection
     * @param userId user ID
     * @param userType user type
     * @return number of rows updated
     */
    int updateNotifyMessageRead(Collection<Long> ids, Long userId, Integer userType);

    /**
     * Mark all in-site notifications as read
     *
     * @param userId   user ID
     * @param userType user type
     * @return number of rows updated
     */
    int updateAllNotifyMessageRead(Long userId, Integer userType);

}
