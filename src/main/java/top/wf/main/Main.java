package top.wf.main;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import top.wf.bean.FirstLine;
import top.wf.bean.SQLBean;
import top.wf.data.Data;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangxiaofeng
 */
public class Main {

    public static void main(String[] args) {
        new MyFrame();
    }

    public static class MyFrame extends JFrame{

        static JFrame jFrame;
        static Font font;
        static JTextField jTextField;
        static JTextArea jTextArea;
        static StringBuffer stringBuffer=new StringBuffer();

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

        private void analysisExcel(String path)  {

            File file = new File(path);
            if (!file.isFile()){
                JOptionPane.showMessageDialog(null,"文件路径有误，请重新选择。","提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ExcelReader build = EasyExcel.read(path).build();
            List<ReadSheet> readSheets = build.excelExecutor().sheetList();


            // 创建定长线程池
            ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(5);
            CountDownLatch countDownLatch = new CountDownLatch(readSheets.size());
            for (ReadSheet readSheet : readSheets) {
                Data data = new Data();
                newFixedThreadPool.execute(() -> {

                    EasyExcel.read(path, FirstLine.class, new PageReadListener<>(data::mkTableName)).sheet(readSheet.getSheetName()).headRowNumber(1).doRead();
                    EasyExcel.read(path, SQLBean.class, new PageReadListener<SQLBean>(dataList->{
                        data.mkSQLScript(dataList,data.getTableName());
                    })).sheet(readSheet.getSheetName()).headRowNumber(3).doRead();
                    stringBuffer.append(data.getBuffer());
                    countDownLatch.countDown();

                });
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            jTextArea.setText(stringBuffer.toString());
        }
    }
}
