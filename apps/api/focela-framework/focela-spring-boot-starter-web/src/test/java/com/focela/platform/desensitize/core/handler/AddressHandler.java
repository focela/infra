package com.focela.platform.desensitize.core.handler;

import com.focela.platform.desensitize.core.DesensitizeTest;
import com.focela.platform.desensitize.core.base.handler.DesensitizationHandler;
import com.focela.platform.desensitize.core.annotation.Address;

/**
 * Desensitization handler for {@link Address}
 *
 * Used by {@link DesensitizeTest} tests
 */
public class AddressHandler implements DesensitizationHandler<Address> {

    @Override
    public String desensitize(String origin, Address annotation) {
        return origin + annotation.replacer();
    }

}
