package com.focela.platform.framework.dictionary.config;

import com.focela.platform.framework.common.contract.system.dictionary.DictionaryDataCommonApi;
import com.focela.platform.framework.dictionary.core.DictionaryFrameworkUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FocelaDictionaryAutoConfiguration {

    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public DictionaryFrameworkUtils dictUtils(DictionaryDataCommonApi dictDataApi) {
        DictionaryFrameworkUtils.init(dictDataApi);
        return new DictionaryFrameworkUtils();
    }

}
