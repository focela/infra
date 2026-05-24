package com.focela.platform.infra.config.file.client.ftp;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.ftp.FtpMode;
import com.focela.platform.infra.config.file.client.ftp.FtpFileClient;
import com.focela.platform.infra.config.file.client.ftp.FtpFileClientConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * {@link FtpFileClient} manual integration test.
 */
@Tag("manual")
public class FtpFileClientManualIntegrationTest {

//    docker run -d \
//            -p 2121:21 -p 30000-30009:30000-30009 \
//            -e FTP_USER=foo \
//            -e FTP_PASS=pass \
//            -e PASV_ADDRESS=127.0.0.1 \
//            -e PASV_MIN_PORT=30000 \
//            -e PASV_MAX_PORT=30009 \
//            -v $(pwd)/ftp-data:/home/vsftpd \
//    fauria/vsftpd

    @Test
    @Disabled("Requires a local FTP server")
    public void ftpFileClient_uploadsFileWhenServerAvailable() {
        // Create client
        FtpFileClientConfig config = new FtpFileClientConfig();
        config.setDomain("http://127.0.0.1:48080");
        config.setBasePath("/home/ftp");
        config.setHost("127.0.0.1");
        config.setPort(2121);
        config.setUsername("foo");
        config.setPassword("pass");
        config.setMode(FtpMode.Passive.name());
        FtpFileClient client = new FtpFileClient(0L, config);
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
