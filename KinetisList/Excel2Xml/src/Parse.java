import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Parse {
	public static void main(String[] args) {
		// Configuration
		String readFile = "C:/_ Kinetis - Master Product Update Report (PUR).xlsx";
		//String readFile = args[0];
		String output = "C:/test.xml";
		//String output = args[1];
		int TotalRow = 631;
		//int TotalRow = Integer.parseInt(args[2]);
		int StartRow = 8;
		//int StartRow =  Integer.parseInt(args[3]);
		Document document = DocumentHelper.createDocument();
		Boolean isEx10 = true;
		try {
			InputStream input = new FileInputStream(readFile);
			Workbook wb = null;
			if (isEx10)
				wb = new XSSFWorkbook(input);
			else
				wb = new HSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(1);
			// <FEATURES>
			System.out.println("========FEATURES========");
			Element rootElement = document.addElement("FEATURES");
			Iterator<Row> rows = sheet.rowIterator();
			String Famliy = "";
			Element familyElement = null;
			ArrayList<String> attrList = new ArrayList<String>();
			while (rows.hasNext()) {
				Row row = rows.next();
				Iterator<Cell> cells = row.cellIterator();
				if (row.getRowNum() ==  (StartRow - 2)) { // build header
					System.out.println("========HEADER========");
					Element headerElement = rootElement.addElement("HEADER");
					while (cells.hasNext()) {

						Element item = headerElement.addElement("ITEM");
						Cell cell = cells.next();
						item.addAttribute("Width", Integer.toString((int)sheet
								.getColumnWidthInPixels(cell.getColumnIndex())));
						//Parse title
						String title = cell.getStringCellValue().replaceAll("-", "").replaceAll("'", "").replace("(", "[").replace(")", "]");
						if(title.contains("*"))
							title = title.substring(0,cell.getStringCellValue().indexOf("*"));
						//if(title.contains("("))
							//title = title.substring(0,cell.getStringCellValue().indexOf("("));
						title = title.trim();
						item.addAttribute("Name", title);
						attrList.add(title);
						item.addAttribute("Data", title);
						Cell new_cell = sheet.getRow(StartRow - 1).getCell(cell.getColumnIndex());
						switch (new_cell.getCellType()) {
						case XSSFCell.CELL_TYPE_NUMERIC:
							if(String.valueOf(new_cell.getNumericCellValue()).contains(".0"))
								item.addAttribute("Type", "INTEGER");
							else
								item.addAttribute("Type", "DOUBLE");
							// System.out.println(cell.getNumericCellValue());
							break;
						case XSSFCell.CELL_TYPE_STRING:
							item.addAttribute("Type", "STRING");
							// System.out.println(cell.getStringCellValue());
							break;
						case XSSFCell.CELL_TYPE_BOOLEAN:
							item.addAttribute("Type", "STRING");
							// System.out.println(cell.getBooleanCellValue());
							break;
						case XSSFCell.CELL_TYPE_FORMULA:
							item.addAttribute("Type", "STRING");
							// System.out.println(cell.getCellFormula());
							break;
						default:
							item.addAttribute("Type", "STRING");
							// System.out.println("unsuported sell type");
							break;
						}
					}
				}
				if (row.getRowNum() > (StartRow - 2)
						&& row.getRowNum() < (TotalRow - 2)) {
					if (!Famliy.equals(row.getCell(1).getStringCellValue())) {
						System.out.println("========FAMLIY========");
						familyElement = rootElement.addElement("FAMILY");
						familyElement.addAttribute("Name", row.getCell(1)
								.getStringCellValue());
						Famliy = row.getCell(1).getStringCellValue();
					}
					Element Device = null;
					while (cells.hasNext()) {
						//System.out.println("========DEVICES========");
						Cell cell = cells.next();
						// System.out.println(cell.getColumnIndex());
						if (cell.getColumnIndex() == 0) {
							Device = familyElement.addElement("DEVICE");
							Device.addAttribute("Name",
									cell.getStringCellValue());
							Element Item = Device.addElement("ITEM");
							Item.addAttribute("Name",
									attrList.get(cell.getColumnIndex()));		
							Item.addAttribute("Data",
									cell.getStringCellValue());							
						}
						// System.out.println("Cell #" + cell.getColumnIndex());
						if (cell.getColumnIndex() > 1) {
							Element Item = Device.addElement("ITEM");
							Item.addAttribute("Name",
									attrList.get(cell.getColumnIndex()));
							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_NUMERIC:
								Item.addAttribute("Data", Double.toString(cell
										.getNumericCellValue()));
								break;
							case XSSFCell.CELL_TYPE_STRING:
								Item.addAttribute("Data",
										cell.getStringCellValue());
								break;
							case XSSFCell.CELL_TYPE_BOOLEAN:
								Item.addAttribute("Data", Boolean.toString(cell
										.getBooleanCellValue()));
								break;
							case XSSFCell.CELL_TYPE_FORMULA:
								Item.addAttribute("Data", cell.getCellFormula());
								// System.out.println(cell.getCellFormula());
								break;
							default:
								Item.addAttribute("Data",
										cell.getStringCellValue());
								// System.out.println("unsuported sell type");
								break;
							}
						}
					}
				}

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter writer = new XMLWriter(new FileOutputStream(new File(
					output)), format);
			writer.write(document);
			writer.close();
			System.out.println("========Write into XML========");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
