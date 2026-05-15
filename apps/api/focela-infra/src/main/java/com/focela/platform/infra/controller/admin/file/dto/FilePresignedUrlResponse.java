package com.focela.platform.infra.controller.admin.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Admin - file presigned URL Response VO")
@Data
public class FilePresignedUrlResponse {

    @Schema(description = "Config ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    @Schema(description = "file upload URL", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "https://s3.cn-south-1.qiniucs.com/ruoyi-vue-pro/758d3a5387507358c7236de4c8f96de1c7f5097ff6a7722b34772fb7b76b140f.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=3TvrJ70gl2Gt6IBe7_IZT1F6i_k0iMuRtyEv4EyS%2F20240217%2Fcn-south-1%2Fs3%2Faws4_request&X-Amz-Date=20240217T123222Z&X-Amz-Expires=600&X-Amz-SignedHeaders=host&X-Amz-Signature=a29f33770ab79bf523ccd4034d0752ac545f3c2a3b17baa1eb4e280cfdccfda5")
    private String uploadUrl;

    /**
     * Why return the url field?
     *
     * After the frontend uploads the file, it needs to use this URL to access the file.
     */
    @Schema(description = "file access URL", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "https://www.example.com/758d3a5387507358c7236de4c8f96de1c7f5097ff6a7722b34772fb7b76b140f.png")
    private String url;

    /**
     * Why return the path field?
     *
     * After the frontend uploads the file, it needs to call createFile to record the path.
     */
    @Schema(description = "File path", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx.png")
    private String path;

}
