package com.focela.platform.ip.core.utils;

import com.focela.platform.ip.core.Area;
import org.junit.jupiter.api.Test;
import org.lionsoul.ip2region.xdb.Searcher;


import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link IpUtils}.
 */
public class IpUtilsTest {

    @Test
    public void testGetAreaId_string() {
        // 120.202.4.0|120.202.4.255|420600
        Integer areaId = IpUtils.getAreaId("120.202.4.50");
        assertEquals(420600, areaId);
    }

    @Test
    public void testGetAreaId_long() throws Exception {
        // 120.203.123.0|120.203.133.255|360900
        long ip = Searcher.checkIP("120.203.123.250");
        Integer areaId = IpUtils.getAreaId(ip);
        assertEquals(360900, areaId);
    }

    @Test
    public void testGetArea_string() {
        // 120.202.4.0|120.202.4.255|420600
        Area area = IpUtils.getArea("120.202.4.50");
        assertEquals("襄阳市", area.getName());
    }

    @Test
    public void testGetArea_long() throws Exception {
        // 120.203.123.0|120.203.133.255|360900
        long ip = Searcher.checkIP("120.203.123.252");
        Area area = IpUtils.getArea(ip);
        assertEquals("宜春市", area.getName());
    }

}
