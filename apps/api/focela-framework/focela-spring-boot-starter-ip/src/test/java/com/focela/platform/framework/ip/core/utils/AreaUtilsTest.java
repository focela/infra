package com.focela.platform.framework.ip.core.utils;


import com.focela.platform.framework.ip.core.Area;
import com.focela.platform.framework.ip.core.enums.AreaTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link AreaUtils}.
 */
public class AreaUtilsTest {

    @Test
    public void testGetArea() {
        // Invoke: Beijing
        Area area = AreaUtils.getArea(110100);
        // Assert
        assertEquals(area.getId(), 110100);
        assertEquals(area.getName(), "北京市");
        assertEquals(area.getType(), AreaTypeEnum.CITY.getType());
        assertEquals(area.getParent().getId(), 110000);
        assertEquals(area.getChildren().size(), 16);
    }

    @Test
    public void testFormat() {
        assertEquals(AreaUtils.format(110105), "北京市 北京市 朝阳区");
        assertEquals(AreaUtils.format(1), "中国");
        assertEquals(AreaUtils.format(2), "蒙古");
    }

}
