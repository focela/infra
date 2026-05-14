package com.focela.platform.framework.desensitize.core.regex.handler;

import com.focela.platform.framework.desensitize.core.regex.annotation.RegexDesensitize;

/**
 * Regex desensitization handler for {@link RegexDesensitize}
 */
public class DefaultRegexDesensitizationHandler extends AbstractRegexDesensitizationHandler<RegexDesensitize> {

    @Override
    String getRegex(RegexDesensitize annotation) {
        return annotation.regex();
    }

    @Override
    String getReplacer(RegexDesensitize annotation) {
        return annotation.replacer();
    }

    @Override
    public String getDisable(RegexDesensitize annotation) {
        return annotation.disable();
    }

}
