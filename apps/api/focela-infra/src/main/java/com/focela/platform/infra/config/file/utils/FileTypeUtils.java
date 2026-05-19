package com.focela.platform.infra.config.file.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.utils.http.HttpUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.IOException;

/**
 * File type Utils
 */
@Slf4j
public class FileTypeUtils {

    private static final Tika TIKA = new Tika();

    /**
     * Get the MIME type of a file. There may be discrepancies for doc, jar and similar files.
     *
     * @param data file content
     * @return MIME type, returns "application/octet-stream" when unable to recognize
     */
    @SneakyThrows
    public static String getMimeType(byte[] data) {
        return TIKA.detect(data);
    }

    /**
     * Get the file type by file name; in some cases more accurate than via byte array, e.g. for jar files name is more accurate
     *
     * @param name file name
     * @return MIME type, returns "application/octet-stream" when unable to recognize
     */
    public static String getMimeType(String name) {
        return TIKA.detect(name);
    }

    /**
     * When both file and data are available, this method is preferred — most accurate
     *
     * @param data file content
     * @param name file name
     * @return MIME type, returns "application/octet-stream" when unable to recognize
     */
    public static String getMimeType(byte[] data, String name) {
        return TIKA.detect(data, name);
    }

    /**
     * Get file extension by MIME type
     *
     * Note: If not found or an exception occurs, returns null
     *
     * @param mimeType type
     * @return extension, e.g. .pdf
     */
    public static String getExtension(String mimeType) {
        try {
            return MimeTypes.getDefaultMimeTypes().forName(mimeType).getExtension();
        } catch (MimeTypeException e) {
            log.warn("[getExtension][get file suffix ({}) failed]", mimeType, e);
            return null;
        }
    }

    /**
     * Return attachment
     *
     * @param response response
     * @param filename file name
     * @param content  attachment content
     */
    public static void writeAttachment(HttpServletResponse response, String filename, byte[] content) throws IOException {
        // Set header and contentType
        String mimeType = getMimeType(content, filename);
        response.setContentType(mimeType);
        // Set content display, download file name: https://www.cnblogs.com/wq-9/articles/12165056.html
        if (isImage(mimeType)) {
            // See https://github.com/YunaiV/ruoyi-vue-pro/issues/692 for discussion
            response.setHeader("Content-Disposition", "inline;filename=" + HttpUtils.encodeUtf8(filename));
        } else {
            response.setHeader("Content-Disposition", "attachment;filename=" + HttpUtils.encodeUtf8(filename));
        }
        // Special handling for video, to solve compatibility issues when playing video URLs on mobile
        if (StrUtil.containsIgnoreCase(mimeType, "video")) {
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", String.valueOf(content.length));
        }
        // Output attachment
        IoUtil.write(response.getOutputStream(), false, content);
    }

    /**
     * Check whether it is an image
     *
     * @param mimeType type
     * @return whether it is an image
     */
    public static boolean isImage(String mimeType) {
        return StrUtil.startWith(mimeType, "image/");
    }

}
