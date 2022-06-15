package Nonograms;

import java.util.*;

//数据操作接口
interface data<N> {
    int get(N a, N b);

    void set(N a, N b, N x);
}

//游戏数据父类
public class game implements data {
    private int n, m;
    private final int[][] list;

    game(int n, int m) {
        this.n = n;
        this.m = m;
        this.list = new int[n][m];
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }

    public int get(Object a, Object b) {
        return list[Integer.parseInt(a.toString())][Integer.parseInt(b.toString())];
    }

    public void set(Object a, Object b, Object x) {
        list[Integer.parseInt(a.toString())][Integer.parseInt(b.toString())] = Integer.parseInt(x.toString());
    }

    public int[][] getList() {
        return list;
    }
}

//生成答案数据
class answer extends game {

    answer(int n, int m) {
        super(n, m);
        init();
    }

    void init() {
        var rd = new Random();
        for (int i = 0; i < getN(); ++i) {
            for (int j = 0; j < getM(); ++j) {
                super.set(i, j, rd.nextInt(2));
            }
        }
    }
}

//生成边栏数据
class table extends game {
    int d;

    public int getD() {
        return d;
    }

    boolean judge() {
        for (int i = 0; i < getN(); ++i) {
            if (get(i, 0) == 0) {
                return true;
            }
            for (int j = 0; j < getM(); ++j) {
                if (get(i, j) == 5) {
                    return true;
                }
            }
        }
        return false;
    }

    void init(int[][] list) {
        for (int i = 0; i < getN(); ++i) {
            for (int j = 0; j < getM(); ++j) {
                super.set(i, j, 0);
            }
        }
        int count = 0, num = 0;
        //0左栏，1上栏
        if (d == 0) {
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    if (list[i][j] == 1) {
                        count++;
                    } else if (count != 0 && j != 4) {
                        super.set(i, num, count);
                        num++;
                        count = 0;
                    }
                }
                if (count != 0) {
                    super.set(i, num, count);
                }
                num = 0;
                count = 0;
            }
        } else {
            for (int j = 0; j < 5; ++j) {
                for (int i = 0; i < 5; ++i) {
                    if (list[i][j] == 1) {
                        count++;
                    } else if (i != 4 && count != 0) {
                        super.set(j, num, count);
                        num++;
                        count = 0;
                    }
                }
                if (count != 0) {
                    super.set(j, num, count);
                }
                num = 0;
                count = 0;
            }
        }
    }

    public boolean arrayEquals(int[][] array2) {

        if (getList() == array2)
            return true;
        if (getList() == null || array2 == null)
            return false;

        if (getList().length != array2.length)
            return false;

        if (getList()[0].length != array2[0].length)
            return false;

        for (int i = 0; i < getList().length; i++) {  //二维数组中的每个数组使用equals方法比较
            if (!Arrays.equals(getList()[i], array2[i]))
                return false;  //有一个为false，返回false
        }

        return true;
    }

    table(int n, int m, int d, int[][] list) {
        super(n, m);
        this.d = d;
        init(list);
    }
}

//玩家游戏数据
class sub extends game {
    sub(int n, int m) {
        super(n, m);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                super.set(i, j, 0);
            }
        }
    }
}

