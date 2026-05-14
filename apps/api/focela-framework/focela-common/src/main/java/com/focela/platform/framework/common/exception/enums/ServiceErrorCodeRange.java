package com.focela.platform.framework.common.exception.enums;

/**
 * Business exception error code ranges. Declared (not actually used) to keep each module's error codes from colliding.
 *
 * Codes are 10 digits split into four segments.
 *
 * Segment 1 (1 digit): type
 *      1 - business-level exception
 *      x - reserved
 * Segment 2 (3 digits): system
 *      001 - user system
 *      002 - product system
 *      003 - order system
 *      004 - payment system
 *      005 - coupon system
 *      ... - ...
 * Segment 3 (3 digits): module
 *      No fixed rule.
 *      Typically each system has several modules. For example, the user system contains:
 *          001 - OAuth2 module
 *          002 - User module
 *          003 - MobileCode module
 * Segment 4 (3 digits): error code
 *       No fixed rule.
 *       Typically auto-incremented within each module.
 */
public class ServiceErrorCodeRange {

    // module infra error code range [1-001-000-000 ~ 1-002-000-000)
    // module system error code range [1-002-000-000 ~ 1-003-000-000)
    // module report error code range [1-003-000-000 ~ 1-004-000-000)
    // module member error code range [1-004-000-000 ~ 1-005-000-000)
    // module mp error code range [1-006-000-000 ~ 1-007-000-000)
    // module pay error code range [1-007-000-000 ~ 1-008-000-000)
    // module bpm error code range [1-009-000-000 ~ 1-010-000-000)

    // module product error code range [1-008-000-000 ~ 1-009-000-000)
    // module trade error code range [1-011-000-000 ~ 1-012-000-000)
    // module promotion error code range [1-013-000-000 ~ 1-014-000-000)

    // module crm error code range [1-020-000-000 ~ 1-021-000-000)

    // module ai error code range [1-022-000-000 ~ 1-023-000-000)

}
