package com.focela.platform.module.infra.service.codegen.inner;

import com.focela.platform.module.infra.repository.entity.codegen.CodegenColumnEntity;
import com.focela.platform.module.infra.repository.entity.codegen.CodegenTableEntity;
import com.focela.platform.module.infra.enums.codegen.CodegenFrontTypeEnum;
import com.focela.platform.module.infra.enums.codegen.CodegenTemplateTypeEnum;
import com.baomidou.mybatisplus.annotation.DbType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * {@link CodegenEngine} 的 Vue2 + Element Plus 单元测试
 *
 * @author 芋道源码
 */
@Disabled
public class CodegenEngineVue3Test extends CodegenEngineAbstractTest {

    @Test
    public void testExecute_vue3_one() {
        // 准备参数
        CodegenTableEntity table = getTable("student")
                .setFrontType(CodegenFrontTypeEnum.VUE3_ELEMENT_PLUS.getType())
                .setTemplateType(CodegenTemplateTypeEnum.ONE.getType());
        List<CodegenColumnEntity> columns = getColumnList("student");

        // 调用
        Map<String, String> result = codegenEngine.execute(DbType.MYSQL, table, columns, null, null);
        // 生成测试文件
        //writeResult(result, resourcesPath + "/vue3_one");
        // 断言
        assertResult(result, "/vue3_one");
    }

    @Test
    public void testExecute_vue3_tree() {
        // 准备参数
        CodegenTableEntity table = getTable("category")
                .setFrontType(CodegenFrontTypeEnum.VUE3_ELEMENT_PLUS.getType())
                .setTemplateType(CodegenTemplateTypeEnum.TREE.getType());
        List<CodegenColumnEntity> columns = getColumnList("category");

        // 调用
        Map<String, String> result = codegenEngine.execute(DbType.MYSQL, table, columns, null, null);
        // 生成测试文件
        //writeResult(result, resourcesPath + "/vue3_tree");
        // 断言
        assertResult(result, "/vue3_tree");
//        writeFile(result, "/Users/yunai/test/demo66.zip");
    }

    @Test
    public void testExecute_vue3_master_normal() {
        testExecute_vue3_master(CodegenTemplateTypeEnum.MASTER_NORMAL, "/vue3_master_normal");
    }

    @Test
    public void testExecute_vue3_master_erp() {
        testExecute_vue3_master(CodegenTemplateTypeEnum.MASTER_ERP, "/vue3_master_erp");
    }

    @Test
    public void testExecute_vue3_master_inner() {
        testExecute_vue3_master(CodegenTemplateTypeEnum.MASTER_INNER, "/vue3_master_inner");
    }

    private void testExecute_vue3_master(CodegenTemplateTypeEnum templateType,
                                         String path) {
        // 准备参数
        CodegenTableEntity table = getTable("student")
                .setFrontType(CodegenFrontTypeEnum.VUE3_ELEMENT_PLUS.getType())
                .setTemplateType(templateType.getType());
        List<CodegenColumnEntity> columns = getColumnList("student");
        // 准备参数（子表）
        CodegenTableEntity contactTable = getTable("contact")
                .setTemplateType(CodegenTemplateTypeEnum.SUB.getType())
                .setFrontType(CodegenFrontTypeEnum.VUE3_ELEMENT_PLUS.getType())
                .setSubJoinColumnId(100L).setSubJoinMany(true);
        List<CodegenColumnEntity> contactColumns = getColumnList("contact");
        // 准备参数（班主任）
        CodegenTableEntity teacherTable = getTable("teacher")
                .setTemplateType(CodegenTemplateTypeEnum.SUB.getType())
                .setFrontType(CodegenFrontTypeEnum.VUE3_ELEMENT_PLUS.getType())
                .setSubJoinColumnId(200L).setSubJoinMany(false);
        List<CodegenColumnEntity> teacherColumns = getColumnList("teacher");

        // 调用
        Map<String, String> result = codegenEngine.execute(DbType.MYSQL, table, columns,
                Arrays.asList(contactTable, teacherTable), Arrays.asList(contactColumns, teacherColumns));
        // 生成测试文件
        //writeResult(result, resourcesPath + path);
        // 断言
        assertResult(result, path);
        // writeFile(result, "/Users/yunai/test/demo11.zip");
    }

}
