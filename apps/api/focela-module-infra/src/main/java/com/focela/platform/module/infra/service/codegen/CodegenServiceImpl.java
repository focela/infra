package com.focela.platform.module.infra.service.codegen;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.framework.mybatis.core.util.JdbcUtils;
import com.focela.platform.module.infra.controller.admin.codegen.vo.CodegenCreateListReqVO;
import com.focela.platform.module.infra.controller.admin.codegen.vo.CodegenUpdateReqVO;
import com.focela.platform.module.infra.controller.admin.codegen.vo.table.CodegenTablePageReqVO;
import com.focela.platform.module.infra.controller.admin.codegen.vo.table.DatabaseTableRespVO;
import com.focela.platform.module.infra.repository.entity.codegen.CodegenColumnEntity;
import com.focela.platform.module.infra.repository.entity.codegen.CodegenTableEntity;
import com.focela.platform.module.infra.repository.entity.db.DataSourceConfigEntity;
import com.focela.platform.module.infra.repository.mapper.codegen.CodegenColumnMapper;
import com.focela.platform.module.infra.repository.mapper.codegen.CodegenTableMapper;
import com.focela.platform.module.infra.enums.codegen.CodegenSceneEnum;
import com.focela.platform.module.infra.enums.codegen.CodegenTemplateTypeEnum;
import com.focela.platform.module.infra.framework.codegen.config.CodegenProperties;
import com.focela.platform.module.infra.service.codegen.inner.CodegenBuilder;
import com.focela.platform.module.infra.service.codegen.inner.CodegenEngine;
import com.focela.platform.module.infra.service.db.DataSourceConfigService;
import com.focela.platform.module.infra.service.db.DatabaseTableService;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.focela.platform.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.focela.platform.framework.common.util.collection.CollectionUtils.convertMap;
import static com.focela.platform.framework.common.util.collection.CollectionUtils.convertSet;
import static com.focela.platform.module.infra.enums.ErrorCodeConstants.*;

/**
 * 代码生成 Service 实现类
 *
 * @author 芋道源码
 */
@Service
public class CodegenServiceImpl implements CodegenService {

    @Resource
    private DatabaseTableService databaseTableService;
    @Resource
    private DataSourceConfigService dataSourceConfigService;

    @Resource
    private CodegenTableMapper codegenTableMapper;
    @Resource
    private CodegenColumnMapper codegenColumnMapper;

    @Resource
    private CodegenBuilder codegenBuilder;
    @Resource
    private CodegenEngine codegenEngine;

    @Resource
    private CodegenProperties codegenProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createCodegenList(String author, CodegenCreateListReqVO reqVO) {
        List<Long> ids = new ArrayList<>(reqVO.getTableNames().size());
        // 遍历添加。虽然效率会低一点，但是没必要做成完全批量，因为不会这么大量
        reqVO.getTableNames().forEach(tableName -> ids.add(createCodegen(author, reqVO.getDataSourceConfigId(), tableName)));
        return ids;
    }

    private Long createCodegen(String author, Long dataSourceConfigId, String tableName) {
        // 从数据库中，获得数据库表结构
        TableInfo tableInfo = databaseTableService.getTable(dataSourceConfigId, tableName);
        // 导入
        return createCodegen0(author, dataSourceConfigId, tableInfo);
    }

    private Long createCodegen0(String author, Long dataSourceConfigId, TableInfo tableInfo) {
        // 校验导入的表和字段非空
        validateTableInfo(tableInfo);
        // 校验是否已经存在
        if (codegenTableMapper.selectByTableNameAndDataSourceConfigId(tableInfo.getName(),
                dataSourceConfigId) != null) {
            throw exception(CODEGEN_TABLE_EXISTS);
        }

        // 构建 CodegenTableEntity 对象，插入到 DB 中
        CodegenTableEntity table = codegenBuilder.buildTable(tableInfo);
        table.setDataSourceConfigId(dataSourceConfigId);
        table.setScene(CodegenSceneEnum.ADMIN.getScene()); // 默认配置下，使用管理后台的模板
        table.setFrontType(codegenProperties.getFrontType());
        table.setAuthor(author);
        codegenTableMapper.insert(table);

        // 构建 CodegenColumnEntity 数组，插入到 DB 中
        List<CodegenColumnEntity> columns = codegenBuilder.buildColumns(table.getId(), tableInfo.getFields());
        // 如果没有主键，则使用第一个字段作为主键
        if (!tableInfo.isHavePrimaryKey()) {
            columns.get(0).setPrimaryKey(true);
        }
        codegenColumnMapper.insertBatch(columns);
        return table.getId();
    }

    @VisibleForTesting
    void validateTableInfo(TableInfo tableInfo) {
        if (tableInfo == null) {
            throw exception(CODEGEN_IMPORT_TABLE_NULL);
        }
        if (StrUtil.isEmpty(tableInfo.getComment())) {
            throw exception(CODEGEN_TABLE_INFO_TABLE_COMMENT_IS_NULL);
        }
        if (CollUtil.isEmpty(tableInfo.getFields())) {
            throw exception(CODEGEN_IMPORT_COLUMNS_NULL);
        }
        tableInfo.getFields().forEach(field -> {
            if (StrUtil.isEmpty(field.getComment())) {
                throw exception(CODEGEN_TABLE_INFO_COLUMN_COMMENT_IS_NULL, field.getName());
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCodegen(CodegenUpdateReqVO updateReqVO) {
        // 校验是否已经存在
        if (codegenTableMapper.selectById(updateReqVO.getTable().getId()) == null) {
            throw exception(CODEGEN_TABLE_NOT_EXISTS);
        }
        // 校验主表字段存在
        if (Objects.equals(updateReqVO.getTable().getTemplateType(), CodegenTemplateTypeEnum.SUB.getType())) {
            if (codegenTableMapper.selectById(updateReqVO.getTable().getMasterTableId()) == null) {
                throw exception(CODEGEN_MASTER_TABLE_NOT_EXISTS, updateReqVO.getTable().getMasterTableId());
            }
            if (CollUtil.findOne(updateReqVO.getColumns(),  // 关联主表的字段不存在
                    column -> column.getId().equals(updateReqVO.getTable().getSubJoinColumnId())) == null) {
                throw exception(CODEGEN_SUB_COLUMN_NOT_EXISTS, updateReqVO.getTable().getSubJoinColumnId());
            }
        }

        // 更新 table 表定义
        CodegenTableEntity updateTableObj = BeanUtils.toBean(updateReqVO.getTable(), CodegenTableEntity.class);
        codegenTableMapper.updateById(updateTableObj);
        // 更新 column 字段定义
        List<CodegenColumnEntity> updateColumnObjs = BeanUtils.toBean(updateReqVO.getColumns(), CodegenColumnEntity.class);
        updateColumnObjs.forEach(updateColumnObj -> codegenColumnMapper.updateById(updateColumnObj));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncCodegenFromDB(Long tableId) {
        // 校验是否已经存在
        CodegenTableEntity table = codegenTableMapper.selectById(tableId);
        if (table == null) {
            throw exception(CODEGEN_TABLE_NOT_EXISTS);
        }
        // 从数据库中，获得数据库表结构
        TableInfo tableInfo = databaseTableService.getTable(table.getDataSourceConfigId(), table.getTableName());
        // 执行同步
        syncCodegen0(tableId, tableInfo);
    }

    private void syncCodegen0(Long tableId, TableInfo tableInfo) {
        // 1. 校验导入的表和字段非空
        validateTableInfo(tableInfo);
        List<TableField> tableFields = tableInfo.getFields();

        // 2. 构建 CodegenColumnEntity 数组，只同步新增的字段
        List<CodegenColumnEntity> codegenColumns = codegenColumnMapper.selectListByTableId(tableId);
        Set<String> codegenColumnNames = convertSet(codegenColumns, CodegenColumnEntity::getColumnName);

        // 3.1 计算需要【修改】的字段，插入时重新插入，删除时将原来的删除
        Map<String, CodegenColumnEntity> codegenColumnDOMap = convertMap(codegenColumns, CodegenColumnEntity::getColumnName);
        BiPredicate<TableField, CodegenColumnEntity> primaryKeyPredicate =
                (tableField, codegenColumn) -> tableField.getMetaInfo().getJdbcType().name().equals(codegenColumn.getDataType())
                        && tableField.getMetaInfo().isNullable() == codegenColumn.getNullable()
                        && tableField.isKeyFlag() == codegenColumn.getPrimaryKey()
                        && tableField.getComment().equals(codegenColumn.getColumnComment());
        Set<String> modifyFieldNames = IntStream.range(0, tableFields.size()).mapToObj(index -> {
            TableField tableField = tableFields.get(index);
            String columnName = tableField.getColumnName();
            CodegenColumnEntity codegenColumn = codegenColumnDOMap.get(columnName);
            if (codegenColumn == null) {
                return null;
            }
            if (!primaryKeyPredicate.test(tableField, codegenColumn) || codegenColumn.getOrdinalPosition() != index) {
                return columnName;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        // 3.2 计算需要【删除】的字段
        Set<String> tableFieldNames = convertSet(tableFields, TableField::getName);
        Set<Long> deleteColumnIds = codegenColumns.stream()
                .filter(column -> (!tableFieldNames.contains(column.getColumnName())) || modifyFieldNames.contains(column.getColumnName()))
                .map(CodegenColumnEntity::getId).collect(Collectors.toSet());
        // 移除已经存在的字段
        tableFields.removeIf(column -> codegenColumnNames.contains(column.getColumnName()) && (!modifyFieldNames.contains(column.getColumnName())));
        if (CollUtil.isEmpty(tableFields) && CollUtil.isEmpty(deleteColumnIds)) {
            throw exception(CODEGEN_SYNC_NONE_CHANGE);
        }

        // 4.1 插入新增的字段
        List<CodegenColumnEntity> columns = codegenBuilder.buildColumns(tableId, tableFields);
        codegenColumnMapper.insertBatch(columns);
        // 4.2 删除不存在的字段
        if (CollUtil.isNotEmpty(deleteColumnIds)) {
            codegenColumnMapper.deleteByIds(deleteColumnIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCodegen(Long tableId) {
        // 校验是否已经存在
        if (codegenTableMapper.selectById(tableId) == null) {
            throw exception(CODEGEN_TABLE_NOT_EXISTS);
        }

        // 删除 table 表定义
        codegenTableMapper.deleteById(tableId);
        // 删除 column 字段定义
        codegenColumnMapper.deleteListByTableId(tableId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCodegenList(List<Long> tableIds) {
        // 批量删除 table 表定义
        codegenTableMapper.deleteByIds(tableIds);
        // 批量删除 column 字段定义
        codegenColumnMapper.deleteListByTableId(tableIds);
    }

    @Override
    public List<CodegenTableEntity> getCodegenTableList(Long dataSourceConfigId) {
        return codegenTableMapper.selectListByDataSourceConfigId(dataSourceConfigId);
    }

    @Override
    public PageResult<CodegenTableEntity> getCodegenTablePage(CodegenTablePageReqVO pageReqVO) {
        return codegenTableMapper.selectPage(pageReqVO);
    }

    @Override
    public CodegenTableEntity getCodegenTable(Long id) {
        return codegenTableMapper.selectById(id);
    }

    @Override
    public List<CodegenColumnEntity> getCodegenColumnListByTableId(Long tableId) {
        return codegenColumnMapper.selectListByTableId(tableId);
    }

    @Override
    public Map<String, String> generationCodes(Long tableId) {
        // 校验是否已经存在
        CodegenTableEntity table = codegenTableMapper.selectById(tableId);
        if (table == null) {
            throw exception(CODEGEN_TABLE_NOT_EXISTS);
        }
        List<CodegenColumnEntity> columns = codegenColumnMapper.selectListByTableId(tableId);
        if (CollUtil.isEmpty(columns)) {
            throw exception(CODEGEN_COLUMN_NOT_EXISTS);
        }

        // 如果是主子表，则加载对应的子表信息
        List<CodegenTableEntity> subTables = null;
        List<List<CodegenColumnEntity>> subColumnsList = null;
        if (CodegenTemplateTypeEnum.isMaster(table.getTemplateType())) {
            // 校验子表存在
            subTables = codegenTableMapper.selectListByTemplateTypeAndMasterTableId(
                    CodegenTemplateTypeEnum.SUB.getType(), tableId);
            if (CollUtil.isEmpty(subTables)) {
                throw exception(CODEGEN_MASTER_GENERATION_FAIL_NO_SUB_TABLE);
            }
            // 校验子表的关联字段存在
            subColumnsList = new ArrayList<>();
            for (CodegenTableEntity subTable : subTables) {
                List<CodegenColumnEntity> subColumns = codegenColumnMapper.selectListByTableId(subTable.getId());
                if (CollUtil.findOne(subColumns, column -> column.getId().equals(subTable.getSubJoinColumnId())) == null) {
                    throw exception(CODEGEN_SUB_COLUMN_NOT_EXISTS, subTable.getId());
                }
                subColumnsList.add(subColumns);
            }
        }

        // 获取数据源对应的数据库类型
        DataSourceConfigEntity dataSourceConfig = dataSourceConfigService.getDataSourceConfig(table.getDataSourceConfigId());
        DbType dbType = JdbcUtils.getDbType(dataSourceConfig.getUrl());
        // 执行生成
        return codegenEngine.execute(dbType, table, columns, subTables, subColumnsList);
    }

    @Override
    public List<DatabaseTableRespVO> getDatabaseTableList(Long dataSourceConfigId, String name, String comment) {
        List<TableInfo> tables = databaseTableService.getTableList(dataSourceConfigId, name, comment);
        // 移除在 Codegen 中，已经存在的
        Set<String> existsTables = convertSet(
                codegenTableMapper.selectListByDataSourceConfigId(dataSourceConfigId), CodegenTableEntity::getTableName);
        tables.removeIf(table -> existsTables.contains(table.getName()));
        return BeanUtils.toBean(tables, DatabaseTableRespVO.class);
    }

}
