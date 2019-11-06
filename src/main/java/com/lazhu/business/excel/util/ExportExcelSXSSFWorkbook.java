package com.lazhu.business.excel.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import com.lazhu.ecp.utils.StrUtils;

/**
 * Date: 2019年02月27日 
 * 
 * @author hz
 */
public class ExportExcelSXSSFWorkbook {
	// 显示的导出表的标题
	private String title;
	// 导出表的列名
	private String[] rowName;

	private List<Object[]> dataList = new ArrayList<Object[]>();

	HttpServletResponse response;

	// 构造方法，传入要导出的数据
	public ExportExcelSXSSFWorkbook(String title, String[] rowName,
			List<Object[]> dataList) {
		this.dataList = dataList;
		this.rowName = rowName;
		this.title = title;
	}

	
	//导出数据
	public void export(OutputStream out) throws Exception {
		try {
			// 获取SXSSFWorkbook实例
			// 在内存中保持100行，超过100行将被刷新到磁盘
			SXSSFWorkbook workbook = new SXSSFWorkbook(100);// 创建工作簿对象
			Sheet sheet = workbook.createSheet(title);// 创建工作表
			// 冻结最左边的两列、冻结最上面的一行
			// 即：滚动横向滚动条时，左边的第一、二列固定不动;滚动纵向滚动条时，上面的第一行固定不动。
			//sheet.createFreezePane(2, 1);
			sheet.createFreezePane(0, 1);
			// 设置并获取到需要的样式
			XSSFCellStyle styleHeader = getAndSetXSSFCellStyleHeader(workbook);
			XSSFCellStyle styleOne = getAndSetXSSFCellStyleOne(workbook);
			// 定义所需列数
			int columnNum = rowName.length;
			Row rowRowName = sheet.createRow(0); // 在索引2的位置创建行(最顶端的行开始的第二行)
			// 将列头设置到sheet的单元格中
			for (int n = 0; n < columnNum; n++) {
				Cell cellRowName = rowRowName.createCell(n); // 创建列头对应个数的单元格
				cellRowName.setCellType(XSSFCell.CELL_TYPE_STRING); // 设置列头单元格的数据类型
				XSSFRichTextString text = new XSSFRichTextString(rowName[n]);
				cellRowName.setCellValue(text); // 设置列头单元格的值
				cellRowName.setCellStyle(styleHeader); // 设置列头单元格样式
			}
			// 将查询出的数据设置到sheet对应的单元格中
			for (int i = 0; i < dataList.size(); i++) {
				Object[] obj = dataList.get(i);// 遍历每个对象
				Row row = sheet.createRow(i + 1);// 创建所需的行数（从第二行开始写数据）
				for (int j = 0; j < obj.length; j++) {
					Cell cell = null; // 设置单元格的数据类型
					if (j == 0) {
						cell = row.createCell(j, XSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue(i + 1);
					} else {
						cell = row.createCell(j, XSSFCell.CELL_TYPE_STRING);
						if (!"".equals(obj[j]) && obj[j] != null) {
							/*if (obj[j].toString().matches("^(-?\\d+)(\\.\\d+)?$")) {
								cell = row.createCell(j, XSSFCell.CELL_TYPE_NUMERIC);
								cell.setCellValue(StrUtils.toDouble(obj[j])); // 设置单元格的值
							} else {*/
								cell.setCellValue(obj[j].toString()); // 设置单元格的值
							//}
						}
					}
					cell.setCellStyle(styleOne); // 设置单元格样式
				}
			}
			// 让列宽随着导出的列长自动适应
			for (int colNum = 0; colNum < columnNum; colNum++) {
				int columnWidth = sheet.getColumnWidth(colNum) / 256;
				for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
					Row currentRow;
					// 当前行未被使用过
					if (sheet.getRow(rowNum) == null) {
						currentRow = sheet.createRow(rowNum);
					} else {
						currentRow = sheet.getRow(rowNum);
					}
					if (currentRow.getCell(colNum) != null) {
						Cell currentCell = currentRow.getCell(colNum);
						if (currentCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							int length = 0;
							try {
								if (null != currentCell && null != currentCell.getStringCellValue()) {
									length = currentCell.getStringCellValue()
											.getBytes().length;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (columnWidth < length) {
								columnWidth = length;
							}
						}
					}
				}
				if (colNum == 0) {
					sheet.setColumnWidth(colNum, (columnWidth - 2) * 256);
				} else {
					if (columnWidth <= 30) {
						sheet.setColumnWidth(colNum, (columnWidth + 4) * 256);
					} else {
						sheet.setColumnWidth(colNum, 50 * 256);
					}
				}
			}
			if (workbook != null) {
				try {
					workbook.write(out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	
	/**
     * 设置sheet
     */
    @SuppressWarnings("unused")
	private void setSheet(Sheet sheet) {
        // 设置各列宽度(单位为:字符宽度的1/256)
        sheet.setColumnWidth(0, 32 * 256);
        sheet.setColumnWidth(1, 32 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 20 * 256);
        sheet.setColumnWidth(4, 20 * 256);
        sheet.setColumnWidth(5, 20 * 256);
        sheet.setColumnWidth(6, 20 * 256);
        sheet.setColumnWidth(7, 20 * 256);
        sheet.setColumnWidth(8, 20 * 256);
        sheet.setColumnWidth(9, 20 * 256);
        sheet.setColumnWidth(10, 32 * 256);
    }
 
    /**
     * 获取并设置header样式
     */
    private XSSFCellStyle getAndSetXSSFCellStyleHeader(SXSSFWorkbook sxssfWorkbook) {
        XSSFCellStyle xssfCellStyle = (XSSFCellStyle) sxssfWorkbook.createCellStyle();
        Font font = sxssfWorkbook.createFont();
        // 字体大小
        font.setFontHeightInPoints((short) 14);
        // 字体粗细
        font.setBoldweight((short) 20);
        // 将字体应用到样式上面
        xssfCellStyle.setFont(font);
        // 是否自动换行
        xssfCellStyle.setWrapText(false);
        // 水平居中
        xssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 垂直居中
        xssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return xssfCellStyle;
    }
 
    /**
     * 获取并设置样式一
     */
    private XSSFCellStyle getAndSetXSSFCellStyleOne(SXSSFWorkbook sxssfWorkbook) {
        XSSFCellStyle xssfCellStyle = (XSSFCellStyle) sxssfWorkbook.createCellStyle();
        XSSFDataFormat format = (XSSFDataFormat)sxssfWorkbook.createDataFormat();
        // 是否自动换行
        xssfCellStyle.setWrapText(false);
        // 水平居中
        xssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 垂直居中
        xssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 前景颜色
        xssfCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        xssfCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        // 边框
        xssfCellStyle.setBorderBottom(BorderStyle.THIN);
        xssfCellStyle.setBorderRight(BorderStyle.THIN);
        xssfCellStyle.setBorderTop(BorderStyle.THIN);
        xssfCellStyle.setBorderLeft(BorderStyle.THIN);
        xssfCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        xssfCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        xssfCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        xssfCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        // 防止数字过长,excel导出后,显示为科学计数法,如:防止8615192053888被显示为8.61519E+12
        xssfCellStyle.setDataFormat(format.getFormat("0"));
        return xssfCellStyle;
    }
 
    /**
     * 获取并设置样式二
     */
    @SuppressWarnings("unused")
	private XSSFCellStyle getAndSetXSSFCellStyleTwo(SXSSFWorkbook sxssfWorkbook) {
        XSSFCellStyle xssfCellStyle = (XSSFCellStyle) sxssfWorkbook.createCellStyle();
        XSSFDataFormat format = (XSSFDataFormat)sxssfWorkbook.createDataFormat();
        // 是否自动换行
        xssfCellStyle.setWrapText(false);
        // 水平居中
        xssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 边框
        xssfCellStyle.setBorderBottom(BorderStyle.THIN);
        xssfCellStyle.setBorderRight(BorderStyle.THIN);
        xssfCellStyle.setBorderTop(BorderStyle.THIN);
        xssfCellStyle.setBorderLeft(BorderStyle.THIN);
        xssfCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        xssfCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        xssfCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        xssfCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        // 垂直居中
        xssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 防止数字过长,excel导出后,显示为科学计数法,如:防止8615192053888被显示为8.61519E+12
        xssfCellStyle.setDataFormat(format.getFormat("0"));
        return xssfCellStyle;
    }

}
