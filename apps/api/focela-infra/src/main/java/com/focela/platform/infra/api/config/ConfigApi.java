package com.focela.platform.infra.api.config;

/**
 * Param config API interface
 */
public interface ConfigApi {


    /**
     * Query the param value by param key.
     *
     * @param key param key
     * @return param value
     */
    String getConfigValueByKey(String key);

}
