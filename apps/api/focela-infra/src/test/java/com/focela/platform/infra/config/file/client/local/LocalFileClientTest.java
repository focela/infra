package com.focela.platform.infra.config.file.client.local;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import com.focela.platform.infra.config.file.client.local.LocalFileClient;
import com.focela.platform.infra.config.file.client.local.LocalFileClientConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.focela.platform.test.core.utils.RandomUtils.randomString;

public class LocalFileClientTest {

    @Test
    @Disabled
    public void test() {
        // Create client
        LocalFileClientConfig config = new LocalFileClientConfig();
        config.setDomain("http://127.0.0.1:48080");
        config.setBasePath("/Users/focela/file_test");
        LocalFileClient client = new LocalFileClient(0L, config);
        client.init();
        // Upload file
        String path = IdUtil.fastSimpleUUID() + ".jpg";
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        String fullPath = client.upload(content, path, "image/jpeg");
        System.out.println("Access URL: " + fullPath);
        client.delete(path);
    }

    @Test
    @Disabled
    public void getContent_missing() {
        // Create client
        LocalFileClientConfig config = new LocalFileClientConfig();
        config.setDomain("http://127.0.0.1:48080");
        config.setBasePath("/Users/focela/file_test");
        LocalFileClient client = new LocalFileClient(0L, config);
        client.init();
        // Upload file
        byte[] content = client.getContent(randomString());
        System.out.println();
    }

}
