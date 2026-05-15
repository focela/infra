package com.focela.platform.ip.core;

import com.focela.platform.ip.core.enums.AreaTypeEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Area node, including country, province, city, district, and so on.
 *
 * See resources/area.csv for the underlying data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"parent"}) // See https://gitee.com/yudaocode/yudao-cloud-mini/pulls/2 for the rationale
public class Area {

    /**
     * ID - Global, i.e. the root node
     */
    public static final Integer ID_GLOBAL = 0;
    /**
     * ID - China
     */
    public static final Integer ID_CHINA = 1;

    /**
     * ID
     */
    private Integer id;
    /**
     * Name
     */
    private String name;
    /**
     * Type
     *
     * See enum {@link AreaTypeEnum}
     */
    private Integer type;

    /**
     * Parent node
     */
    @JsonManagedReference
    private Area parent;
    /**
     * Child nodes
     */
    @JsonBackReference
    private List<Area> children;

}
