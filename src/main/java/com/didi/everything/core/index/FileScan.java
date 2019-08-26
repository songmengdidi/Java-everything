package com.didi.everything.core.index;

import com.didi.everything.core.interceptor.FileInterceptor;

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
