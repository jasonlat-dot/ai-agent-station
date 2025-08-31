package com.jasonlat.types.exception.file;

import java.io.Serial;
import java.io.Serializable;


/**
 * 文件名称超长限制异常类
 * 
 * @author jasonlat
 */
public class FileNameLengthLimitExceededException extends FileException implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public FileNameLengthLimitExceededException(int defaultFileNameLength) {
        super("upload.filename.exceed.length", new Object[] { defaultFileNameLength });
    }
}
