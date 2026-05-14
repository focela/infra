package com.focela.platform.framework.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Sorting field DTO.
 *
 * The "ing" suffix avoids clashing with ES SortField.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortingField implements Serializable {

    /**
     * Order - ascending
     */
    public static final String ORDER_ASC = "asc";
    /**
     * Order - descending
     */
    public static final String ORDER_DESC = "desc";

    /**
     * Field name
     */
    private String field;
    /**
     * Order
     */
    private String order;

}
