package com.didi.everything.core.common;

import com.didi.everything.core.model.FileType;
import com.didi.everything.core.model.Thing;

import java.io.File;

/**
 * 辅助工具类，将File对象转换成Thing对象
 */
public final class FileConvertThing {
    private FileConvertThing(){}

    public static Thing convert(File file){
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computeFileDepth(file));
        thing.setFileType(computeFileType(file));
        return thing;
    }

    private static int computeFileDepth(File file){
        int dept = 0;
        String[] segments = file.getAbsolutePath().split("\\\\");
        dept = segments.length;

        return dept;
    }

    private static FileType computeFileType(File file){
        if(file.isDirectory()){
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = file.getName().lastIndexOf(".");
        if(index != -1 && index < fileName.length() - 1){
            String extend = file.getName().substring(index + 1);
            return FileType.lookup(extend);
        }else{
            return FileType.OTHER;
        }
    }

}
