package com.didi.everything.core.search.impl;

import com.didi.everything.core.dao.FileIndexDao;
import com.didi.everything.core.model.Condition;
import com.didi.everything.core.model.Thing;
import com.didi.everything.core.search.FileSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务层：
 */

public class FileSearchImpl implements FileSearch {
    private final FileIndexDao fileIndexDao;

    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public List<Thing> search(Condition condition) {
        //数据库的处理逻辑

        if(condition == null){
            return new ArrayList<>();
        }
        return this.fileIndexDao.search(condition);
    }
}
