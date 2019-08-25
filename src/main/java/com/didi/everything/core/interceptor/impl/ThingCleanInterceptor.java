package com.didi.everything.core.interceptor.impl;

import com.didi.everything.core.dao.FileIndexDao;
import com.didi.everything.core.interceptor.ThingInterceptor;
import com.didi.everything.core.model.Thing;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThingCleanInterceptor implements ThingInterceptor,Runnable {
    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);

    private final FileIndexDao fileIndexDao;

    public ThingCleanInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }


    @Override
    public void apply(Thing thing) {
        this.queue.add(thing);
    }

    @Override
    public void run() {
        while(true){
            Thing thing = this.queue.poll();
            if(thing != null){
                fileIndexDao.delete(thing);
            }

            //1.批量删除  优化
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
