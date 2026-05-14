package com.focela.platform.framework.ip.core.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.focela.platform.framework.common.utils.object.ObjectUtils;
import com.focela.platform.framework.ip.core.Area;
import com.focela.platform.framework.ip.core.enums.AreaTypeEnum;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.focela.platform.framework.common.utils.collection.CollectionUtils.convertList;
import static com.focela.platform.framework.common.utils.collection.CollectionUtils.findFirst;

/**
 * Area utility class.
 */
@Slf4j
@UtilityClass
public class AreaUtils {

    /**
     * In-memory Area cache to speed up access.
     */
    private static Map<Integer, Area> areas;

    static {
        init();
    }

    /**
     * Initialize.
     */
    private static void init() {
        try {
            long now = System.currentTimeMillis();
            areas = new HashMap<>();
            areas.put(Area.ID_GLOBAL, new Area(Area.ID_GLOBAL, "Global", 0, null, new ArrayList<>()));
            // Load data from csv
            List<CsvRow> rows = CsvUtil.getReader().read(ResourceUtil.getUtf8Reader("area.csv")).getRows();
            rows.remove(0); // remove header
            for (CsvRow row : rows) {
                Area area = new Area(Integer.valueOf(row.get(0)), row.get(1), Integer.valueOf(row.get(2)), null, new ArrayList<>());
                areas.put(area.getId(), area);
            }

            // Build parent-child relationships: Area has no parentId field, so we iterate again
            for (CsvRow row : rows) {
                Area area = areas.get(Integer.valueOf(row.get(0))); // self
                Area parent = areas.get(Integer.valueOf(row.get(3))); // parent
                Assert.isTrue(area != parent, "{}: parent and child nodes are identical", area.getName());
                area.setParent(parent);
                parent.getChildren().add(area);
            }
            log.info("init load AreaUtils success, elapsed ({}) millisecond", System.currentTimeMillis() - now);
        } catch (Exception e) {
            throw new RuntimeException("AreaUtils init failed", e);
        }
    }

    /**
     * Get the area for the given ID.
     *
     * @param id area ID
     * @return area
     */
    public static Area getArea(Integer id) {
        return areas.get(id);
    }

    /**
     * Resolve an area from its full path.
     *
     * @param pathStr area path, e.g. Henan Province/Shijiazhuang City/Xinhua District
     * @return area
     */
    public static Area parseArea(String pathStr) {
        String[] paths = pathStr.split("/");
        Area area = null;
        for (String path : paths) {
            if (area == null) {
                area = findFirst(areas.values(), item -> item.getName().equals(path));
            } else {
                area = findFirst(area.getChildren(), item -> item.getName().equals(path));
            }
        }
        return area;
    }

    /**
     * Get the full path names of all nodes, e.g. Province/City/District.
     *
     * @param areas area tree
     * @return full path names of all nodes
     */
    public static List<String> getAreaNodePathList(List<Area> areas) {
        List<String> paths = new ArrayList<>();
        areas.forEach(area -> getAreaNodePathList(area, "", paths));
        return paths;
    }

    /**
     * Build full path names for every node in a tree, stored in the form "ancestor/parent/child".
     *
     * @param node  parent node
     * @param path  full path name
     * @param paths list of full path names, province/city/district
     */
    private static void getAreaNodePathList(Area node, String path, List<String> paths) {
        if (node == null) {
            return;
        }
        // Build the current node path
        String currentPath = path.isEmpty() ? node.getName() : path + "/" + node.getName();
        paths.add(currentPath);
        // Recursively traverse child nodes
        for (Area child : node.getChildren()) {
            getAreaNodePathList(child, currentPath, paths);
        }
    }

    /**
     * Format the area.
     *
     * @param id area ID
     * @return formatted area
     */
    public static String format(Integer id) {
        return format(id, " ");
    }

    /**
     * Format the area.
     *
     * Examples:
     * 1. id = "Jing'an District": Shanghai Shanghai Jing'an District
     * 2. id = "Shanghai City": Shanghai Shanghai
     * 3. id = "Shanghai": Shanghai
     * 4. id = "United States": United States
     * When the area lies inside China, "China" is omitted by default.
     *
     * @param id        area ID
     * @param separator separator
     * @return formatted area
     */
    public static String format(Integer id, String separator) {
        // Get the area
        Area area = areas.get(id);
        if (area == null) {
            return null;
        }

        // Format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < AreaTypeEnum.values().length; i++) { // avoid infinite loop
            sb.insert(0, area.getName());
            // "Recurse" into the parent
            area = area.getParent();
            if (area == null
                    || ObjectUtils.equalsAny(area.getId(), Area.ID_GLOBAL, Area.ID_CHINA)) { // skip when the parent is China
                break;
            }
            sb.insert(0, separator);
        }
        return sb.toString();
    }

    /**
     * Get the list of areas of the given type.
     *
     * @param type area type
     * @param func conversion function
     * @param <T>  result type
     * @return area list
     */
    public static <T> List<T> getByType(AreaTypeEnum type, Function<Area, T> func) {
        return convertList(areas.values(), func, area -> type.getType().equals(area.getType()));
    }

    /**
     * Get the parent area ID for the given area ID and parent area type.
     *
     * @param id   area ID
     * @param type area type
     * @return parent area ID
     */
    public static Integer getParentIdByType(Integer id, @NonNull AreaTypeEnum type) {
        for (int i = 0; i < Byte.MAX_VALUE; i++) {
            Area area = AreaUtils.getArea(id);
            if (area == null) {
                return null;
            }
            // Case 1: a match is found, return it
            if (type.getType().equals(area.getType())) {
                return area.getId();
            }
            // Case 2: root node reached, return null
            if (area.getParent() == null || area.getParent().getId() == null) {
                return null;
            }
            // Otherwise: keep walking up
            id = area.getParent().getId();
        }
        return null;
    }

}
