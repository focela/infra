package com.focela.platform.infra.service.database;

import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.infra.domain.entity.database.DataSourceConfigEntity;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import jakarta.annotation.Resource;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.focela.platform.test.core.utils.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Import(DefaultDatabaseTableService.class)
public class DefaultDatabaseTableServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultDatabaseTableService databaseTableService;

    @MockitoBean
    private DataSourceConfigService dataSourceConfigService;

    @Test
    public void getTableList() {
        // prepare parameters
        Long dataSourceConfigId = randomLongId();
        // mock the method
        DataSourceConfigEntity dataSourceConfig = new DataSourceConfigEntity().setUsername("sa").setPassword("")
                .setUrl("jdbc:h2:mem:testdb");
        when(dataSourceConfigService.getDataSourceConfig(eq(dataSourceConfigId)))
                .thenReturn(dataSourceConfig);

        // invoke
        List<TableInfo> tables = databaseTableService.getTableList(dataSourceConfigId,
                "config", "Parameter");
        // assert
        assertEquals(1, tables.size());
        assertTableInfo(tables.get(0));
    }

    @Test
    public void getTable() {
        // prepare parameters
        Long dataSourceConfigId = randomLongId();
        // mock the method
        DataSourceConfigEntity dataSourceConfig = new DataSourceConfigEntity().setUsername("sa").setPassword("")
                .setUrl("jdbc:h2:mem:testdb");
        when(dataSourceConfigService.getDataSourceConfig(eq(dataSourceConfigId)))
                .thenReturn(dataSourceConfig);

        // invoke
        TableInfo tableInfo = databaseTableService.getTable(dataSourceConfigId, "infra_config");
        // assert
        assertTableInfo(tableInfo);
    }

    private void assertTableInfo(TableInfo tableInfo) {
        assertEquals("infra_config", tableInfo.getName());
        assertEquals("Parameter configuration table", tableInfo.getComment());
        assertEquals(13, tableInfo.getFields().size());
        // id field
        TableField idField = tableInfo.getFields().get(0);
        assertEquals("id", idField.getName());
        assertEquals(JdbcType.BIGINT, idField.getMetaInfo().getJdbcType());
        assertEquals("ID", idField.getComment());
        assertFalse(idField.getMetaInfo().isNullable());
        assertTrue(idField.isKeyFlag());
        assertTrue(idField.isKeyIdentityFlag());
        assertEquals(DbColumnType.LONG, idField.getColumnType());
        assertEquals("id", idField.getPropertyName());
        // name field
        TableField nameField = tableInfo.getFields().get(3);
        assertEquals("name", nameField.getName());
        assertEquals(JdbcType.VARCHAR, nameField.getMetaInfo().getJdbcType());
        assertEquals("Name", nameField.getComment());
        assertFalse(nameField.getMetaInfo().isNullable());
        assertFalse(nameField.isKeyFlag());
        assertFalse(nameField.isKeyIdentityFlag());
        assertEquals(DbColumnType.STRING, nameField.getColumnType());
        assertEquals("name", nameField.getPropertyName());
    }
}
