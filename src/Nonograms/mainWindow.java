package Nonograms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class mainWindow extends JFrame {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                //启动游戏主窗口
                mainWindow frame = new mainWindow();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public mainWindow() {
        //游戏数据生成
        var startTime = System.currentTimeMillis();//记录当前时间
        var ans = new answer(5, 5);//生成答案
        var topTable = new table(5, 3, 1, ans.getList());//顶栏数据
        var leftTable = new table(5, 3, 0, ans.getList());//侧栏数据
        //判断生成游戏是否符合规则
        while (topTable.judge() || leftTable.judge()) {
            ans.init();
            topTable.init(ans.getList());
            leftTable.init(ans.getList());
        }
        var user = new sub(5, 5);//生成用户数据

        //命令行答案
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                System.out.print(ans.get(i, j) + " ");
            }
            System.out.println();
        }

        //窗口参数设置
        Toolkit kit = Toolkit.getDefaultToolkit();//读取屏幕参数
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        setTitle("数织游戏");//设置标题
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭参数
        setBounds((screenWidth - 600) / 2, (screenHeight - 600) / 2, 600, 600);//设置在屏幕中间，大小600*600
        setResizable(false);//不可改变大小

        //创建主面板
        JPanel root = new JPanel();//生成面板
        root.setBorder(new EmptyBorder(5, 5, 5, 5));//内部边框
        setContentPane(root);
        root.setBackground(new Color(255, 255, 254));
        root.setLayout(null);//绝对布局

        //计时标签
        var timeStr = new Time(-28800 * 1000 + System.currentTimeMillis() - startTime);//初始化
        var timeLabel = new JLabel("已用时间：" + timeStr, SwingConstants.CENTER);
        timeLabel.setBounds(100, 0, 400, 50);
        timeLabel.setFont(getSelfDefinedFont(35));
        timeLabel.setForeground(new Color(9, 64, 103));
        root.add(timeLabel);
        ActionListener taskPerformer = e -> {//每秒变化
            var timeStr1 = new Time(-28800 * 1000 + System.currentTimeMillis() - startTime);
            timeLabel.setText("已用时间：" + timeStr1);
        };
        var timer = new Timer(1000, taskPerformer);
        timer.start();//开始计时

        //底部三个按钮
        JButton bt1 = new JButton("怎么玩？");//说明窗口
        bt1.addActionListener(e -> JOptionPane.showMessageDialog(null, "<html><div style=\"font-family: Ubuntu; font-size: 20px; width: 300px\">数织游戏是一种逻辑性图片益智游戏，玩家根据网格旁的数字，将网格中的方格填色或留空，从而展现一副隐藏的图画。 这些数字通过离散断层方式来计算有多少条完整的线会被填入到横向或纵向的方格中。例如，“4 8 3”表示按顺序分别有4个、8个和3个连续方格要填色，且各组填色方格之间至少有一个留空方格。</div></html>"));
        bt1.setFont(getSelfDefinedFont(24));
        bt1.setForeground(new Color(255, 255, 254));
        bt1.setBackground(new Color(61, 169, 252));
        bt1.setBorder(null);
        bt1.setBounds(20, 480, 130, 50);
        root.add(bt1);

        JButton bt2 = new JButton("提交");//提交答案
        bt2.setFont(getSelfDefinedFont(35));
        bt2.setForeground(new Color(255, 255, 254));
        bt2.setBackground(new Color(61, 169, 252));
        bt2.setBounds(160, 480, 280, 50);
        bt2.addActionListener(e -> {
            var userTopTable = new table(5, 3, 1, user.getList());//根据用户回答生成顶栏和侧栏数据
            var userLeftTable = new table(5, 3, 0, user.getList());
            if (userTopTable.arrayEquals(topTable.getList()) && userLeftTable.arrayEquals(leftTable.getList())) {//根据两栏的数据判断正确与否，因为可能有多解

                timer.stop();//计时器暂停
                var endTime = System.currentTimeMillis();
                var time = new Time(-28800 * 1000 + endTime - startTime);//计算使用时间

                //显示名字输入
                var name = new StringBuilder(JOptionPane.showInputDialog("<html><div style=\"font-family: Ubuntu; font-size: 15px; width: 300px\">您的解答是对的， 请输入您的名字</div></html>"));
                while (name.toString().trim().length() == 0) {//若输入空白则再弹出
                    name.append(JOptionPane.showInputDialog( "<html><div style=\"font-family: Ubuntu; font-size: 15px; width: 300px\">请输入您的名字</div></html>"));
                }

                //数据库连接
                sqlControl conn = new sqlControl();
                Object[] obj = {};//新建空白类
                ResultSet res = conn.select("select count(*) from rankList where playerName = '" + name + "'", obj);//读取结果
                try {
                    if (res.next() && res.getInt(1) != 0) {//如果数据库中有该名字
                        res = conn.select("select * from rankList where playerName = '" + name + "'", obj);//获取数据库用户的时间
                        if (res.next() && res.getTime("playTime").getTime() > time.getTime()) {//比较，若时间更短则更新
                            conn.update("update rankList set `playTime` = '" + time + "' where `playerName` = '" + name + "'", obj);
                        }
                    } else {//若没有该用户则将数据插入
                        Object[] objs = {name.toString(), time};
                        conn.update("insert into rankList values(?, ?)", objs);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                conn.closeConnection();//断开连接
            } else {//错误答案提醒
                JOptionPane.showMessageDialog(null, "<html><div style=\"font-family: Ubuntu; font-size: 15px; width: 300px\">您的解答有误。</div></html>");
            }
        });
        root.add(bt2);

        JButton bt3 = new JButton("排行榜");//排行榜前十显示
        bt3.setFont(getSelfDefinedFont(24));
        bt3.setBackground(new Color(61, 169, 252));
        bt3.setForeground(new Color(255, 255, 254));
        bt3.setBorder(null);
        bt3.setBounds(450, 480, 130, 50);
        bt3.addActionListener(e -> rankList(this, screenWidth, screenHeight));//生成排行榜
        root.add(bt3);

        //创建纵横数字栏
        JLabel[][] llb = new JLabel[5][3];//顶栏
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 3; ++j) {
                llb[i][j] = new JLabel("", SwingConstants.CENTER);
                if (topTable.get(i, j) != 0) {
                    llb[i][j].setText(String.valueOf(topTable.get(i, j)));
                }
                llb[i][j].setFont(getSelfDefinedFont(35));
                llb[i][j].setBorder(new LineBorder(new Color(144, 180, 206), 2));
                llb[i][j].setBounds(250 + 50 * i, 50 + j * 50, 50, 50);
                root.add(llb[i][j]);
            }
        }

        JLabel[][] hlb = new JLabel[5][3];//侧栏
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 3; ++j) {
                hlb[i][j] = new JLabel("", SwingConstants.CENTER);
                hlb[i][j].setFont(getSelfDefinedFont(35));
                if (leftTable.get(i, j) != 0) {
                    hlb[i][j].setText(String.valueOf(topTable.get(i, j)));
                }
                hlb[i][j].setBorder(new LineBorder(new Color(144, 180, 206), 2));
                hlb[i][j].setBounds(100 + 50 * j, 200 + i * 50, 50, 50);
                root.add(hlb[i][j]);
            }
        }

        //创造数织界面
        JButton[][] bt = new JButton[5][5];
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                bt[i][j] = new JButton();
                bt[i][j].setBackground(new Color(216, 238, 254));
                bt[i][j].setBorder(new LineBorder(new Color(9, 64, 103), 2));
                bt[i][j].setBounds(250 + 50 * j, 200 + 50 * i, 50, 50);
                root.add(bt[i][j]);
                int finalI = i;
                int finalJ = j;
                bt[i][j].addActionListener(e -> {
                    int n = user.get(finalI, finalJ);
                    if (n == 0) {
                        n = 1;
                    } else {
                        n = 0;
                    }
                    user.set(finalI, finalJ, n);
                    if (user.get(finalI, finalJ) == 0) {
                        bt[finalI][finalJ].setBackground(new Color(216, 238, 254));
                    } else {
                        bt[finalI][finalJ].setBackground(new Color(9, 64, 103));
                    }
                });
            }
        }

        //左上角标题
        var title1 = new JLabel("数 织", SwingConstants.CENTER);
        title1.setFont(getSelfDefinedFont(60));
        title1.setForeground(new Color(9, 64, 103));
        title1.setBounds(100, 50, 150, 75);
        root.add(title1);

        var title2 = new JLabel("游 戏", SwingConstants.CENTER);
        title2.setFont(getSelfDefinedFont(60));
        title2.setForeground(new Color(95, 108, 123));
        title2.setBounds(100, 125, 150, 75);
        root.add(title2);

        //游戏界面底部边框
        JPanel panel = new JPanel();
        panel.setBackground(new Color(216, 238, 254));
        panel.setBorder(new LineBorder(new Color(9, 64, 103), 2));
        panel.setBounds(99, 49, 151, 151);
        root.add(panel);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(new Color(216, 238, 254));
        panel_1.setBorder(new LineBorder(new Color(9, 64, 103), 4));
        panel_1.setBounds(250, 49, 251, 151);
        root.add(panel_1);

        JPanel panel_2 = new JPanel();
        panel_2.setBackground(new Color(216, 238, 254));
        panel_2.setBorder(new LineBorder(new Color(9, 64, 103), 4));
        panel_2.setBounds(99, 200, 151, 251);
        root.add(panel_2);
    }

    //排行版面板
    void rankList(Frame frame, int screenWidth, int screenHeight) {
        var rankList = new JDialog(frame, false);
        var pn = new JPanel(null);
        pn.setBorder(new EmptyBorder(5, 5, 5, 5));
        rankList.add(pn);
        rankList.setTitle("排行榜");
        rankList.setBounds((screenWidth - 600) / 2 + 600, (screenHeight - 600) / 2, 500, 600);
        var lb1 = new JLabel("排名", SwingConstants.CENTER);
        lb1.setBounds(0, 0, 100, 50);
        lb1.setFont(getSelfDefinedFont(35));
        lb1.setForeground(new Color(9, 64, 103));
        pn.add(lb1);
        var lb2 = new JLabel("名字");
        lb2.setBounds(150, 0, 200, 50);
        lb2.setFont(getSelfDefinedFont(35));
        lb2.setForeground(new Color(9, 64, 103));
        pn.add(lb2);
        var lb3 = new JLabel("所用时间");
        lb3.setBounds(300, 0, 200, 50);
        lb3.setFont(getSelfDefinedFont(35));
        lb3.setForeground(new Color(9, 64, 103));
        pn.add(lb3);
        JLabel[][] list = new JLabel[10][3];
        int i = 0, j = 0;
        sqlControl conn = new sqlControl();
        Object[] obj = {};
        ResultSet res = conn.select("select * from rankList order by playTime limit 10", obj);//读取前十名
        try {
            while (res.next()) {
                while (j < 3) {
                    switch (j) {
                        case 0 -> {
                            list[i][j] = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
                            list[i][j].setBounds(0, 50 + 50 * i, 100, 50);
                        }
                        case 1 -> {
                            list[i][j] = new JLabel(res.getString("playerName"));
                            list[i][j].setBounds(120, 50 + 50 * i, 200, 50);
                        }
                        case 2 -> {
                            list[i][j] = new JLabel(res.getString("playTime"));
                            list[i][j].setBounds(300, 50 + 50 * i, 200, 50);
                        }
                    }
                    list[i][j].setFont(getSelfDefinedFont(35));
                    list[i][j].setForeground(new Color(95, 108, 123));
                    pn.add(list[i][j]);
                    ++j;
                }
                ++i;
                j = 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        conn.closeConnection();
        rankList.setVisible(true);
    }

    //字体本地读取
    static java.awt.Font getSelfDefinedFont(int size) {
        java.awt.Font font;
        File file = new File("src/Nonograms/HarmonyOS_Sans_SC_Regular.ttf");//鸿蒙简体中文字体
        try {
            font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, file);
            font = font.deriveFont(Font.BOLD, size);
        } catch (FontFormatException | IOException e) {
            return null;
        }
        return font;
    }
}