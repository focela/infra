package com.focela.platform.datasource.core.enums;

/**
 * Corresponds to different data source configurations in a multi-datasource setup.
 *
 * Use the {@link com.baomidou.dynamic.datasource.annotation.DS} annotation on a method to set the data source.
 * Note that the default is the {@link #MASTER} data source.
 *
 * Official documentation: http://dynamic-datasource.com/guide/customize/Annotation.html
 */
public interface DataSourceEnum {

    /**
     * Master database, recommended to use the {@link com.baomidou.dynamic.datasource.annotation.Master} annotation
     */
    String MASTER = "master";
    /**
     * Slave database, recommended to use the {@link com.baomidou.dynamic.datasource.annotation.Slave} annotation
     */
    String SLAVE = "slave";

}
