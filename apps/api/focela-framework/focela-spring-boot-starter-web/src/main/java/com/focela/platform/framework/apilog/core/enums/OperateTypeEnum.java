package com.focela.platform.framework.apilog.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Operation type of the operate log
 */
@Getter
@AllArgsConstructor
public enum OperateTypeEnum {

    /**
     * Query
     */
    GET(1),
    /**
     * Create
     */
    CREATE(2),
    /**
     * Update
     */
    UPDATE(3),
    /**
     * Delete
     */
    DELETE(4),
    /**
     * Export
     */
    EXPORT(5),
    /**
     * Import
     */
    IMPORT(6),
    /**
     * Other
     *
     * Choose Other when it cannot be categorized. The operation name can further identify it
     */
    OTHER(0);

    /**
     * Type
     */
    private final Integer type;

}
