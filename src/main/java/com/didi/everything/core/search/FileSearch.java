package com.didi.everything.core.search;

import com.didi.everything.core.dao.DataSourceFactory;
import com.didi.everything.core.model.Condition;
import com.didi.everything.core.model.Thing;
import com.didi.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.util.List;

public interface FileSearch {
    /**
     * 根据condition条件进行数据库检索
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);

}
