package com.didi.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.didi.everything.config.JavaEverythingConfig;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSourceFactory {

    /**
     * 数据源
     */
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory(){
    }

    public static DataSource dataSource(){
        if(dataSource == null){
            synchronized (DataSourceFactory.class){
                if(dataSource == null){
                    //实例化
                    dataSource = new DruidDataSource();

                    dataSource.setDriverClassName("org.h2.Driver");
                    //url,username,password
                    //采用的是H2的嵌入式数据库，数据库以本地文件的方式进行存储，只需要提供url接口
                    //获取当前工程路径
                    //JDBC规范中关于H2 jdbc:h2:filepath->存储到本地文件
                    //JDBC规范中关于H2 jdbc:h2:~/filepath->存储到当前用户的home目录
                    //JDBC规范中关于H2 jdbc:h2://ip:port/databaseName->存储到服务器
                    dataSource.setUrl("jdbc:h2:" + JavaEverythingConfig.getInstance().getH2IndexPath());
                    dataSource.setValidationQuery("select now()");
                }
            }
        }
        return dataSource;
    }

    //初始化数据库
    public static void initDatabase(){
        //1.获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        //2.获取SQL语句
        //采取读取classpath路径下的文件
        //try-with-resources
        try(InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("java_everything.sql");
        ){
            if(in == null){
                throw new RuntimeException("Not read init database script please check it");
            }
            StringBuilder sqlBuilder = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in));){
                String line = null;
                while((line = reader.readLine())!=null){
                    if(!line.startsWith("--")){
                        sqlBuilder.append(line);
                    }
                }
            }
            //3.获取数据库连接和名称执行SQL
            String sql = sqlBuilder.toString();
            //JDBC
            //3.1获取数据库连接
            Connection connection = dataSource.getConnection();
            //3.2创建命令
            PreparedStatement statement = connection.prepareStatement(sql);
            //3.3执行SQL语句
            statement.execute();
            connection.close();
            statement.close();

        }catch(IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        /*try(InputStream in = DataSourceFactory.class
                .getClassLoader().getResourceAsStream("java_everything" + ".sql");){

            //String sql = IOUtils.toString(in);
            //System.out.println(sql);
            IOUtils.readLines(in)
                    .stream()
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String line) {
                            return !line.startsWith("--");
                        }
                    })
                    .forEach(line -> System.out.println(line));
        }catch(IOException e){
        }*/

    }
}