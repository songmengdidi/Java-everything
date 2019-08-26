package com.didi.everything.core.index.impl;

import com.didi.everything.config.JavaEverythingConfig;
import com.didi.everything.core.index.FileScan;
import com.didi.everything.core.interceptor.FileInterceptor;

import java.io.File;
import java.util.LinkedList;

public class FileScanImpl implements FileScan {

    private JavaEverythingConfig config = JavaEverythingConfig.getInstance();

    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    @Override
    public void index(String path) {
        File file = new File(path);
        if(file.isFile()){
            if(config.getExcludePath().contains(file.getParent())){
                return;
            }
        }else{
            if(config.getExcludePath().contains(path)){
                return;
            }else{
                File[] files = file.listFiles();
                if(files != null){
                    for(File f : files){
                        index(f.getAbsolutePath());
                    }
                }
            }
        }
        for(FileInterceptor interceptor : this.interceptors){
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }
}
