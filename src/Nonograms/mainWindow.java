package Nonograms;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
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
        var startTime = System.currentTimeMillis();
        var ans = new answer(5, 5);
        var topTable = new table(5, 3, 1, ans.getList());
        var leftTable = new table(5, 3, 0, ans.getList());
        while (topTable.judge() || leftTable.judge()) {
            ans.init();
            topTable.init(ans.getList());
            leftTable.init(ans.getList());
        }
        var user = new sub(5, 5);

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                System.out.print(ans.get(i, j) + " ");
            }
            System.out.println();
        }

        //窗口参数设置
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        setTitle("数织游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds((screenWidth - 600) / 2, (screenHeight - 600) / 2, 600, 600);
        setResizable(false);
        //创建主面板
        JPanel root = new JPanel();
        root.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(root);
        root.setLayout(null);
        //底部三个按钮
        JButton bt1 = new JButton("怎么玩？");
        bt1.addActionListener(e -> JOptionPane.showMessageDialog(null, "数织游戏是一种逻辑性图片益智游戏，\n玩家根据网格旁的数字，将网格中的方格填色或留空，从而展现一副隐藏的图画。\n这些数字通过离散断层方式来计算有多少条完整的线会被填入到横向或纵向的方格中。\n例如，“4 8 3”表示按顺序分别有4个、8个和3个连续方格要填色，\n且各组填色方格之间至少有一个留空方格。"));
        bt1.setFont(new Font("微软雅黑", Font.PLAIN, 24));
        bt1.setBounds(20, 500, 130, 50);
        root.add(bt1);

        JButton bt2 = new JButton("提交");
        bt2.setFont(new Font("微软雅黑", Font.PLAIN, 45));
        bt2.setBounds(160, 500, 280, 50);
        bt2.addActionListener(e -> {
            var userTopTable = new table(5, 3, 1, user.getList());
            var userLeftTable = new table(5, 3, 0, user.getList());
            if (userTopTable.arrayEquals(topTable.getList()) && userLeftTable.arrayEquals(leftTable.getList())) {
                var endTime = System.currentTimeMillis();
                var time = new Time(-28800 * 1000 + endTime - startTime);
                System.out.println(time);
                var name = new StringBuilder(JOptionPane.showInputDialog("您的解答是对的， 请输入您的名字"));
                while (name.length() == 0) {
                    name.append(JOptionPane.showInputDialog("请输入您的名字"));
                }
                sqlControl conn = new sqlControl();
                Object[] obj = {};
                ResultSet res = conn.select("select * from rankList", obj);
                try {
                    while (res.next()) {
                        System.out.println(res.getString("playerName"));
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                conn.closeConnection();
            } else {
                JOptionPane.showMessageDialog(null, "您的解答有误。");
            }
        });
        root.add(bt2);

        JButton bt3 = new JButton("排行榜");
        bt3.setFont(new Font("微软雅黑", Font.PLAIN, 24));
        bt3.setBounds(450, 500, 130, 50);
        bt3.addActionListener(e -> rankList(this, screenWidth, screenHeight));
        root.add(bt3);
        //创建纵横数字栏
        JLabel[][] llb = new JLabel[5][3];
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (topTable.get(i, j) != 0) {
                    llb[i][j] = new JLabel(String.valueOf(topTable.get(i, j)), SwingConstants.CENTER);
                    llb[i][j].setFont(new Font("微软雅黑", Font.PLAIN, 35));
                    llb[i][j].setBounds(250 + 50 * i, 50 + j * 50, 50, 50);
                    root.add(llb[i][j]);
                }
            }
        }
        JLabel[][] hlb = new JLabel[5][3];
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (leftTable.get(i, j) != 0) {
                    hlb[i][j] = new JLabel(String.valueOf(leftTable.get(i, j)), SwingConstants.CENTER);
                    hlb[i][j].setFont(new Font("微软雅黑", Font.PLAIN, 35));
                    hlb[i][j].setBounds(100 + 50 * j, 200 + i * 50, 50, 50);
                    root.add(hlb[i][j]);
                }
            }
        }
        //创造数织界面
        JButton[][] bt = new JButton[5][5];
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                bt[i][j] = new JButton();
                bt[i][j].setBackground(Color.WHITE);
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
                        bt[finalI][finalJ].setBackground(Color.WHITE);
                    } else {
                        bt[finalI][finalJ].setBackground(Color.BLACK);
                    }
                });
            }
        }
        //游戏界面边框
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(Color.BLACK));
        panel.setBounds(100, 50, 150, 150);
        root.add(panel);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.WHITE);
        panel_1.setBorder(new LineBorder(Color.BLACK));
        panel_1.setBounds(250, 50, 250, 150);
        root.add(panel_1);

        JPanel panel_2 = new JPanel();
        panel_2.setBackground(Color.WHITE);
        panel_2.setBorder(new LineBorder(Color.BLACK));
        panel_2.setBounds(100, 200, 150, 250);
        root.add(panel_2);
        //装饰性标题
        JLabel title = new JLabel("数织游戏");
        title.setFont(new Font("微软雅黑", Font.PLAIN, 25));
        title.setBounds(230, 0, 150, 35);
        root.add(title);
    }

    void rankList(Frame frame, int screenWidth, int screenHeight) {
        var rankList = new JDialog(frame, true);
        var pn = new JPanel(null);
        pn.setBorder(new EmptyBorder(5, 5, 5, 5));
        rankList.add(pn);
        rankList.setTitle("排行榜");
        rankList.setBounds((screenWidth - 600) / 2, (screenHeight - 600) / 2, 600, 600);
        var lb1 = new JLabel("排名",SwingConstants.CENTER);
        lb1.setBounds(0, 0, 200, 50);
        lb1.setFont(new Font("微软雅黑", Font.PLAIN, 35));
        pn.add(lb1);
        var lb2 = new JLabel("名字");
        lb2.setBounds(200, 0, 200, 50);
        lb2.setFont(new Font("微软雅黑", Font.PLAIN, 35));
        pn.add(lb2);
        var lb3 = new JLabel("所用时间");
        lb3.setBounds(375, 0, 200, 50);
        lb3.setFont(new Font("微软雅黑", Font.PLAIN, 35));
        pn.add(lb3);
        JLabel[][] list = new JLabel[10][3];
        int i = 0, j = 0;
        sqlControl conn = new sqlControl();
        Object[] obj = {};
        ResultSet res = conn.select("select * from rankList order by playTime limit 10", obj);
        try {
            while (res.next()) {
                while (j < 3) {
                    switch (j) {
                        case 0 -> {
                            list[i][j] = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
                            list[i][j].setBounds(50, 50 + 50 * i, 100, 50);
                        }
                        case 1 -> {
                            list[i][j] = new JLabel(res.getString("playerName"));
                            list[i][j].setBounds(200, 50 + 50 * i, 200, 50);
                        }
                        case 2 -> {
                            list[i][j] = new JLabel(res.getString("playTime"));
                            list[i][j].setBounds(400, 50 + 50 * i, 200, 50);
                        }
                    }
                    list[i][j].setFont(new Font("微软雅黑", Font.PLAIN, 35));
                    pn.add(list[i][j]);
                    ++j;
                }
                ++i;
                j = 0;
                System.out.println(res.getString("playerName"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        conn.closeConnection();
        rankList.setVisible(true);
    }
}