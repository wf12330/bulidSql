package top.wf.bean;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 读取首行表名
 * @author wangxiaofeng
 */
public class FirstLine {

    /**
     * 表名
     */
    @ExcelProperty("表名")
    private String tableName;
    /**
     * 表中文名
     */
    @ExcelProperty("表中文名")
    private String tableCNName;

    public FirstLine() {
    }

    public FirstLine(String tableName, String tableCNName) {
        this.tableName = tableName;
        this.tableCNName = tableCNName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableCNName() {
        return tableCNName;
    }

    public void setTableCNName(String tableCNName) {
        this.tableCNName = tableCNName;
    }

    @Override
    public String toString() {
        return "FirstLine{" +
                "tableName='" + tableName + '\'' +
                ", tableCNName='" + tableCNName + '\'' +
                '}';
    }

}
