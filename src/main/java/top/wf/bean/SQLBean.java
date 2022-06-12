package top.wf.bean;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 类名:		SQLBean
 * 描述:		数据表的相关属性
 * @author 	WF
 */
public class SQLBean {

  /**
   * 字段名
   */
  @ExcelProperty("字段名")
  private String columnName;
  /**
   * 列中文名
   */
  @ExcelProperty("字段中文名")
  private String columnCNName;
  /**
   * 数据类型
   */
  @ExcelProperty("数据类型")
  private String dataType;
  /**
   * 数据长度
   */
  @ExcelProperty("数据长度")
  private String dataLength;
  /**
   * 小数位数
   */
  @ExcelProperty("小数位数")
  private String dataSca;
  /**
   * 是否主键
   */
  @ExcelProperty("主键")
  private String isK;
  /**
   * 可为空
   */
  @ExcelProperty("可否为空")
  private String nullAble;
  /**
   * 备注
   */
  @ExcelProperty("备注")
  private String explain;

  public SQLBean() {
  }

  public SQLBean(String columnName, String columnCNName, String dataType, String dataLength, String dataSca, String isK, String nullAble, String explain) {
    this.columnName = columnName;
    this.columnCNName = columnCNName;
    this.dataType = dataType;
    this.dataLength = dataLength;
    this.dataSca = dataSca;
    this.isK = isK;
    this.nullAble = nullAble;
    this.explain = explain;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getColumnCNName() {
    return columnCNName;
  }

  public void setColumnCNName(String columnCNName) {
    this.columnCNName = columnCNName;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getDataLength() {
    return dataLength;
  }

  public void setDataLength(String dataLength) {
    this.dataLength = dataLength;
  }

  public String getDataSca() {
    return dataSca;
  }

  public void setDataSca(String dataSca) {
    this.dataSca = dataSca;
  }

  public String getIsK() {
    return isK;
  }

  public void setIsK(String isK) {
    this.isK = isK;
  }

  public String getNullAble() {
    return nullAble;
  }

  public void setNullAble(String nullAble) {
    this.nullAble = nullAble;
  }

  public String getExplain() {
    return explain;
  }

  public void setExplain(String explain) {
    this.explain = explain;
  }

  @Override
  public String toString() {
    return "SQLBean{" +
            "columnName='" + columnName + '\'' +
            ", columnCNName='" + columnCNName + '\'' +
            ", dataType='" + dataType + '\'' +
            ", dataLength='" + dataLength + '\'' +
            ", dataSca='" + dataSca + '\'' +
            ", isK='" + isK + '\'' +
            ", nullAble='" + nullAble + '\'' +
            ", explain='" + explain + '\'' +
            '}';
  }
}
