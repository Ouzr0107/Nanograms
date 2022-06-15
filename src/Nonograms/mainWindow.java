package Nonograms;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        sqlControl conn = new sqlControl();
        var startTime = System.currentTimeMillis();
        var ans = new answer(5, 5);
        var topTable = new table(5, 3, 1, ans.getList());
        var leftTable = new table(5, 3, 0, ans.getList());
        while (!topTable.judge() || !leftTable.judge()) {
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
        setTitle("Nonograms");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 300);
        setResizable(false);
        //创建主面板
        JPanel root = new JPanel();
        root.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(root);
        root.setLayout(null);
        //底部三个按钮
        JButton bt1 = new JButton("怎么玩？");
        bt1.addActionListener(e -> JOptionPane.showMessageDialog(null, "Nonograms是一种逻辑性图片益智游戏，\n玩家根据网格旁的数字，将网格中的方格填色或留空，从而展现一副隐藏的图画。\n这些数字通过离散断层方式来计算有多少条完整的线会被填入到横向或纵向的方格中。\n例如，“4 8 3”表示按顺序分别有4个、8个和3个连续方格要填色，\n且各组填色方格之间至少有一个留空方格。"));
        bt1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        bt1.setBounds(10, 220, 130, 20);
        root.add(bt1);

        JButton bt2 = new JButton("提交");
        bt2.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        bt2.setBounds(160, 218, 285, 25);
        bt2.addActionListener(e -> {
            var userTopTable = new table(5, 3, 1, user.getList());
            var userLeftTable = new table(5, 3, 0, user.getList());
            if (userTopTable.arrayEquals(topTable.getList()) && userLeftTable.arrayEquals(leftTable.getList())) {
                var endTime = System.currentTimeMillis();
                var name = new StringBuilder(JOptionPane.showInputDialog("您的解答是对的， 请输入您的名字"));
                while (name.length() == 0) {
                    name.append(JOptionPane.showInputDialog("请输入您的名字"));
                }
                Object[] obj = {};
                ResultSet res = conn.select("select * from list", obj);
                try {
                    while (res.next()){
                        System.out.println(res.getString("userName"));
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
        bt3.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        bt3.setBounds(465, 220, 110, 20);
        root.add(bt3);
        //创建纵横数字栏
        JLabel[][] llb = new JLabel[5][3];
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (topTable.get(i, j) != 0) {
                    llb[i][j] = new JLabel(String.valueOf(topTable.get(i, j)));
                    llb[i][j].setBounds(285 + 20 * i, 40 + j * 20, 20, 20);
                    root.add(llb[i][j]);
                }
            }
        }
        JLabel[][] hlb = new JLabel[5][3];
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (leftTable.get(i, j) != 0) {
                    hlb[i][j] = new JLabel(String.valueOf(leftTable.get(i, j)));
                    hlb[i][j].setBounds(225 + 20 * j, 100 + i * 20, 20, 20);
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
                bt[i][j].setBounds(280 + 20 * j, 100 + 20 * i, 20, 20);
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
        panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel.setBounds(220, 40, 60, 60);
        root.add(panel);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.WHITE);
        panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel_1.setBounds(280, 40, 100, 60);
        root.add(panel_1);

        JPanel panel_2 = new JPanel();
        panel_2.setBackground(Color.WHITE);
        panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel_2.setBounds(220, 100, 60, 100);
        root.add(panel_2);
        //装饰性标题
        JLabel title = new JLabel("Nanograms");
        title.setFont(new Font("微软雅黑", Font.PLAIN, 25));
        title.setBounds(230, 0, 150, 35);
        root.add(title);
    }
}