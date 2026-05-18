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

        // invoke
        DefaultoperateLogService.createOperateLog(request);
        // assert
        OperateLogEntity operateLogEntity = operateLogMapper.selectOne(null);
        assertPojoEquals(request, operateLogEntity);
    }

    @Test
    public void testGetOperateLogPage_byPageRequest() {
        // build operate log
        OperateLogEntity operateLogEntity = RandomUtils.randomPojo(OperateLogEntity.class, o -> {
            o.setUserId(2048L);
            o.setBizId(999L);
            o.setType("order");
            o.setSubType("Create order");
            o.setAction("Modify user info with ID 1");
            o.setCreateTime(buildTime(2021, 3, 6));
        });
        operateLogMapper.insert(operateLogEntity);
        // test userId mismatch
        operateLogMapper.insert(cloneIgnoreId(operateLogEntity, o -> o.setUserId(1024L)));
        // test bizId mismatch
        operateLogMapper.insert(cloneIgnoreId(operateLogEntity, o -> o.setBizId(888L)));
        // test type mismatch
        operateLogMapper.insert(cloneIgnoreId(operateLogEntity, o -> o.setType("refund")));
        // test subType mismatch
        operateLogMapper.insert(cloneIgnoreId(operateLogEntity, o -> o.setSubType("create refund")));
        // test action mismatch
        operateLogMapper.insert(cloneIgnoreId(operateLogEntity, o -> o.setAction("update refund info with ID 1")));
        // test createTime mismatch
        operateLogMapper.insert(cloneIgnoreId(operateLogEntity, o -> o.setCreateTime(buildTime(2021, 2, 6))));

        // build call parameters
        OperateLogPageRequest request = new OperateLogPageRequest();
        request.setUserId(2048L);
        request.setBizId(999L);
        request.setType("order");
        request.setSubType("order");
        request.setAction("user info");
        request.setCreateTime(buildBetweenTime(2021, 3, 5, 2021, 3, 7));

        // invoke
        PageResult<OperateLogEntity> pageResult = DefaultoperateLogService.getOperateLogPage(request);
        // assert only one matching record was found
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(operateLogEntity, pageResult.getList().get(0));
    }

    @Test
    public void testGetOperateLogPage_byRpcRequest() {
        // build operate log
        OperateLogEntity operateLogEntity = RandomUtils.randomPojo(OperateLogEntity.class, o -> {
            o.setUserId(2048L);
            o.setBizId(999L);
            o.setType("order");
        });
        operateLogMapper.insert(operateLogEntity);
        // test userId mismatch
        operateLogMapper.insert(cloneIgnoreId(operateLogEntity, o -> o.setUserId(1024L)));
        // test bizId mismatch
        operateLogMapper.insert(cloneIgnoreId(operateLogEntity, o -> o.setBizId(888L)));
        // test type mismatch
        operateLogMapper.insert(cloneIgnoreId(operateLogEntity, o -> o.setType("refund")));

        // build call parameters
        OperateLogPageRpcRequest request = new OperateLogPageRpcRequest();
        request.setUserId(2048L);
        request.setBizId(999L);
        request.setType("order");

        // invoke
        PageResult<OperateLogEntity> pageResult = DefaultoperateLogService.getOperateLogPage(request);
        // assert only one matching record was found
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(operateLogEntity, pageResult.getList().get(0));
    }

}
