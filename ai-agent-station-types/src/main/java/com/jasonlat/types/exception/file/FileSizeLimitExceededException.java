package com.jasonlat.types.exception.file;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件名大小限制异常类
 * 
 * @author jasonlat
 */
public class FileSizeLimitExceededException extends FileException implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public FileSizeLimitExceededException(long defaultMaxSize)
    {
        super("upload.exceed.maxSize", new Object[] { defaultMaxSize });
    }
}
