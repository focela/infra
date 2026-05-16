package com.focela.platform.system.service.notify;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.notify.dto.template.NotifyTemplatePageRequest;
import com.focela.platform.system.controller.admin.notify.dto.template.NotifyTemplateSaveRequest;
import com.focela.platform.system.entity.notify.NotifyTemplateEntity;
import com.focela.platform.system.repository.mapper.notify.NotifyTemplateMapper;
import com.focela.platform.system.constants.RedisKeyConstants;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.ErrorCodeConstants.NOTIFY_TEMPLATE_CODE_DUPLICATE;
import static com.focela.platform.system.constants.ErrorCodeConstants.NOTIFY_TEMPLATE_NOT_EXISTS;

/**
 * In-site notification template Service implementation class
 */
@Service
@Validated
@Slf4j
public class DefaultNotifyTemplateService implements NotifyTemplateService {

    /**
     * Regular expression matching variables inside {}
     */
    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    @Resource
    private NotifyTemplateMapper notifyTemplateMapper;

    @Override
    public Long createNotifyTemplate(NotifyTemplateSaveRequest createRequest) {
        // validate template code is not duplicated
        validateNotifyTemplateCodeDuplicate(null, createRequest.getCode());

        // insert
        NotifyTemplateEntity notifyTemplate = BeanUtils.toBean(createRequest, NotifyTemplateEntity.class);
        notifyTemplate.setParams(parseTemplateContentParams(notifyTemplate.getContent()));
        notifyTemplateMapper.insert(notifyTemplate);
        return notifyTemplate.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE,
            allEntries = true) // allEntries clears all caches because the code field may have changed, which is hard to clear
    public void updateNotifyTemplate(NotifyTemplateSaveRequest updateRequest) {
        // validate existence
        validateNotifyTemplateExists(updateRequest.getId());
        // validate template code is not duplicated
        validateNotifyTemplateCodeDuplicate(updateRequest.getId(), updateRequest.getCode());

        // update
        NotifyTemplateEntity updateObj = BeanUtils.toBean(updateRequest, NotifyTemplateEntity.class);
        updateObj.setParams(parseTemplateContentParams(updateObj.getContent()));
        notifyTemplateMapper.updateById(updateObj);
    }

    @VisibleForTesting
    public List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE,
            allEntries = true) // allEntries clears all caches because id is not directly the cached code, hard to clear individually
    public void deleteNotifyTemplate(Long id) {
        // validate existence
        validateNotifyTemplateExists(id);
        // delete
        notifyTemplateMapper.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE,
            allEntries = true) // allEntries clears all caches because id is not directly the cached code, hard to clear individually
    public void deleteNotifyTemplateList(List<Long> ids) {
        notifyTemplateMapper.deleteByIds(ids);
    }

    private void validateNotifyTemplateExists(Long id) {
        if (notifyTemplateMapper.selectById(id) == null) {
            throw exception(NOTIFY_TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public NotifyTemplateEntity getNotifyTemplate(Long id) {
        return notifyTemplateMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE, key = "#code",
            unless = "#result == null")
    public NotifyTemplateEntity getNotifyTemplateByCodeFromCache(String code) {
        return notifyTemplateMapper.selectByCode(code);
    }

    @Override
    public PageResult<NotifyTemplateEntity> getNotifyTemplatePage(NotifyTemplatePageRequest pageRequest) {
        return notifyTemplateMapper.selectPage(pageRequest);
    }

    @VisibleForTesting
    void validateNotifyTemplateCodeDuplicate(Long id, String code) {
        NotifyTemplateEntity template = notifyTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        // if id is null, no need to compare against a dictionary type with the same id
        if (id == null) {
            throw exception(NOTIFY_TEMPLATE_CODE_DUPLICATE, code);
        }
        if (!template.getId().equals(id)) {
            throw exception(NOTIFY_TEMPLATE_CODE_DUPLICATE, code);
        }
    }

    /**
     * Format in-site notification content
     *
     * @param content in-site notification template content
     * @param params  in-site notification content parameters
     * @return formatted content
     */
    @Override
    public String formatNotifyTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }

}
