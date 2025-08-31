package com.jasonlat.types.exception.file;


import com.jasonlat.types.exception.BaseException;

import java.io.Serial;

/**
 * @author jasonlat
 */
public class FileException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
