package com.didi.everything.core.model;
import lombok.Data;
/**
 * 文件属性信息索引之后的记录用Thing表示
 */
@Data //getter setter toString生成完成
public class Thing {

    /**
     * 文件名称（保留名称）
     */
    private String name;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件路径深度
     */
    private Integer depth;

    /**
     * 文件类型
     */
    private FileType fileType;

}
