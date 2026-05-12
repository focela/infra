package com.focela.platform.module.system.service.logger;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.business.system.logger.dto.OperateLogCreateReqDTO;
import com.focela.platform.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.focela.platform.module.system.controller.admin.logger.dto.operatelog.OperateLogPageRequest;
import com.focela.platform.module.system.repository.entity.logger.OperateLogEntity;

/**
 * 操作日志 Service 接口
 */
public interface OperateLogService {

    /**
     * 记录操作日志
     *
     * @param createReqDTO 创建请求
     */
    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    /**
     * 获得操作日志
     *
     * @param id 编号
     * @return 操作日志
     */
    OperateLogEntity getOperateLog(Long id);

    /**
     * 获得操作日志分页列表
     *
     * @param pageRequest 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogEntity> getOperateLogPage(OperateLogPageRequest pageRequest);

    /**
     * 获得操作日志分页列表
     *
     * @param pageRequest 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogEntity> getOperateLogPage(OperateLogPageReqDTO pageRequest);

}
