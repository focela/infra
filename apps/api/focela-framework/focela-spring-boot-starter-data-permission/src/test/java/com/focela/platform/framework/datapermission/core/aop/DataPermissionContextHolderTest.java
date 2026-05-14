package com.focela.platform.framework.datapermission.core.aop;

import com.focela.platform.framework.datapermission.core.annotation.DataPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link DataPermissionContextHolder}.
 */
class DataPermissionContextHolderTest {

    @BeforeEach
    public void setUp() {
        DataPermissionContextHolder.clear();
    }

    @Test
    public void testGet() {
        // mock the method
        DataPermission dataPermission01 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission01);
        DataPermission dataPermission02 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission02);

        // invoke
        DataPermission result = DataPermissionContextHolder.get();
        // assert
        assertSame(result, dataPermission02);
    }

    @Test
    public void testPush() {
        // invoke
        DataPermission dataPermission01 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission01);
        DataPermission dataPermission02 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission02);
        // assert
        DataPermission first = DataPermissionContextHolder.getAll().get(0);
        DataPermission second = DataPermissionContextHolder.getAll().get(1);
        assertSame(dataPermission01, first);
        assertSame(dataPermission02, second);
    }

    @Test
    public void testRemove() {
        // mock the method
        DataPermission dataPermission01 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission01);
        DataPermission dataPermission02 = mock(DataPermission.class);
        DataPermissionContextHolder.add(dataPermission02);

        // invoke
        DataPermission result = DataPermissionContextHolder.remove();
        // assert
        assertSame(result, dataPermission02);
        assertEquals(1, DataPermissionContextHolder.getAll().size());
    }

}
