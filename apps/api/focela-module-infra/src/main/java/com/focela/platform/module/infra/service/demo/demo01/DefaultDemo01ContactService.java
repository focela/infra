package com.focela.platform.module.infra.service.demo.demo01;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.infra.controller.admin.demo.demo01.dto.Demo01ContactPageRequest;
import com.focela.platform.module.infra.controller.admin.demo.demo01.dto.Demo01ContactSaveRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo01.Demo01ContactEntity;
import com.focela.platform.module.infra.repository.mapper.demo.demo01.Demo01ContactMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.focela.platform.framework.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.module.infra.enums.ErrorCodeConstants.DEMO01_CONTACT_NOT_EXISTS;

/**
 * 示例联系人 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DefaultDemo01ContactService implements Demo01ContactService {

    @Resource
    private Demo01ContactMapper demo01ContactMapper;

    @Override
    public Long createDemo01Contact(Demo01ContactSaveRequest createRequest) {
        // 插入
        Demo01ContactEntity demo01Contact = BeanUtils.toBean(createRequest, Demo01ContactEntity.class);
        demo01ContactMapper.insert(demo01Contact);
        // 返回
        return demo01Contact.getId();
    }

    @Override
    public void updateDemo01Contact(Demo01ContactSaveRequest updateRequest) {
        // 校验存在
        validateDemo01ContactExists(updateRequest.getId());
        // 更新
        Demo01ContactEntity updateObj = BeanUtils.toBean(updateRequest, Demo01ContactEntity.class);
        demo01ContactMapper.updateById(updateObj);
    }

    @Override
    public void deleteDemo01Contact(Long id) {
        // 校验存在
        validateDemo01ContactExists(id);
        // 删除
        demo01ContactMapper.deleteById(id);
    }

    @Override
    public void deleteDemo0iContactList(List<Long> ids) {
        // 校验存在
        validateDemo01ContactExists(ids);
        // 删除
        demo01ContactMapper.deleteByIds(ids);
    }

    private void validateDemo01ContactExists(List<Long> ids) {
        List<Demo01ContactEntity> list = demo01ContactMapper.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(DEMO01_CONTACT_NOT_EXISTS);
        }
    }

    private void validateDemo01ContactExists(Long id) {
        if (demo01ContactMapper.selectById(id) == null) {
            throw exception(DEMO01_CONTACT_NOT_EXISTS);
        }
    }

    @Override
    public Demo01ContactEntity getDemo01Contact(Long id) {
        return demo01ContactMapper.selectById(id);
    }

    @Override
    public PageResult<Demo01ContactEntity> getDemo01ContactPage(Demo01ContactPageRequest pageRequest) {
        return demo01ContactMapper.selectPage(pageRequest);
    }

}