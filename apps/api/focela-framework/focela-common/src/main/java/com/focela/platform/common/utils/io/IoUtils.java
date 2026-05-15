package com.focela.platform.common.utils.io;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;

import java.io.InputStream;

/**
 * IO utilities; fills gaps in {@link cn.hutool.core.io.IoUtil}.
 */
public class IoUtils {

    /**
     * Read UTF-8 encoded content from the stream.
     *
     * @param in input stream
     * @param isClose whether to close the stream
     * @return content
     * @throws IORuntimeException IO exception
     */
    public static String readUtf8(InputStream in, boolean isClose) throws IORuntimeException {
        return StrUtil.utf8Str(IoUtil.read(in, isClose));
    }

}
