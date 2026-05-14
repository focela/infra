package com.focela.platform.framework.excel.core.utils;

import cn.idev.excel.FastExcelFactory;
import cn.idev.excel.converters.longconverter.LongStringConverter;
import com.focela.platform.framework.common.utils.http.HttpUtils;
import com.focela.platform.framework.excel.core.handler.ColumnWidthMatchStyleStrategy;
import com.focela.platform.framework.excel.core.handler.SelectSheetWriteHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Excel utility class.
 */
public class ExcelUtils {

    /**
     * Write the list as an Excel response to the client.
     *
     * @param response  response
     * @param filename  file name
     * @param sheetName Excel sheet name
     * @param head      Excel head class
     * @param data      data list
     * @param <T>       generic type, ensuring head and data types match
     * @throws IOException when the write fails
     */
    public static <T> void write(HttpServletResponse response, String filename, String sheetName,
                                 Class<T> head, List<T> data) throws IOException {
        // Output Excel
        FastExcelFactory.write(response.getOutputStream(), head)
                .autoCloseStream(false) // Do not auto-close; let the Servlet handle it
                .registerWriteHandler(new ColumnWidthMatchStyleStrategy()) // Auto-fit width by column length, max 255
                .registerWriteHandler(new SelectSheetWriteHandler(head)) // Dropdowns backed by a fixed reference sheet
                .registerConverter(new LongStringConverter()) // Avoid precision loss for Long values
                .sheet(sheetName).doWrite(data);
        // Set the header and contentType last to avoid mutating contentType before an error response
        response.addHeader("Content-Disposition", "attachment;filename=" + HttpUtils.encodeUtf8(filename));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
    }

    public static <T> List<T> read(MultipartFile file, Class<T> head) throws IOException {
        // See https://t.zsxq.com/zM77F : wrap in try to stay compatible with Windows scenarios
        try (InputStream inputStream = file.getInputStream()) {
            return FastExcelFactory.read(inputStream, head, null)
                    .autoCloseStream(false) // Do not auto-close; let the Servlet handle it
                    .doReadAllSync();
        }
    }

}
