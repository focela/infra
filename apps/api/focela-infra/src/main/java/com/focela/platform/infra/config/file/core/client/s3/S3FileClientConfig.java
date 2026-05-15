package com.focela.platform.infra.config.file.core.client.s3;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.infra.config.file.core.client.FileClientConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * S3 file client config class
 */
@Data
public class S3FileClientConfig implements FileClientConfig {

    public static final String ENDPOINT_QINIU = "qiniucs.com";
    public static final String ENDPOINT_ALIYUN = "aliyuncs.com";
    public static final String ENDPOINT_TENCENT = "myqcloud.com";
    public static final String ENDPOINT_VOLCES = "volces.com"; // Volcengine (ByteDance)

    /**
     * Endpoint address
     * 1. MinIO: https://www.example.com/Spring-Boot/MinIO . For example, http://127.0.0.1:9000
     * 2. Aliyun: https://help.aliyun.com/document_detail/31837.html
     * 3. Tencent Cloud: https://cloud.tencent.com/document/product/436/6224
     * 4. Qiniu: https://developer.qiniu.com/kodo/4088/s3-access-domainname
     * 5. Huawei Cloud: https://console.huaweicloud.com/apiexplorer/#/endpoint/OBS
     * 6. Volcengine: https://www.volcengine.com/docs/6349/107356
     */
    @NotNull(message = "endpoint must not be blank")
    private String endpoint;
    /**
     * Custom domain
     * 1. MinIO: configure via Nginx
     * 2. Aliyun: https://help.aliyun.com/document_detail/31836.html
     * 3. Tencent Cloud: https://cloud.tencent.com/document/product/436/11142
     * 4. Qiniu: https://developer.qiniu.com/kodo/8556/set-the-custom-source-domain-name
     * 5. Huawei Cloud: https://support.huaweicloud.com/usermanual-obs/obs_03_0032.html
     * 6. Volcengine: https://www.volcengine.com/docs/6349/128983
     */
    @URL(message = "domain must be URL format")
    private String domain;
    /**
     * Storage bucket
     */
    @NotNull(message = "bucket must not be blank")
    private String bucket;

    /**
     * Access Key
     * 1. MinIO: https://www.example.com/Spring-Boot/MinIO
     * 2. Aliyun: https://ram.console.aliyun.com/manage/ak
     * 3. Tencent Cloud: https://console.cloud.tencent.com/cam/capi
     * 4. Qiniu: https://portal.qiniu.com/user/key
     * 5. Huawei Cloud: https://support.huaweicloud.com/qs-obs/obs_qs_0005.html
     * 6. Volcengine: https://console.volcengine.com/iam/keymanage/
     */
    @NotNull(message = "accessKey must not be blank")
    private String accessKey;
    /**
     * Access Secret
     */
    @NotNull(message = "accessSecret must not be blank")
    private String accessSecret;

    /**
     * Whether to enable PathStyle access
     */
    @NotNull(message = "enablePathStyleAccess must not be blank")
    private Boolean enablePathStyleAccess;

    /**
     * Whether public access is enabled
     *
     * true: public access, anyone can access
     * false: private access, only the configured accessKey can access
     */
    @NotNull(message = "enablePublicAccess must not be blank")
    private Boolean enablePublicAccess;

    /**
     * Region
     * 1. AWS S3: https://docs.aws.amazon.com/general/latest/gr/s3.html — e.g. us-east-1, us-west-2
     * 2. MinIO: any value works, typically us-east-1
     * 3. Aliyun: not required, auto-detected
     * 4. Tencent Cloud: not required, auto-detected
     * 5. Qiniu: not required, auto-detected
     * 6. Huawei Cloud: not required, auto-detected
     * 7. Volcengine: not required, auto-detected
     */
    private String region;

    @SuppressWarnings("RedundantIfStatement")
    @AssertTrue(message = "domain must not be blank")
    @JsonIgnore
    public boolean isDomainValid() {
        // For Qiniu, domain must be provided
        if (StrUtil.contains(endpoint, ENDPOINT_QINIU) && StrUtil.isEmpty(domain)) {
            return false;
        }
        return true;
    }

}
