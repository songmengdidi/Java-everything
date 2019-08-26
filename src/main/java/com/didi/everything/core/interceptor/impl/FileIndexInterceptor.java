package com.didi.everything.core.interceptor.impl;

import com.didi.everything.core.common.FileConvertThing;
import com.didi.everything.core.dao.FileIndexDao;
import com.didi.everything.core.interceptor.FileInterceptor;
import com.didi.everything.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements FileInterceptor {

    private final FileIndexDao fileIndexDao;

    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing = FileConvertThing.convert(file);
        fileIndexDao.insert(thing);
    }
}
