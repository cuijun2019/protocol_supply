package com.etone.protocolsupply.model.dto;
/**
 * Excel导出通用实体
 *
 */
public class ExcelHeaderColumnPojo {
	// 表头单元格名称
	private String headerLabel;

	// 与表头单元格相对应的数据库字段名称
	private String headerKey;

	// 表头单元格在excel中第几行
	private int rowNum;

	// 当前单元格占用几行,默认一行
	private int occupyRowNum = 1;

	// 当前单元格占用几列，默认一列
	private int occupyColumnNum = 1;

	public String getHeaderLabel() {
		return headerLabel;
	}

	/**
	 * 表头单元格名称
	 * 
	 * @param headerLabel
	 */
	public void setHeaderLabel(String headerLabel) {
		this.headerLabel = headerLabel;
	}

	public String getHeaderKey() {
		return headerKey;
	}

	/**
	 * 与表头单元格相对应的数据库字段名称
	 * 
	 * @param headerKey
	 */
	public void setHeaderKey(String headerKey) {
		this.headerKey = headerKey;
	}

	public int getRowNum() {
		return rowNum;
	}

	/**
	 * 表头单元格在excel中第几行
	 * 
	 * @param rowNum
	 */
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public int getOccupyRowNum() {
		return occupyRowNum;
	}

	/**
	 * 当前单元格占用几行,默认一行
	 * 
	 * @param occupyRowNum
	 */
	public void setOccupyRowNum(int occupyRowNum) {
		this.occupyRowNum = occupyRowNum;
	}

	public int getOccupyColumnNum() {
		return occupyColumnNum;
	}

	/**
	 * 当前单元格占用几列,默认一列
	 * 
	 * @param occupyColumnNum
	 */
	public void setOccupyColumnNum(int occupyColumnNum) {
		this.occupyColumnNum = occupyColumnNum;
	}
}
