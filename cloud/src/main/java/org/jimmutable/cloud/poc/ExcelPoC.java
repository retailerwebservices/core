package org.jimmutable.cloud.poc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jimmutable.core.objects.common.USDMonetaryAmount;

/**
 * This is meant to demo the functionality of Apache POI excel writing. The
 * three main topics being handling our main data types, handling changing cell
 * color and font color, as well as handling borders on a cell.
 * 
 * @author avery.gonzales
 *
 */
public class ExcelPoC
{
	private static final File DEMO_BOOK_FILE_PATH = new File(System.getProperty("user.home") + "/jimmutable_dev/" + "demo_workbook.xlsx");

	public static void main( String[] args )
	{

		XSSFWorkbook workbook = new XSSFWorkbook();
		// Each sheet has a unique operation to accomplish as named
		createTypeSheet(workbook);
		createColoryBoiSheet(workbook);
		createBorderSheet(workbook);
		createMergedCellsSheet(workbook);
		autoSizeColumns(workbook);

		try
		{
			DEMO_BOOK_FILE_PATH.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(DEMO_BOOK_FILE_PATH);
			workbook.write(outputStream);
			workbook.close();
		}
		catch ( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}

		System.out.println("Done");
	}

	/*
	 * Demos how to set types for sheet. Important custom one being our
	 * USDMonetaryAmount
	 */
	public static void createTypeSheet( XSSFWorkbook workbook )
	{
		Date cur_date = new Date();
		Date prev_date = Date.from(Instant.now().minusMillis(TimeUnit.DAYS.toMillis(2)));
		Date next_date = Date.from(Instant.now().plusMillis(TimeUnit.DAYS.toMillis(2)));

		XSSFSheet sheet = workbook.createSheet("Datatypes Sheet");
		Object[][] datatypes = { { "Integer Value", "Float Values", "USDMonetaryAmount values", "String Values", "Date Values No Time", "Date Time Values" },

				{ new Integer(2), 12342.3243d, new USDMonetaryAmount(324l), "Avery", cur_date, cur_date }, { new Integer(4), 323.23d, new USDMonetaryAmount(32344l), "Testing", prev_date, prev_date }, { new Integer(8), 234.2d, new USDMonetaryAmount(124l), "Excel", next_date, next_date } };

		int rowNum = 0;
		for ( Object[] datatype : datatypes )
		{
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for ( Object field : datatype )
			{
				Cell cell = row.createCell(colNum++);
				if ( field instanceof String )
				{
					cell.setCellValue((String) field);
				}
				else if ( field instanceof Integer )
				{
					cell.setCellValue((Integer) field);
				}
				/*
				 * Probably do not want to use Float, Because floats can't be represented
				 * exactly as a binary number, the closest float to the true value is always not
				 * going to be exact which means we write a weird value out. Doubles don't have
				 * this problem.
				 */
				else if ( field instanceof Float )
				{
					CellStyle cs = workbook.createCellStyle();
					DataFormat format = workbook.createDataFormat();
					cs.setDataFormat(format.getFormat("#.##"));
					cell.setCellValue((Float) field);
					cell.setCellStyle(cs);
				}
				else if ( field instanceof Double )
				{
					// Two possible routes for rounding

					// one is keep the number and display the rounded number as this is written
					CellStyle cs = workbook.createCellStyle();
					cs.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("##.##"));
					// two is to just round the initial value passed in and then skip the formatting
					// step as it's not longer needed.
					double d = (double) field;
					double rounded_to_two_decimal = Math.round(d * 100.0) / 100.0;
					cell.setCellValue(rounded_to_two_decimal);
					cell.setCellStyle(cs);
				}
				else if ( field instanceof USDMonetaryAmount )
				{
					USDMonetaryAmount cur_field = (USDMonetaryAmount) field;
					CellStyle cellStyle = workbook.createCellStyle();
					cell.setCellValue(cur_field.getSimpleAmountInCents());
					cellStyle.setDataFormat((short) 7);
					cell.setCellStyle(cellStyle);

				}
				else if ( field instanceof Date )
				{
					cell.setCellValue((Date) field);
					CellStyle cell_style = workbook.createCellStyle();
					CreationHelper createHelper = workbook.getCreationHelper();
					// Would create something here in the wrapper to show only date vs date time
					cell_style.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
					cell.setCellValue((Date) field);
					cell.setCellStyle(cell_style);
				}
			}
		}
	}

	/*
	 * Demos how to set colors for cells in a sheet. The background as well as the
	 * text itself.
	 */
	public static void createColoryBoiSheet( XSSFWorkbook workbook )
	{

		XSSFSheet sheet = workbook.createSheet("ColoryBois Sheet");
		Object[][] datatypes = { { "Coloring Cells & Font!!" },

				{ "Cell" }, { "With" }, { "Aqua" } };

		int rowNum = 0;
		for ( Object[] datatype : datatypes )
		{
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for ( Object field : datatype )
			{
				Cell cell = row.createCell(colNum++);
				if ( field instanceof String )
				{
					CellStyle cs = workbook.createCellStyle();
					// Change this to what ever you want the cell to be
					cs.setFillForegroundColor(IndexedColors.AQUA.getIndex());
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);

					// Change this to be whatever you want the font color to be
					Font font = workbook.createFont();
					font.setColor(IndexedColors.RED.getIndex());
					cs.setFont(font);

					cell.setCellValue((String) field);
					cell.setCellStyle(cs);
				}
			}
		}
	}

	/*
	 * Demos how to set borders on a cell. Border on just the top, border on just
	 * the bottom, double border, and thick border
	 */
	public static void createBorderSheet( XSSFWorkbook workbook )
	{
		final String top_only = "Top Only";
		final String bottom_only = "Bottom Only";
		final String double_border = "Double Border";
		final String thick_border = "Thick Border";

		XSSFSheet sheet = workbook.createSheet("Border Sheet");
		Object[][] datatypes = { { "Border Cells" },

				{ top_only }, { "" }, { bottom_only }, { "" }, { double_border }, { "" }, { thick_border } };

		int rowNum = 0;
		for ( Object[] datatype : datatypes )
		{
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for ( Object field : datatype )
			{
				Cell cell = row.createCell(colNum++);
				if ( field instanceof String )
				{
					CellStyle cs = workbook.createCellStyle();

					if ( field.equals(top_only) )
					{
						cs.setBorderTop(BorderStyle.MEDIUM);
					}
					else if ( field.equals(bottom_only) )
					{
						cs.setBorderBottom(BorderStyle.MEDIUM);
					}
					else if ( field.equals(double_border) )
					{
						cs.setBorderBottom(BorderStyle.DOUBLE);
						cs.setBorderTop(BorderStyle.DOUBLE);
					}
					else if ( field.equals(thick_border) )
					{
						cs.setBorderBottom(BorderStyle.THICK);
						cs.setBorderTop(BorderStyle.THICK);
						cs.setBorderLeft(BorderStyle.THICK);
						cs.setBorderRight(BorderStyle.THICK);
					}

					cell.setCellValue((String) field);
					cell.setCellStyle(cs);
				}
			}
		}
	}

	/*
	 * Demos how to merge cells together
	 */
	public static void createMergedCellsSheet( XSSFWorkbook workbook )
	{
		final String normal_merge = "Merge Cells. This is a really long header since we have lots of columns!";
		final String merge_with_styling = "Merged Cells with Style! We are still writing stuff now!";

		XSSFSheet sheet = workbook.createSheet("Merged Cells Sheet");

		Object[][] datatypes = { { normal_merge },

				{ "" }, { merge_with_styling }, { "" }, { "" }, { "Yes" } };
		int rowNum = 0;
		for ( Object[] datatype : datatypes )
		{
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for ( Object field : datatype )
			{
				Cell cell = row.createCell(colNum++);
				if ( field instanceof String )
				{
					cell.setCellValue((String) field);
				}
			}
		}

		// will merge from A1 to F1
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
		// will merge from A3 to F2
		CellRangeAddress pretty_cell_range = new CellRangeAddress(2, 2, 0, 6);
		sheet.addMergedRegion(pretty_cell_range);

		// You have to specify the border using this tool otherwise it would be hard to
		// track while writing the rest of the sheet
		// This also has to be done after writing all cells otherwise it will be
		// overwritten. Only matters when styling a range of cells.
		RegionUtil.setBorderBottom(BorderStyle.THICK, pretty_cell_range, sheet);
		RegionUtil.setBorderLeft(BorderStyle.THICK, pretty_cell_range, sheet);
		RegionUtil.setBorderTop(BorderStyle.THICK, pretty_cell_range, sheet);
		RegionUtil.setBorderRight(BorderStyle.THICK, pretty_cell_range, sheet);
	}

	/*
	 * Shows how you can auto resize columns to fit a cell. This one only does it
	 * for the column headers. Note that this is a slow process when files get
	 * large.
	 */
	public static void autoSizeColumns( Workbook workbook )
	{
		int numberOfSheets = workbook.getNumberOfSheets();
		for ( int i = 0; i < numberOfSheets; i++ )
		{
			Sheet sheet = workbook.getSheetAt(i);
			if ( sheet.getPhysicalNumberOfRows() > 0 )
			{
				Row row = sheet.getRow(0);
				Iterator<Cell> cellIterator = row.cellIterator();
				while ( cellIterator.hasNext() )
				{
					Cell cell = cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					sheet.autoSizeColumn(columnIndex);
				}
			}
		}
	}

}
