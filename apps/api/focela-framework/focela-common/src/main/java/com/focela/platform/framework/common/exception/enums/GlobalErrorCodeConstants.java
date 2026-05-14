package com.focela.platform.framework.common.exception.enums;

import com.focela.platform.framework.common.exception.ErrorCode;

/**
 * Global error code constants.
 * Codes 0-999 are reserved for system-level errors.
 *
 * In general, HTTP response status codes are used (https://developer.mozilla.org/en-US/docs/Web/HTTP/Status).
 * HTTP status codes are not very expressive for business semantics, but they work well at the system level.
 * Note: because 0 has historically been used to indicate success, this codebase uses 0 instead of 200.
 */
public interface GlobalErrorCodeConstants {

    ErrorCode SUCCESS = new ErrorCode(0, "Success");

    // ========== Client error range ==========

    ErrorCode BAD_REQUEST = new ErrorCode(400, "Invalid request parameters");
    ErrorCode UNAUTHORIZED = new ErrorCode(401, "Account not logged in");
    ErrorCode FORBIDDEN = new ErrorCode(403, "No permission for this operation");
    ErrorCode NOT_FOUND = new ErrorCode(404, "Request not found");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode(405, "HTTP method not allowed");
    ErrorCode LOCKED = new ErrorCode(423, "Request failed, please try again later"); // concurrent request not allowed
    ErrorCode TOO_MANY_REQUESTS = new ErrorCode(429, "Too many requests, please try again later");

    // ========== Server error range ==========

    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "System error");
    ErrorCode NOT_IMPLEMENTED = new ErrorCode(501, "Feature not implemented or not enabled");
    ErrorCode ERROR_CONFIGURATION = new ErrorCode(502, "Invalid configuration");

    // ========== Custom error range ==========
    ErrorCode REPEATED_REQUESTS = new ErrorCode(900, "Duplicate request, please try again later"); // duplicate request
    ErrorCode DEMO_DENY = new ErrorCode(901, "Demo mode: write operations are disabled");

    ErrorCode UNKNOWN = new ErrorCode(999, "Unknown error");

}
