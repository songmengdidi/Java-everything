package com.didi.everything.config;

import lombok.Getter;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public class JavaEverythingConfig {
    private static volatile JavaEverythingConfig config;

    /**
     * 建立索引的路径
     */
    private Set<String> includePath = new HashSet<>();

    /**
     * 排除索引文件的路径
     */
    private Set<String> excludePath = new HashSet<>();

    //TODO可配置参数

    /**
     * H2数据库文件路径
     */
    private String h2IndexPath = System.getProperty("user.dir") + File.separator + "java_everything";

    private JavaEverythingConfig(){
        //this.initDefaultPathsConfig();
    }

    private void initDefaultPathsConfig(){
        //1.获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();
        //遍历的目录
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        iterable.forEach(path -> config.includePath.add(path.toString()));
        //排除的目录
        String osname = System.getProperty("os.name");
        if(osname.startsWith("Windows")){
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\Program Files (x86)");
            config.getExcludePath().add("C:\\Program Files");
            config.getExcludePath().add("C:\\ProgramData");
        }else{
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/root");
        }
    }

    public static JavaEverythingConfig getInstance(){
        if(config == null){
            synchronized (JavaEverythingConfig.class){
                if(config == null){
                    config = new JavaEverythingConfig();
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }


}
