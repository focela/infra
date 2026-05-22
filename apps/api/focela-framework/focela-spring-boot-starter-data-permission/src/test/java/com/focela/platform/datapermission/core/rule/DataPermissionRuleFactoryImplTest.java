package com.focela.platform.datapermission.core.rule;

import com.focela.platform.datapermission.core.annotation.DataPermission;
import com.focela.platform.datapermission.core.aop.DataPermissionContextHolder;
import com.focela.platform.test.core.support.BaseMockitoUnitTest;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.focela.platform.test.core.utils.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DefaultDataPermissionRuleFactory}.
 */
class DataPermissionRuleFactoryImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DefaultDataPermissionRuleFactory dataPermissionRuleFactory;

    @Spy
    private List<DataPermissionRule> rules = Arrays.asList(new DataPermissionRule01(),
            new DataPermissionRule02());

    @BeforeEach
    public void setUp() {
        DataPermissionContextHolder.clear();
    }

    @Test
    public void getDataPermissionRule_noAnnotationReturnsAllRules() {
        // prepare parameters
        String mappedStatementId = randomString();

        // invoke
        List<DataPermissionRule> result = dataPermissionRuleFactory.getDataPermissionRule(mappedStatementId);
        // assert
        assertSame(rules, result);
    }

    @Test
    public void getDataPermissionRule_disabledReturnsEmpty() {
        // prepare parameters
        String mappedStatementId = randomString();
        // mock the method
        DataPermissionContextHolder.add(AnnotationUtils.findAnnotation(TestClass03.class, DataPermission.class));

        // invoke
        List<DataPermissionRule> result = dataPermissionRuleFactory.getDataPermissionRule(mappedStatementId);
        // assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void getDataPermissionRule_includeRulesFilters() {
        // prepare parameters
        String mappedStatementId = randomString();
        // mock the method
        DataPermissionContextHolder.add(AnnotationUtils.findAnnotation(TestClass04.class, DataPermission.class));

        // invoke
        List<DataPermissionRule> result = dataPermissionRuleFactory.getDataPermissionRule(mappedStatementId);
        // assert
        assertEquals(1, result.size());
        assertEquals(DataPermissionRule01.class, result.get(0).getClass());
    }

    @Test
    public void getDataPermissionRule_excludeRulesFilters() {
        // prepare parameters
        String mappedStatementId = randomString();
        // mock the method
        DataPermissionContextHolder.add(AnnotationUtils.findAnnotation(TestClass05.class, DataPermission.class));

        // invoke
        List<DataPermissionRule> result = dataPermissionRuleFactory.getDataPermissionRule(mappedStatementId);
        // assert
        assertEquals(1, result.size());
        assertEquals(DataPermissionRule02.class, result.get(0).getClass());
    }

    @Test
    public void getDataPermissionRule_defaultAnnotationReturnsAllRules() {
        // prepare parameters
        String mappedStatementId = randomString();
        // mock the method
        DataPermissionContextHolder.add(AnnotationUtils.findAnnotation(TestClass06.class, DataPermission.class));

        // invoke
        List<DataPermissionRule> result = dataPermissionRuleFactory.getDataPermissionRule(mappedStatementId);
        // assert
        assertSame(rules, result);
    }

    @DataPermission(enable = false)
    static class TestClass03 {}

    @DataPermission(includeRules = DataPermissionRule01.class)
    static class TestClass04 {}

    @DataPermission(excludeRules = DataPermissionRule01.class)
    static class TestClass05 {}

    @DataPermission
    static class TestClass06 {}

    static class DataPermissionRule01 implements DataPermissionRule {

        @Override
        public Set<String> getTableNames() {
            return null;
        }

        @Override
        public Expression getExpression(String tableName, Alias tableAlias) {
            return null;
        }

    }

    static class DataPermissionRule02 implements DataPermissionRule {

        @Override
        public Set<String> getTableNames() {
            return null;
        }

        @Override
        public Expression getExpression(String tableName, Alias tableAlias) {
            return null;
        }

    }

}
