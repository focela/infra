package com.focela.platform.infra.config.file.client.s3;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import com.focela.platform.common.utils.validation.ValidationUtils;
import com.focela.platform.infra.config.file.client.s3.S3FileClient;
import com.focela.platform.infra.config.file.client.s3.S3FileClientConfig;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("resource")
public class S3FileClientTest {

    @Test
    @Disabled // MinIO; comment out this line to run as an integration test
    public void testMinIO() throws Exception {
        S3FileClientConfig config = new S3FileClientConfig();
        // Configure with your own values
        config.setAccessKey("admin");
        config.setAccessSecret("password");
        config.setBucket("focela-bucket");
        config.setDomain(null);
        // Default 9000 endpoint
        config.setEndpoint("http://127.0.0.1:9000");

        // Run upload
        testExecuteUpload(config);
    }

    @Test
    @Disabled // Aliyun OSS; comment out this line to run as an integration test
    public void testAliyun() throws Exception {
        S3FileClientConfig config = new S3FileClientConfig();
        // Configure with your own values
        config.setAccessKey(System.getenv("ALIYUN_ACCESS_KEY"));
        config.setAccessSecret(System.getenv("ALIYUN_SECRET_KEY"));
        config.setBucket("focela-aliyun-bucket");
        config.setDomain(null); // Optional custom domain, e.g. http://ali-oss.example.com
        // Default Beijing endpoint
        config.setEndpoint("oss-cn-beijing.aliyuncs.com");

        // Run upload
        testExecuteUpload(config);
    }

    @Test
    @Disabled // Tencent Cloud COS; comment out this line to run as an integration test
    public void testQCloud() throws Exception {
        S3FileClientConfig config = new S3FileClientConfig();
        // Configure with your own values
        config.setAccessKey(System.getenv("QCLOUD_ACCESS_KEY"));
        config.setAccessSecret(System.getenv("QCLOUD_SECRET_KEY"));
        config.setBucket("focela-qcloud-1255880240");
        config.setDomain(null); // Optional custom domain, e.g. http://tencent-oss.example.com
        // Default Shanghai endpoint
        config.setEndpoint("cos.ap-shanghai.myqcloud.com");

        // Run upload
        testExecuteUpload(config);
    }

    @Test
    @Disabled // Qiniu Cloud Storage; comment out this line to run as an integration test
    public void testQiniu() throws Exception {
        S3FileClientConfig config = new S3FileClientConfig();
        // Configure with your own values
//        config.setAccessKey(System.getenv("QINIU_ACCESS_KEY"));
//        config.setAccessSecret(System.getenv("QINIU_SECRET_KEY"));
        config.setAccessKey("b7yvuhBSAGjmtPhMFcn9iMOxUOY_I06cA_p0ZUx8");
        config.setAccessSecret("kXM1l5ia1RvSX3QaOEcwI3RLz3Y2rmNszWonKZtP");
        config.setBucket("focela-qiniu");
        config.setDomain("https://www.example.com"); // Optional custom domain, e.g. https://www.example.com
        config.setEnablePathStyleAccess(false);
        // Default Shanghai endpoint
        config.setEndpoint("s3-cn-south-1.qiniucs.com");

        // Run upload
        testExecuteUpload(config);
    }

    @Test
    @Disabled // Qiniu Cloud Storage (read private bucket); comment out this line to run as an integration test
    public void testQiniu_privateGet() {
        S3FileClientConfig config = new S3FileClientConfig();
        // Configure with your own values
//        config.setAccessKey(System.getenv("QINIU_ACCESS_KEY"));
//        config.setAccessSecret(System.getenv("QINIU_SECRET_KEY"));
        config.setAccessKey("b7yvuhBSAGjmtPhMFcn9iMOxUOY_I06cA_p0ZUx8");
        config.setAccessSecret("kXM1l5ia1RvSX3QaOEcwI3RLz3Y2rmNszWonKZtP");
        config.setBucket("focela-qiniu-private");
        config.setDomain("http://t151glocd.hn-bkt.clouddn.com"); // Optional custom domain, e.g. https://www.example.com
        config.setEnablePathStyleAccess(false);
        // Default Shanghai endpoint
        config.setEndpoint("s3-cn-south-1.qiniucs.com");

        // Validate config
        ValidationUtils.validate(Validation.buildDefaultValidatorFactory().getValidator(), config);
        // Create Client
        S3FileClient client = new S3FileClient(0L, config);
        client.init();
        // Generate presigned URL
        String path = "output.png";
        String presignedUrl = client.presignGetUrl(path, 300);
        System.out.println(presignedUrl);
    }

    @Test
    @Disabled // Huawei Cloud Storage; comment out this line to run as an integration test
    public void testHuaweiCloud() throws Exception {
        S3FileClientConfig config = new S3FileClientConfig();
        // Configure with your own values
//        config.setAccessKey(System.getenv("HUAWEI_CLOUD_ACCESS_KEY"));
//        config.setAccessSecret(System.getenv("HUAWEI_CLOUD_SECRET_KEY"));
        config.setBucket("focela");
        config.setDomain(null); // Optional custom domain
        // Default Shanghai endpoint
        config.setEndpoint("obs.cn-east-3.myhuaweicloud.com");

        // Run upload
        testExecuteUpload(config);
    }

    private void testExecuteUpload(S3FileClientConfig config) {
        // Validate config
        ValidationUtils.validate(Validation.buildDefaultValidatorFactory().getValidator(), config);
        // Create Client
        S3FileClient client = new S3FileClient(0L, config);
        client.init();
        // Upload file
        String path = IdUtil.fastSimpleUUID() + ".jpg";
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        String fullPath = client.upload(content, path, "image/jpeg");
        System.out.println("Access URL: " + fullPath);
        // Read file
        if (true) {
            byte[] bytes = client.getContent(path);
            System.out.println("File content: " + bytes.length);
        }
        // Delete file
        if (false) {
            client.delete(path);
        }
    }

}
