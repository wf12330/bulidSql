package top.wf.data;

import top.wf.bean.FirstLine;
import top.wf.bean.SQLBean;

import javax.swing.*;
import java.util.List;

public class Data {

    private String tableName;
    private final StringBuffer buffer = new StringBuffer();
    public  void mkTableName(List<FirstLine> list){
        FirstLine firstLine = list.get(0);
        tableName= firstLine.getTableName();
    }
    /**
     * 描述:	 组装SQL脚本
     *
     * @param list 表名    列名    列中文名     字段类型    字段长度    小数位长度(无可不填)   是否可为空   是否主键
     */
    public void mkSQLScript(List<SQLBean> list, String tableName) {
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
        creTableSql.append(");\r\n");
        buffer.append(creTableSql);
    }
    static Boolean strIsNotNull(String str){
        return str != null && !"".equals(str);
    }

    static void remind(String str){
        JOptionPane.showMessageDialog(null,str,"提示", JOptionPane.ERROR_MESSAGE);
    }

    public String getTableName() {
        return tableName;
    }

    public StringBuffer getBuffer() {
        return buffer;
    }
}
