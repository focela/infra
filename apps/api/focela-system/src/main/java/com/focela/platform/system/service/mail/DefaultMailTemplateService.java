package com.focela.platform.system.service.mail;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.mail.dto.template.MailTemplatePageRequest;
import com.focela.platform.system.controller.admin.mail.dto.template.MailTemplateSaveRequest;
import com.focela.platform.system.domain.entity.mail.MailTemplateEntity;
import com.focela.platform.system.repository.mapper.mail.MailTemplateMapper;
import com.focela.platform.system.constants.RedisKeyConstants;
import com.google.common.annotations.VisibleForTesting;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.MAIL_TEMPLATE_CODE_EXISTS;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.MAIL_TEMPLATE_NOT_EXISTS;

/**
 * Mail template Service implementation class
 *
 * @since 2022-03-21
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class DefaultMailTemplateService implements MailTemplateService {

    /**
     * Regex matching variables inside {}
     */
    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    private final MailTemplateMapper mailTemplateMapper;

    @Override
    public Long createMailTemplate(MailTemplateSaveRequest createRequest) {
        // Validate uniqueness of the code
        validateCodeUnique(null, createRequest.getCode());

        // Insert
        MailTemplateEntity template = BeanUtils.toBean(createRequest, MailTemplateEntity.class)
                .setParams(parseTemplateTitleAndContentParams(createRequest.getTitle(), createRequest.getContent()));
        mailTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.MAIL_TEMPLATE,
            allEntries = true) // allEntries clears all caches because the code field may be modified, making targeted eviction hard
    public void updateMailTemplate(@Valid MailTemplateSaveRequest updateRequest) {
        // Validate existence
        validateMailTemplateExists(updateRequest.getId());
        // Validate uniqueness of the code
        validateCodeUnique(updateRequest.getId(),updateRequest.getCode());

        // Update
        MailTemplateEntity updateObj = BeanUtils.toBean(updateRequest, MailTemplateEntity.class)
                .setParams(parseTemplateTitleAndContentParams(updateRequest.getTitle(), updateRequest.getContent()));
        mailTemplateMapper.updateById(updateObj);
    }

    @VisibleForTesting
    void validateCodeUnique(Long id, String code) {
        MailTemplateEntity template = mailTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        // When a template record exists
        if (id == null // on create, duplicate
                || ObjUtil.notEqual(id, template.getId())) { // on update, duplicate if id differs
            throw exception(MAIL_TEMPLATE_CODE_EXISTS);
        }
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.MAIL_TEMPLATE,
            allEntries = true) // allEntries clears all caches because id is not the direct cache key (code is), making targeted eviction hard
    public void deleteMailTemplate(Long id) {
        // Validate existence
        validateMailTemplateExists(id);

        // Delete
        mailTemplateMapper.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.MAIL_TEMPLATE,
            allEntries = true) // allEntries clears all caches because id is not the direct cache key (code is), making targeted eviction hard
    public void deleteMailTemplateList(List<Long> ids) {
        mailTemplateMapper.deleteByIds(ids);
    }

    private void validateMailTemplateExists(Long id) {
        if (mailTemplateMapper.selectById(id) == null) {
            throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public MailTemplateEntity getMailTemplate(Long id) {return mailTemplateMapper.selectById(id);}

    @Override
    @Cacheable(value = RedisKeyConstants.MAIL_TEMPLATE, key = "#code", unless = "#result == null")
    public MailTemplateEntity getMailTemplateByCodeFromCache(String code) {
        return mailTemplateMapper.selectByCode(code);
    }

    @Override
    public PageResult<MailTemplateEntity> getMailTemplatePage(MailTemplatePageRequest pageRequest) {
        return mailTemplateMapper.selectPage(pageRequest);
    }

    @Override
    public List<MailTemplateEntity> getMailTemplateList() {return mailTemplateMapper.selectList();}

    @Override
    public String formatMailTemplateContent(String content, Map<String, Object> params) {
        // 1. Replace template variables first
        String formattedContent = StrUtil.format(content, params);

        // 2.1 Unescape HTML special characters
        formattedContent = unescapeHtml(formattedContent);
        // 2.2 Handle code blocks (ensure <pre><code> tags are correctly formatted)
        formattedContent = formatHtmlCodeBlocks(formattedContent);
        // 2.3 Replace the outermost pre tag with a div tag
        formattedContent = replaceOuterPreWithDiv(formattedContent);
        return formattedContent;
    }

    private String replaceOuterPreWithDiv(String content) {
        if (StrUtil.isEmpty(content)) {
            return content;
        }
        // Use regex to match all <pre> tags, including nested <code> tags
        String regex = "(?s)<pre[^>]*>(.*?)</pre>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            // Extract the content inside the <pre> tag
            String innerContent = matcher.group(1);
            // Return the content wrapped in a div tag
            matcher.appendReplacement(sb, "<div>" + innerContent + "</div>");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Unescape HTML special characters
     *
     * @param input input string
     * @return unescaped string
     */
    private String unescapeHtml(String input) {
        if (StrUtil.isEmpty(input)) {
            return input;
        }
        return input
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&nbsp;", " ");
    }

    /**
     * Format code blocks in HTML
     *
     * @param content mail content
     * @return formatted mail content
     */
    private String formatHtmlCodeBlocks(String content) {
        // Match code blocks wrapped by <pre><code> tags
        Pattern codeBlockPattern = Pattern.compile("<pre\\s*.*?><code\\s*.*?>(.*?)</code></pre>", Pattern.DOTALL);
        Matcher matcher = codeBlockPattern.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            // Get the code block content
            String codeBlock = matcher.group(1);
            // Apply styling to the code block
            String replacement = "<pre style=\"background-color: #f5f5f5; padding: 10px; border-radius: 5px; overflow-x: auto;\"><code>" + codeBlock + "</code></pre>";
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    public long getMailTemplateCountByAccountId(Long accountId) {
        return mailTemplateMapper.selectCountByAccountId(accountId);
    }

    /**
     * Parse parameters from the title and content
     */
    @VisibleForTesting
    public List<String> parseTemplateTitleAndContentParams(String title, String content) {
        List<String> titleParams = ReUtil.findAllGroup1(PATTERN_PARAMS, title);
        List<String> contentParams = ReUtil.findAllGroup1(PATTERN_PARAMS, content);
        // Merge parameters and deduplicate
        List<String> allParams = new ArrayList<>(titleParams);
        for (String param : contentParams) {
            if (!allParams.contains(param)) {
                allParams.add(param);
            }
        }
        return allParams;
    }

    /**
     * Get the parameters in the mail template, in the form of {key}
     *
     * @param content content
     * @return parameter list
     */
    List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

}