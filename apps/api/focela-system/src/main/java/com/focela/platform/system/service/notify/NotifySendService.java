package com.focela.platform.system.service.notify;

import java.util.List;
import java.util.Map;

/**
 * In-site notification send Service interface
 */
public interface NotifySendService {

    /**
     * Send a single in-site notification to a backoffice admin user.
     *
     * When mobile is empty, use userId to load the admin's mobile number.
     *
     * @param userId user ID
     * @param templateCode SMS template code
     * @param templateParams SMS template parameters
     * @return send log ID
     */
    Long sendSingleNotifyToAdmin(Long userId,
                                 String templateCode, Map<String, Object> templateParams);
    /**
     * Send a single in-site notification to a user APP user.
     *
     * When mobile is empty, use userId to load the member's mobile number.
     *
     * @param userId user ID
     * @param templateCode notification template code
     * @param templateParams notification template parameters
     * @return send log ID
     */
    Long sendSingleNotifyToMember(Long userId,
                                  String templateCode, Map<String, Object> templateParams);

    /**
     * Send a single in-site notification to a user
     *
     * @param userId user ID
     * @param userType user type
     * @param templateCode notification template code
     * @param templateParams notification template parameters
     * @return send log ID
     */
    Long sendSingleNotify( Long userId, Integer userType,
                           String templateCode, Map<String, Object> templateParams);

    default void sendBatchNotify(List<String> mobiles, List<Long> userIds, Integer userType,
                                 String templateCode, Map<String, Object> templateParams) {
        throw new UnsupportedOperationException("temporarily not supported this operation, if interested can implement this feature!");
    }

}
