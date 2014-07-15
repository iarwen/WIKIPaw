import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

public class DBConnectPool
{

    private static BasicDataSource dbs = null;

    private DBConnectPool()
    {
    }

    private static void setupDataSource()
    {
        dbs = new BasicDataSource();
        // 设置驱动程序
        dbs.setDriverClassName("com.mysql.jdbc.Driver");
        // 设置连接用户名
        dbs.setUsername("root");
        // 设置连接密码
        dbs.setPassword("root");
        // 设置连接地址
        dbs.setUrl("jdbc:mysql://localhost:3306/wiki");
        // 设置初始化连接总数
        dbs.setInitialSize(50);
        // 设置同时应用的连接总数
        dbs.setMaxActive(-1);
        // 设置在缓冲池的最大连接数
        dbs.setMaxIdle(-1);
        // 设置在缓冲池的最小连接数
        dbs.setMinIdle(0);
        // 设置最长的等待时间
        dbs.setMaxWait(-1);
        // 设置链接时测试
        dbs.setTestOnBorrow(true);

    }

    // 显示连接池的连接个数的方法
    public static void printDataSourceStats() throws SQLException
    {
        System.out.println("已连接数据库时长：" + dbs.getLoginTimeout() + "秒|" + dbs.getLoginTimeout() / 60 + "分|" + dbs.getLoginTimeout() / 3600 + "时");
        System.out.println("目前等待资源的请求数：" + dbs.getNumIdle());
    }

    // 获取一个数据库连接
    public static synchronized Connection getConnection()
    {
        if (dbs == null)
        {
            setupDataSource();
        }
        Connection con = null;

        try
        {
            con = dbs.getConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return con;
    }

    // 关闭连接池的方法
    public static void shutdownDataSource() throws SQLException
    {
        dbs.close();
    }

}