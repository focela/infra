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
     * Get the mineType of a file. There may be discrepancies for doc, jar and similar files.
     *
     * @param data file content
     * @return mineType, returns "application/octet-stream" when unable to recognize
     */
    @SneakyThrows
    public static String getMineType(byte[] data) {
        return TIKA.detect(data);
    }

    /**
     * Get the file type by file name; in some cases more accurate than via byte array, e.g. for jar files name is more accurate
     *
     * @param name file name
     * @return mineType, returns "application/octet-stream" when unable to recognize
     */
    public static String getMineType(String name) {
        return TIKA.detect(name);
    }

    /**
     * When both file and data are available, this method is preferred — most accurate
     *
     * @param data file content
     * @param name file name
     * @return mineType, returns "application/octet-stream" when unable to recognize
     */
    public static String getMineType(byte[] data, String name) {
        return TIKA.detect(data, name);
    }

    /**
     * Get file extension by mineType
     *
     * Note: If not found or an exception occurs, returns null
     *
     * @param mineType type
     * @return extension, e.g. .pdf
     */
    public static String getExtension(String mineType) {
        try {
            return MimeTypes.getDefaultMimeTypes().forName(mineType).getExtension();
        } catch (MimeTypeException e) {
            log.warn("[getExtension][get file suffix ({}) failed]", mineType, e);
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
        String mineType = getMineType(content, filename);
        response.setContentType(mineType);
        // Set content display, download file name: https://www.cnblogs.com/wq-9/articles/12165056.html
        if (isImage(mineType)) {
            // See https://github.com/YunaiV/ruoyi-vue-pro/issues/692 for discussion
            response.setHeader("Content-Disposition", "inline;filename=" + HttpUtils.encodeUtf8(filename));
        } else {
            response.setHeader("Content-Disposition", "attachment;filename=" + HttpUtils.encodeUtf8(filename));
        }
        // Special handling for video, to solve compatibility issues when playing video URLs on mobile
        if (StrUtil.containsIgnoreCase(mineType, "video")) {
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", String.valueOf(content.length));
        }
        // Output attachment
        IoUtil.write(response.getOutputStream(), false, content);
    }

    /**
     * Check whether it is an image
     *
     * @param mineType type
     * @return whether it is an image
     */
    public static boolean isImage(String mineType) {
        return StrUtil.startWith(mineType, "image/");
    }

}
