package com.focela.platform.infra.config.file.client.sftp;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import com.focela.platform.infra.config.file.client.sftp.SftpFileClient;
import com.focela.platform.infra.config.file.client.sftp.SftpFileClientConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * {@link SftpFileClient} manual integration test.
 */
@Tag("manual")
public class SftpFileClientManualIntegrationTest {

//    docker run -p 2222:22 -d \
//            -v $(pwd)/sftp-data:/home/foo/upload \
//    atmoz/sftp \
//    foo:pass:1001

    @Test
    @Disabled("Requires a local SFTP server")
    public void sftpFileClient_uploadsFileWhenServerAvailable() {
        // Create client
        SftpFileClientConfig config = new SftpFileClientConfig();
        config.setDomain("http://127.0.0.1:48080");
        config.setBasePath("/upload"); // Note: this is a relative path, not the actual path on Linux!
        config.setHost("127.0.0.1");
        config.setPort(2222);
        config.setUsername("foo");
        config.setPassword("pass");
        SftpFileClient client = new SftpFileClient(0L, config);
        client.init();
        // Upload file
        String path = IdUtil.fastSimpleUUID() + ".jpg";
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        String fullPath = client.upload(content, path, "image/jpeg");
        System.out.println("Access URL: " + fullPath);
        if (false) {
            byte[] bytes = client.getContent(path);
            System.out.println("File content: " + bytes);
        }
        if (false) {
            client.delete(path);
        }
    }

}
