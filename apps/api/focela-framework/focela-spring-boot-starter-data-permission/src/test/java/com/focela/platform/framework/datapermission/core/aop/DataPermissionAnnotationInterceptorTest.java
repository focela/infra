package com.focela.platform.framework.datapermission.core.aop;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.datapermission.core.annotation.DataPermission;
import com.focela.platform.framework.test.core.support.BaseMockitoUnitTest;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DataPermissionAnnotationInterceptor}.
 */
public class DataPermissionAnnotationInterceptorTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DataPermissionAnnotationInterceptor interceptor;

    @Mock
    private MethodInvocation methodInvocation;

    @BeforeEach
    public void setUp() {
        interceptor.getDataPermissionCache().clear();
    }

    @Test // No @DataPermission annotation
    public void testInvoke_none() throws Throwable {
        // prepare parameters
        mockMethodInvocation(TestNone.class);

        // invoke
        Object result = interceptor.invoke(methodInvocation);
        // assert
        assertEquals("none", result);
        assertEquals(1, interceptor.getDataPermissionCache().size());
        assertTrue(CollUtil.getFirst(interceptor.getDataPermissionCache().values()).enable());
    }

    @Test // @DataPermission annotation on the method
    public void testInvoke_method() throws Throwable {
        // prepare parameters
        mockMethodInvocation(TestMethod.class);

        // invoke
        Object result = interceptor.invoke(methodInvocation);
        // assert
        assertEquals("method", result);
        assertEquals(1, interceptor.getDataPermissionCache().size());
        assertFalse(CollUtil.getFirst(interceptor.getDataPermissionCache().values()).enable());
    }

    @Test // @DataPermission annotation on the class
    public void testInvoke_class() throws Throwable {
        // prepare parameters
        mockMethodInvocation(TestClass.class);

        // invoke
        Object result = interceptor.invoke(methodInvocation);
        // assert
        assertEquals("class", result);
        assertEquals(1, interceptor.getDataPermissionCache().size());
        assertFalse(CollUtil.getFirst(interceptor.getDataPermissionCache().values()).enable());
    }

    private void mockMethodInvocation(Class<?> clazz) throws Throwable {
        Object targetObject = clazz.newInstance();
        Method method = targetObject.getClass().getMethod("echo");
        when(methodInvocation.getThis()).thenReturn(targetObject);
        when(methodInvocation.getMethod()).thenReturn(method);
        when(methodInvocation.proceed()).then(invocationOnMock -> method.invoke(targetObject));
    }

    static class TestMethod {

        @DataPermission(enable = false)
        public String echo() {
            return "method";
        }

    }

    @DataPermission(enable = false)
    static class TestClass {

        public String echo() {
            return "class";
        }

    }

    static class TestNone {

        public String echo() {
            return "none";
        }

    }

}
