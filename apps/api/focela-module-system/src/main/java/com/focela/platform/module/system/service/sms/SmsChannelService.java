package com.focela.platform.module.system.service.sms;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.module.system.controller.admin.sms.dto.channel.SmsChannelPageRequest;
import com.focela.platform.module.system.controller.admin.sms.dto.channel.SmsChannelSaveRequest;
import com.focela.platform.module.system.repository.entity.sms.SmsChannelEntity;
import com.focela.platform.module.system.config.sms.core.client.SmsClient;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 短信渠道 Service 接口
 *
 * @since 2021/1/25 9:24
 */
public interface SmsChannelService {

    /**
     * 创建短信渠道
     *
     * @param createRequest 创建信息
     * @return 编号
     */
    Long createSmsChannel(@Valid SmsChannelSaveRequest createRequest);

    /**
     * 更新短信渠道
     *
     * @param updateRequest 更新信息
     */
    void updateSmsChannel(@Valid SmsChannelSaveRequest updateRequest);

    /**
     * 删除短信渠道
     *
     * @param id 编号
     */
    void deleteSmsChannel(Long id);

    /**
     * 批量删除短信渠道
     *
     * @param ids 编号数组
     */
    void deleteSmsChannelList(List<Long> ids);

    /**
     * 获得短信渠道
     *
     * @param id 编号
     * @return 短信渠道
     */
    SmsChannelEntity getSmsChannel(Long id);

    /**
     * 获得所有短信渠道列表
     *
     * @return 短信渠道列表
     */
    List<SmsChannelEntity> getSmsChannelList();

    /**
     * 获得短信渠道分页
     *
     * @param pageRequest 分页查询
     * @return 短信渠道分页
     */
    PageResult<SmsChannelEntity> getSmsChannelPage(SmsChannelPageRequest pageRequest);

    /**
     * 获得短信客户端
     *
     * @param id 编号
     * @return 短信客户端
     */
    SmsClient getSmsClient(Long id);

    /**
     * 获得短信客户端
     *
     * @param code 编码
     * @return 短信客户端
     */
    SmsClient getSmsClient(String code);

}
