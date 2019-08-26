package com.didi.everything.cmd;

import com.didi.everything.config.JavaEverythingConfig;
import com.didi.everything.core.JavaEverythingManager;
import com.didi.everything.core.model.Condition;
import com.didi.everything.core.model.Thing;

import java.util.List;
import java.util.Scanner;

public class JavaEverythingCmdApp {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        //解析参数
        parseParams(args);

        //欢迎
        welcome();

        //统一调度器
        JavaEverythingManager manager = JavaEverythingManager.getInstance();

        //启动后台清理线程
        manager.startBackgroundClearThread();

        //启动监控
        manager.startFileSystemMonitor();

        //交互式
        interactive(manager);
    }

    private static void parseParams(String[] args) {
        JavaEverythingConfig config = JavaEverythingConfig.getInstance();
        /**
         * 如果用户设置的参数格式不对，使用默认值即可
         */
        for(String param : args){
            String maxReturnParam = "--maxReturn=";
            if(param.startsWith(maxReturnParam)){
                //--maxReturn=value
                int index = param.indexOf("=");
                String maxReturnStr = param.substring(index + 1);
                try{
                    int maxReturn = Integer.parseInt(maxReturnStr);
                    config.setMaxReturn(maxReturn);
                }catch(NumberFormatException e){
                    //如果用户设置的参数格式不对，使用默认值即可
                }
            }

            String deptOrderByAsc = "deptOrderByAsc=";
            if(param.startsWith(deptOrderByAsc)){
                //--deptOrderByAsc=value
                int index = param.indexOf("=");
                if(index < deptOrderByAsc.length() - 1){
                    String deptOrderByAscStr = param.substring(index + 1);
                    config.setDeptOrderAsc(Boolean.parseBoolean(deptOrderByAscStr));
                }
            }

            String includePathParam = "--includePath=";
            if(param.startsWith(includePathParam)){
                //--includePath=values
                int index = param.indexOf("=");
                String includePathStr = param.substring(index + 1);
                String[] includePaths = includePathStr.split(";");
                if(includePaths.length > 0){
                    config.getIncludePath().clear();
                }
                for(String p : includePaths){
                    config.getIncludePath().add(p);
                }
            }

            String excludePathParam = "--excludePath=";
            if(param.startsWith(excludePathParam)){
                //--excludePath=values
                int index = param.indexOf("=");

                String excludePathStr = param.substring(index + 1);
                String[] excludePaths = excludePathStr.split(";");
                config.getExcludePath().clear();
                for(String p : excludePaths){
                    config.getExcludePath().add(p);
                }
            }
        }
    }

    private static void interactive(JavaEverythingManager manager){
        while(true){
            System.out.println("everything >>");
            String input = scanner.nextLine();
            //优先处理search
            if(input.startsWith("search")){
                String[] values = input.split(" ");
                if(values.length >= 2){
                    if(!values[0].equals("search")){
                        help();
                        continue;
                    }
                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);
                    if(values.length >= 3){
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    search(manager,condition);
                    continue;
                }else{
                    help();
                    continue;
                }
            }
            switch(input){
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    return;
                case "index":
                    index(manager);
                    break;
                default:
                    help();
            }
        }
    }
    private static void search(JavaEverythingManager manager,Condition condition){

        condition.setLimit(JavaEverythingConfig.getInstance().getMaxReturn());
        condition.setOrderByAsc(JavaEverythingConfig.getInstance().getDeptOrderAsc());

        List<Thing> thingList = manager.search(condition);
        for(Thing thing : thingList){
            System.out.println(thing.getPath());
        }
    }
    private static void index(final JavaEverythingManager manager){
        //统一调度器中的index
        new Thread(manager::buildIndex).start();
    }
    private static void quit(){
        System.out.println("再见！");
        System.exit(0);
    }
    private static  void welcome(){

        System.out.println("欢迎使用，Java Everything");
    }
    private static void help(){
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("搜索：search <name> [<file-Type> img | doc | bin | archive | other]");
    }
}
