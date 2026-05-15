package com.focela.platform.ip.core.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import com.focela.platform.ip.core.Area;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

/**
 * IP utility class.
 *
 * The IP data is the lite version of ip2region.xdb, based on the <a href="https://gitee.com/zhijiantianya/ip2region"/> project.
 */
@Slf4j
@UtilityClass
public class IPUtils {

    /**
     * IP searcher, loaded into memory at startup.
     */
    private static Searcher SEARCHER;

    static {
        init();
    }

    /**
     * Initialize.
     */
    private static void init() {
        try {
            long now = System.currentTimeMillis();
            byte[] bytes = ResourceUtil.readBytes("ip2region.xdb");
            SEARCHER = Searcher.newWithBuffer(bytes);
            log.info("init load IPUtils success, elapsed ({}) millisecond", System.currentTimeMillis() - now);
        } catch (Exception e) {
            throw new RuntimeException("IPUtils init failed", e);
        }
    }

    /**
     * Look up the area ID for the given IP.
     *
     * @param ip IP address, e.g. 127.0.0.1
     * @return area ID
     */
    @SneakyThrows
    public static Integer getAreaId(String ip) {
        return Integer.parseInt(SEARCHER.search(ip.trim()));
    }

    /**
     * Look up the area ID for the given IP.
     *
     * @param ip IP address as a long, see the return of {@link Searcher#checkIP(String)}
     * @return area ID
     */
    @SneakyThrows
    public static Integer getAreaId(long ip) {
        return Integer.parseInt(SEARCHER.search(ip));
    }

    /**
     * Look up the area for the given IP.
     *
     * @param ip IP address, e.g. 127.0.0.1
     * @return area
     */
    public static Area getArea(String ip) {
        return AreaUtils.getArea(getAreaId(ip));
    }

    /**
     * Look up the area for the given IP.
     *
     * @param ip IP address as a long, see the return of {@link Searcher#checkIP(String)}
     * @return area
     */
    public static Area getArea(long ip) {
        return AreaUtils.getArea(getAreaId(ip));
    }
}
