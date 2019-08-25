package com.didi.everything.core;

import com.didi.everything.config.JavaEverythingConfig;
import com.didi.everything.core.dao.DataSourceFactory;
import com.didi.everything.core.dao.FileIndexDao;
import com.didi.everything.core.dao.impl.FileIndexDaoImpl;
import com.didi.everything.core.index.FileScan;
import com.didi.everything.core.index.impl.FileScanImpl;
import com.didi.everything.core.interceptor.ThingInterceptor;
import com.didi.everything.core.interceptor.impl.FileIndexInterceptor;
import com.didi.everything.core.interceptor.impl.ThingCleanInterceptor;
import com.didi.everything.core.model.Condition;
import com.didi.everything.core.model.Thing;
import com.didi.everything.core.search.FileSearch;
import com.didi.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JavaEverythingManager {

    private static volatile JavaEverythingManager manager;

    private FileSearch fileSearch;

    private FileScan fileScan;

    private ExecutorService executorService;

    /**
     * 清理删除的文件
     */
    private ThingCleanInterceptor thingClearInterceptor;
    private Thread backgroundClearThread;
    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);

    private JavaEverythingManager(){
        this.initComponent();
    }

    private void initComponent(){
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();

        //检查数据库
        checkDatabase();

        //业务层对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        this.fileScan = new FileScanImpl();
        //真正发布代码时是不需要的
        //this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));
        this.thingClearInterceptor = new ThingCleanInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread-Thing-Clear");
        this.backgroundClearThread.setDaemon(true);
    }

    private void checkDatabase() {
        String fileName = JavaEverythingConfig.getInstance().getH2IndexPath() + ".mv.db";
        File dbFile = new File(fileName);
        if(dbFile.isFile() && !dbFile.exists()){
            DataSourceFactory.initDatabase();
        }
    }

    public static JavaEverythingManager getInstance(){
        if(manager == null){
            synchronized (JavaEverythingManager.class){
                if(manager == null){
                    manager = new JavaEverythingManager();
                }
            }
        }
        return manager;
    }



    /**
     * 检索
     */
    public List<Thing> search(Condition condition){
        //流式处理JDK8
        return this.fileSearch.search(condition)
            .stream()
            .filter(thing -> {
                String path = thing.getPath();
                File f = new File(path);
                boolean flag = f.exists();
                if(!flag){
                    thingClearInterceptor.apply(thing);
                }
                return flag;

            }).collect(Collectors.toList());
    }

    /**
     * 索引
     */
    public void buildIndex(){
        Set<String> directories = JavaEverythingConfig.getInstance().getIncludePath();

        if(this.executorService == null){
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                private final AtomicInteger threadId = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread
                            thread = new Thread(r);
                    thread.setName("Thread-Scan-" + threadId.getAndIncrement());
                    return thread;
                }
            });
        }

        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());

        System.out.println("Build index start ...");
        for(final String path : directories){
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    JavaEverythingManager.this.fileScan.index(path);
                    //当前任务完成，值-1；
                    countDownLatch.countDown();
                }
            });
        }

        //阻塞，直到任务完成，值0
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Build index complete ...");
    }

    /**
     * 启动清理线程
     */
    public void startBackgroundClearThread(){
        if(this.backgroundClearThreadStatus.compareAndSet(false,true)){
            this.backgroundClearThread.start();
        }else{
            System.out.println("Can't repeat stsrt BackgroundClearThread");
        }
    }
}
