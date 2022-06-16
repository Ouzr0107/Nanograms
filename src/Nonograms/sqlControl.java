package Nonograms;

import java.sql.*;

public class sqlControl {
    private static String url;
    private static String user;
    private static String pwd;

    static {
        try {
            url = "jdbc:mysql://localhost:3306/rankList";
            user = "Nanograms";
            pwd = "@Jian10316210";
            String driver = "com.mysql.cj.jdbc.Driver";
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("未找到数据库驱动");
        }
    }

    private Connection conn = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public void getConnection() {
        // 获取数据库连接
        try {
            conn = DriverManager.getConnection(url, user, pwd);
            if (!conn.isClosed()) {
                System.out.println("数据库连接成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库连接失败");
        }
    }

    public ResultSet select(String sql, Object[] objs) {
        // 查询操作
        try {
            getConnection();
            preparedStatement = conn.prepareStatement(sql);
            for (int i = 0; i < objs.length; i++) {
                preparedStatement.setObject(i + 1, objs[i]);
            }
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public int update(String sql, Object[] objs) {
        // 增删改操作
        int i = 0;
        try {
            getConnection();
            preparedStatement = conn.prepareStatement(sql);
            for (int j = 0; j < objs.length; j++) {
                preparedStatement.setObject(j + 1, objs[j]);
            }
            i = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public void closeConnection() {
        // 断开连接
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
