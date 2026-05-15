package com.focela.platform.dictionary.config;

import com.focela.platform.common.api.system.dictionary.DictionaryDataContractApi;
import com.focela.platform.dictionary.core.DictionaryFrameworkUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FocelaDictionaryAutoConfiguration {

    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public DictionaryFrameworkUtils dictUtils(DictionaryDataContractApi dictDataApi) {
        DictionaryFrameworkUtils.init(dictDataApi);
        return new DictionaryFrameworkUtils();
    }

}
