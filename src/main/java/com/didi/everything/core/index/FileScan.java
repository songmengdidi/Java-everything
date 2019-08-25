package com.didi.everything.core.index;

import com.didi.everything.core.dao.DataSourceFactory;
import com.didi.everything.core.dao.impl.FileIndexDaoImpl;
import com.didi.everything.core.index.impl.FileScanImpl;
import com.didi.everything.core.interceptor.FileInterceptor;
import com.didi.everything.core.interceptor.impl.FileIndexInterceptor;
import com.didi.everything.core.interceptor.impl.FilePrintInterceptor;
import com.didi.everything.core.model.Thing;

public interface FileScan {
    /**
     * 遍历path
     * @param path
     */
    void index(String path);

    /**
     * 遍历的拦截器
     * @param interceptor
     */
    void interceptor(FileInterceptor interceptor);
}
