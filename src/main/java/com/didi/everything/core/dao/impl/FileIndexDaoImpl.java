package com.didi.everything.core.dao.impl;

import com.didi.everything.core.dao.DataSourceFactory;
import com.didi.everything.core.dao.FileIndexDao;
import com.didi.everything.core.model.Condition;
import com.didi.everything.core.model.FileType;
import com.didi.everything.core.model.Thing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileIndexDaoImpl implements FileIndexDao {
    private final DataSource dataSource;

    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;

        try{
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备sql语句
            String sql = "insert into file_index(name, path, depth, file_type) values(?,?,?,?)";
            //3.准备命令
            statement = connection.prepareStatement(sql);
            //4.设置参数
            statement.setString(1,thing.getName());
            statement.setString(2,thing.getPath());
            statement.setInt(3,thing.getDepth());
            statement.setString(4,thing.getFileType().name());
            //5.执行命令
            statement.executeUpdate();

        }catch(SQLException e){

        }finally {
            releaseResource(null,statement,connection);
        }
    }

    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> things = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try{
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备sql语句

            //name      :like
            //fileType  :=
            //limit     : limit offset
            //orderbyAsc:order by
            //为什么不用StringBufffer???
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" select name,path,depth,file_type from file_index ");
            sqlBuilder.append(" where ")
                    .append(" name like '%")
                    .append(condition.getName())
                    .append("%' ");
            if(condition.getFileType()!=null){
                sqlBuilder.append(" and file_type = '")
                        .append(condition.getFileType().toUpperCase())
                        .append("' ");
            }

            //limit,order
            sqlBuilder.append(" order by depth ")
                    .append(condition.getOrderByAsc() ? "asc":"desc")
                    .append(" limit ")
                    .append(condition.getLimit())
                    .append(" offset 0 ");
            System.out.println(sqlBuilder.toString());

            //3.准备命令
            statement = connection.prepareStatement(sqlBuilder.toString());
            //4.设置参数

            //5.执行命令
            resultSet = statement.executeQuery();

            //6.处理结果
            while(resultSet.next()){
                //数据库的行记录——>Java中的对象Thing
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                String fileType = resultSet.getString("file_type");
                thing.setFileType(FileType.lookupByName(fileType));
                things.add(thing);
            }

        }catch(SQLException e){

        }finally {
            releaseResource(resultSet,statement,connection);
        }
        return things;
    }

    //解决内部代码大量重复问题：重构
    private void releaseResource(ResultSet resultSet,PreparedStatement statement,Connection connection){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement!=null){
            try{
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

/*
    public static void main(String[] args) {
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(DataSourceFactory.dataSource());
        Thing thing = new Thing();
        thing.setName("简历2.ppt");
        thing.setPath("E:\\a\\test\\简历2.ppt");
        thing.setDepth(3);
        thing.setFileType(FileType.DOC);
        //fileIndexDao.insert(thing);

        Condition condition = new Condition();
        condition.setName("简历");
        condition.setLimit(2);
        condition.setOrderByAsc(true);
        condition.setFileType("IMG");
        List<Thing> things = fileIndexDao.search(condition);
        for(Thing t:things){
            System.out.println(t);
        }

    }*/
}
