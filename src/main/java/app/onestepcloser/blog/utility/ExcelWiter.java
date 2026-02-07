package app.onestepcloser.blog.utility;

import app.onestepcloser.blog.annotation.ExcelColumn;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelWiter<T> {
	private final SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook());
	private final Map<String, Field> mapField = new HashMap<>();
	private final List<ExcelHeader> headers = new ArrayList<>();
	private String sheetName = "sheet0";
	private int ignoredRows = 0;
	private SXSSFSheet sheet;
	private CellStyle bodyCellStyle;
	private OutputStream outputStream;

	public ExcelWiter(Class<T> clazz) {
		super();
		parseEntity(clazz);
	}

	private void parseEntity(Class<T> clazz) {
		Field[] clazzFields = clazz.getDeclaredFields();
		for (Field field : clazzFields) {
			if (!field.isAnnotationPresent(ExcelColumn.class)) continue;
			ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
			field.setAccessible(true);
			mapField.put(field.getName(), field);
			headers.add(new ExcelHeader(field.getName(), annotation.title(), annotation.width(), annotation.column()));
		}
	}

	public void open() {
		sheet = workbook.createSheet(sheetName);
		sheet.trackAllColumnsForAutoSizing();
		initHeaderStyle();
		initBodyStyle();
	}

	public void write(List<T> datas)  {
		int currentRow = ignoredRows;
		for (T data : datas) {
			Row row = sheet.createRow(++currentRow);
			row.setHeight((short) (2 * sheet.getDefaultRowHeight()));
			for (ExcelHeader header : headers) {
				int index = header.getOrder() - 1;
				if (index < 0) continue;
				String columnName = header.getColumnName();
				if (mapField.containsKey(columnName)) {
					String value = null;
					try {
						Object valueField = mapField.get(columnName).get(data);
						if(valueField != null) {
							value = String.valueOf(valueField);
						}
					} catch (Exception e) {
						/*e.printStackTrace();*/
					}
					createCell(bodyCellStyle, row, index, value);
				}
			}
		}
	}

	public void close() throws Exception {
		if (outputStream == null) {
			throw new RuntimeException("Cannot find output");
		}
		workbook.write(outputStream);
		outputStream.flush();
		outputStream.close();
		workbook.close();
	}

	private void initHeaderStyle() {
		Font font = workbook.createFont();
		
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

		CreationHelper createHelper = workbook.getCreationHelper();

		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(font);
		headerCellStyle.setWrapText(true);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		Row headerRow = sheet.createRow(ignoredRows);

		headerRow.setHeight((short) (3 * sheet.getDefaultRowHeight()));

		for (ExcelHeader header : headers) {
			int index = header.getOrder() - 1;
			if (index < 0) continue;
			Cell cell = headerRow.createCell(index);
			cell.setCellStyle(headerCellStyle);
			cell.setCellValue(createHelper.createRichTextString(header.getTitle()));
			sheet.setColumnWidth(index, header.getColumnWidth() * 256);
		}
	}

	private void initBodyStyle() {
		Font font = workbook.createFont();
		
		font.setFontName("Times New Roman");
		
		bodyCellStyle = workbook.createCellStyle();
		bodyCellStyle.setFont(font);
		bodyCellStyle.setWrapText(true);
		bodyCellStyle.setAlignment(HorizontalAlignment.CENTER);
		
		bodyCellStyle.setBorderBottom(BorderStyle.THIN);
		bodyCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		bodyCellStyle.setBorderRight(BorderStyle.THIN);
		bodyCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		bodyCellStyle.setBorderTop(BorderStyle.THIN);
		bodyCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		bodyCellStyle.setBorderLeft(BorderStyle.THIN);
		bodyCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
	}

	private void createCell(CellStyle cellStyle, Row row, int i, String value) {
		Cell cell = row.createCell(i);
		cell.setCellValue(value == null ? Constants.EMPTY_STRING : value);
		cell.setCellStyle(cellStyle);
	}

	public void setIgnoredRows(int ignoredRows) {
		this.ignoredRows = ignoredRows;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	private static class ExcelHeader {

		private final String columnName;
		private final String title;
		private final int columnWidth;
		private final int order;

		public ExcelHeader(String columnName, String title, int columnWidth, int order) {
			super();
			this.columnName = columnName;
			this.title = title;
			this.columnWidth = columnWidth;
			this.order = order;
		}

		public String getColumnName() {
			return columnName;
		}

		public String getTitle() {
			return title;
		}

		public int getColumnWidth() {
			return columnWidth;
		}

		public int getOrder() {
			return order;
		}

	}
}
