package com.focela.platform.system.service.notify;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.notify.dto.template.NotifyTemplatePageRequest;
import com.focela.platform.system.controller.admin.notify.dto.template.NotifyTemplateSaveRequest;
import com.focela.platform.system.entity.notify.NotifyTemplateEntity;
import com.focela.platform.system.repository.mapper.notify.NotifyTemplateMapper;
import com.focela.platform.system.repository.redis.RedisKeyConstants;
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

import static com.focela.platform.framework.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.ErrorCodeConstants.NOTIFY_TEMPLATE_CODE_DUPLICATE;
import static com.focela.platform.system.constants.ErrorCodeConstants.NOTIFY_TEMPLATE_NOT_EXISTS;

/**
 * 站内信模版 Service 实现类
 */
@Service
@Validated
@Slf4j
public class DefaultNotifyTemplateService implements NotifyTemplateService {

    /**
     * 正则表达式，匹配 {} 中的变量
     */
    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    @Resource
    private NotifyTemplateMapper notifyTemplateMapper;

    @Override
    public Long createNotifyTemplate(NotifyTemplateSaveRequest createRequest) {
        // 校验站内信编码是否重复
        validateNotifyTemplateCodeDuplicate(null, createRequest.getCode());

        // 插入
        NotifyTemplateEntity notifyTemplate = BeanUtils.toBean(createRequest, NotifyTemplateEntity.class);
        notifyTemplate.setParams(parseTemplateContentParams(notifyTemplate.getContent()));
        notifyTemplateMapper.insert(notifyTemplate);
        return notifyTemplate.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE,
            allEntries = true) // allEntries 清空所有缓存，因为可能修改到 code 字段，不好清理
    public void updateNotifyTemplate(NotifyTemplateSaveRequest updateRequest) {
        // 校验存在
        validateNotifyTemplateExists(updateRequest.getId());
        // 校验站内信编码是否重复
        validateNotifyTemplateCodeDuplicate(updateRequest.getId(), updateRequest.getCode());

        // 更新
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
            allEntries = true) // allEntries 清空所有缓存，因为 id 不是直接的缓存 code，不好清理
    public void deleteNotifyTemplate(Long id) {
        // 校验存在
        validateNotifyTemplateExists(id);
        // 删除
        notifyTemplateMapper.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE,
            allEntries = true) // allEntries 清空所有缓存，因为 id 不是直接的缓存 code，不好清理
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
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(NOTIFY_TEMPLATE_CODE_DUPLICATE, code);
        }
        if (!template.getId().equals(id)) {
            throw exception(NOTIFY_TEMPLATE_CODE_DUPLICATE, code);
        }
    }

    /**
     * 格式化站内信内容
     *
     * @param content 站内信模板的内容
     * @param params  站内信内容的参数
     * @return 格式化后的内容
     */
    @Override
    public String formatNotifyTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }

}
