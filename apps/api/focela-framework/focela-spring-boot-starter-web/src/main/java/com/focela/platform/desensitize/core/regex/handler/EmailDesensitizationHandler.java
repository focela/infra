package com.focela.platform.desensitize.core.regex.handler;

import com.focela.platform.desensitize.core.regex.annotation.EmailDesensitize;

/**
 * Desensitization handler for {@link EmailDesensitize}
 */
public class EmailDesensitizationHandler extends AbstractRegexDesensitizationHandler<EmailDesensitize> {

    @Override
    String getRegex(EmailDesensitize annotation) {
        return annotation.regex();
    }

    @Override
    String getReplacer(EmailDesensitize annotation) {
        return annotation.replacer();
    }

}
