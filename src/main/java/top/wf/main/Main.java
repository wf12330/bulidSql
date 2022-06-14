package top.wf.main;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import top.wf.bean.FirstLine;
import top.wf.bean.SQLBean;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * @author wangxiaofeng
 */
public class Main {

    public static void main(String[] args) throws FontFormatException {
        new MyFrame();
    }

    public static class MyFrame extends JFrame{

        static JFrame jFrame;
        static Font font;
        static JTextField jTextField;
        static JTextArea jTextArea;
        static String tableName;

        public MyFrame() throws HeadlessException {
            //新建一个窗口
            jFrame = new JFrame("SQl生成器");
            //设置窗口大小
            jFrame.setSize(1000,500);
            //设置窗口显示位置
            jFrame.setLocation(300,200);

            font = new Font(null, Font.PLAIN, 20);

            //创建一个显示文本
            JLabel jl = new JLabel("选择需要解析的Excel文件:");
            jl.setFont(font);
            jl.setBounds(50,10,300,40);


            //创建一个文本输入框
            jTextField = new JTextField();
            jTextField.setFont(new Font(null,Font.PLAIN,15));
            //设置输入框的大小
            jTextField.setBounds(50,60,400,40);

            //新建一个按钮
            JButton jButton = new JButton("开始解析");
            //设置按钮内文本
            jButton.setFont(font);
            //设置按钮大小
            jButton.setBounds(600,60,120,40);
            jButton.addActionListener(e -> {
                String text = jTextField.getText();
                if (text==null||"".equals(text)){
                    JOptionPane.showMessageDialog(null,"请先选择文件","提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                analysisExcel(text);
            });



            //新建一个按钮
            JButton jb = new JButton("选择文件");
            //设置按钮内文本
            jb.setFont(new Font(null, Font.PLAIN, 13));
            //设置按钮大小
            jb.setBounds(448,60,90,39);
            jb.addActionListener(e -> {
                //设置文件选择
                JFileChooser chooser = new JFileChooser("./");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel文档", "xlsx");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(jFrame);

                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    jTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            });

            //新建一个文本域
            jTextArea = new JTextArea();
            jTextArea.setFont(new Font(null,Font.PLAIN,15));
            JScrollPane jScrollPane = new JScrollPane(jTextArea);
            jScrollPane.setBounds(50,130,880,300);


            //设置图标
            ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/log/sql.png")));
            jFrame.setIconImage(imageIcon.getImage().getScaledInstance(-1,-1, Image.SCALE_SMOOTH));

            //创建一个容器
            JPanel jPanel = new JPanel();
            jPanel.setLayout(null);
            jPanel.setBounds(0,0,500,500);
            jPanel.add(jb);
            jPanel.add(jButton);
            jPanel.add(jTextField);
            jPanel.add(jl);
            jPanel.add(jScrollPane);
            jFrame.add(jPanel);


            //设置窗口可被关闭
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setResizable(false);
            //设置窗口可显示
            jFrame.setVisible(true);
        }

        private void analysisExcel(String path) {

            File file = new File(path);
            if (!file.isFile()){
                JOptionPane.showMessageDialog(null,"文件路径有误，请重新选择。","提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            EasyExcel.read(path, FirstLine.class, new PageReadListener<>(MyFrame::mkTableName)).sheet().headRowNumber(1).doRead();
            EasyExcel.read(path, SQLBean.class, new PageReadListener<>(MyFrame::mkSQLScript)).sheet().headRowNumber(3).doRead();

        }

        private static void mkTableName(List<FirstLine> list){
            FirstLine firstLine = list.get(0);
            tableName = firstLine.getTableName();
        }
        /**
         * 描述:	 组装SQL脚本
         * @param list 表名    列名    列中文名     字段类型    字段长度    小数位长度(无可不填)   是否可为空   是否主键
         */
        private static void mkSQLScript(List<SQLBean> list) {
            //判空处理
            if (null == list || 0 == list.size()) {
                remind("excel内容有误，修改后重试。");
                return;
            }
            //检测是否有表名
            if (tableName==null || "".equals(tableName)){
                remind("没有检测到表名");
                return;
            }

            StringBuilder creTableSql= new StringBuilder(),keySql = new StringBuilder();

            //统计有多少主键
            int key=0;
            for (SQLBean sqlBean : list) {
                if (strIsNotNull(sqlBean.getIsK())&&"Y".equals(sqlBean.getIsK())){
                    if ("Y".equals(sqlBean.getNullAble())){
                        remind("主键字段必须设置为非空");
                        return;
                    }
                    key++;
                }
            }


            //开始组装
            creTableSql.append("CREATE TABLE ").append(tableName).append(" (\r\n");

            if (key!=0){
                keySql.append("   ").append("PRIMARY KEY (");
            }
            int forKey=1;
            for (SQLBean sqlBean : list) {
                //添加字段名
                if (strIsNotNull(sqlBean.getColumnName())) {
                    creTableSql.append("    " + "`").append(sqlBean.getColumnName()).append("`  ");
                } else {
                    remind("字段名不可为空");
                    return;
                }

                //添加数据类型
                if (strIsNotNull(sqlBean.getDataType())){
                    creTableSql.append(sqlBean.getDataType());
                }else {
                    remind("数据类型不可为空");
                }

                //添加数据长度
                if (strIsNotNull(sqlBean.getDataLength())){
                    if (strIsNotNull(sqlBean.getDataSca())){
                        creTableSql.append("(").append(sqlBean.getDataLength()).append(",").append(sqlBean.getDataSca()).append(")  ");
                    }else {
                        creTableSql.append("(").append(sqlBean.getDataLength()).append(")  ");
                    }
                }else {
                    creTableSql.append("  ");
                }

                //添加可否为空
                if (strIsNotNull(sqlBean.getNullAble())){
                    if ("Y".equals(sqlBean.getNullAble())){
                        creTableSql.append("NULL");
                    }else if ("N".equals(sqlBean.getNullAble())){
                        creTableSql.append("NOT NULL");
                    }else {
                        remind("可否为空中未知的数据");
                    }
                }

                if (sqlBean.getColumnName().equals(list.get(list.size()-1).getColumnName())){
                    //最后数据添加备注
                    if (strIsNotNull(sqlBean.getExplain())){
                        creTableSql.append("  COMMENT '").append(sqlBean.getExplain()).append("'");
                    }
                    if (key!=0){
                        creTableSql.append(",");
                    }
                }else {
                    //添加备注
                    if (strIsNotNull(sqlBean.getExplain())){
                        creTableSql.append("  COMMENT '").append(sqlBean.getExplain()).append("',");
                    }else {
                        creTableSql.append(",");
                    }
                }

                if (key!=0&&forKey==key){
                    //最后数据添加主键
                    if (strIsNotNull(sqlBean.getIsK())){
                        if ("Y".equals(sqlBean.getIsK())){
                            keySql.append("`").append(sqlBean.getColumnName()).append("`");
                        }else if ("N".equals(sqlBean.getIsK())){
                            creTableSql.append("");
                        }else {
                            remind("是否主键中未知的数据");
                        }
                    }
                }else {
                    //添加主键
                    if (strIsNotNull(sqlBean.getIsK())){
                        if ("Y".equals(sqlBean.getIsK())){
                            keySql.append("`").append(sqlBean.getColumnName()).append("`,");
                        }else if ("N".equals(sqlBean.getIsK())){
                            creTableSql.append("");
                        }else {
                            remind("是否主键中未知的数据");
                        }
                        forKey++;
                    }
                }


                creTableSql.append("\r\n");

            }
            if (key!=0){
                keySql.append(")").append(" USING BTREE");
            }
            creTableSql.append(keySql);
            creTableSql.append("\r\n");
            creTableSql.append(");");

            jTextArea.setText(creTableSql.toString());
        }

        static Boolean strIsNotNull(String str){
            if (str==null||"".equals(str)){
                return false;
            }
            return true;
        }

        static void remind(String str){
            JOptionPane.showMessageDialog(null,str,"提示", JOptionPane.ERROR_MESSAGE);

        }

    }


}
