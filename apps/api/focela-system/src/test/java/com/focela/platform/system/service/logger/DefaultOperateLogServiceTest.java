package com.focela.platform.system.service.logger;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.test.core.utils.RandomUtils;
import com.focela.platform.common.api.system.logger.dto.OperateLogCreateRpcRequest;
import com.focela.platform.system.api.logger.dto.OperateLogPageRpcRequest;
import com.focela.platform.system.controller.admin.logger.dto.operatelog.OperateLogPageRequest;
import com.focela.platform.system.entity.logger.OperateLogEntity;
import com.focela.platform.system.repository.mapper.logger.OperateLogMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({DefaultOperateLogService.class})
public class DefaultOperateLogServiceTest extends BaseDbUnitTest {

    @Resource
    private OperateLogService DefaultoperateLogService;

    @Resource
    private OperateLogMapper operateLogMapper;

    @Test
    public void testCreateOperateLog() {
        OperateLogCreateRpcRequest request = RandomUtils.randomPojo(OperateLogCreateRpcRequest.class);

        // 调研
        DefaultoperateLogService.createOperateLog(request);
        // 断言
        OperateLogEntity operateLogDO = operateLogMapper.selectOne(null);
        assertPojoEquals(request, operateLogDO);
    }

    @Test
    public void testGetOperateLogPage_vo() {
        // 构造操作日志
        OperateLogEntity operateLogDO = RandomUtils.randomPojo(OperateLogEntity.class, o -> {
            o.setUserId(2048L);
            o.setBizId(999L);
            o.setType("订单");
            o.setSubType("创建订单");
            o.setAction("修改编号为 1 的用户信息");
            o.setCreateTime(buildTime(2021, 3, 6));
        });
        operateLogMapper.insert(operateLogDO);
        // 测试 userId 不匹配
        operateLogMapper.insert(cloneIgnoreId(operateLogDO, o -> o.setUserId(1024L)));
        // 测试 bizId 不匹配
        operateLogMapper.insert(cloneIgnoreId(operateLogDO, o -> o.setBizId(888L)));
        // 测试 type 不匹配
        operateLogMapper.insert(cloneIgnoreId(operateLogDO, o -> o.setType("退款")));
        // 测试 subType 不匹配
        operateLogMapper.insert(cloneIgnoreId(operateLogDO, o -> o.setSubType("创建退款")));
        // 测试 action 不匹配
        operateLogMapper.insert(cloneIgnoreId(operateLogDO, o -> o.setAction("修改编号为 1 退款信息")));
        // 测试 createTime 不匹配
        operateLogMapper.insert(cloneIgnoreId(operateLogDO, o -> o.setCreateTime(buildTime(2021, 2, 6))));

        // 构造调用参数
        OperateLogPageRequest request = new OperateLogPageRequest();
        request.setUserId(2048L);
        request.setBizId(999L);
        request.setType("订");
        request.setSubType("订单");
        request.setAction("用户信息");
        request.setCreateTime(buildBetweenTime(2021, 3, 5, 2021, 3, 7));

        // 调用
        PageResult<OperateLogEntity> pageResult = DefaultoperateLogService.getOperateLogPage(request);
        // 断言，只查到了一条符合条件的
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(operateLogDO, pageResult.getList().get(0));
    }

    @Test
    public void testGetOperateLogPage_dto() {
        // 构造操作日志
        OperateLogEntity operateLogDO = RandomUtils.randomPojo(OperateLogEntity.class, o -> {
            o.setUserId(2048L);
            o.setBizId(999L);
            o.setType("订单");
        });
        operateLogMapper.insert(operateLogDO);
        // 测试 userId 不匹配
        operateLogMapper.insert(cloneIgnoreId(operateLogDO, o -> o.setUserId(1024L)));
        // 测试 bizId 不匹配
        operateLogMapper.insert(cloneIgnoreId(operateLogDO, o -> o.setBizId(888L)));
        // 测试 type 不匹配
        operateLogMapper.insert(cloneIgnoreId(operateLogDO, o -> o.setType("退款")));

        // 构造调用参数
        OperateLogPageRpcRequest reqDTO = new OperateLogPageRpcRequest();
        reqDTO.setUserId(2048L);
        reqDTO.setBizId(999L);
        reqDTO.setType("订单");

        // 调用
        PageResult<OperateLogEntity> pageResult = DefaultoperateLogService.getOperateLogPage(reqDTO);
        // 断言，只查到了一条符合条件的
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(operateLogDO, pageResult.getList().get(0));
    }

}
