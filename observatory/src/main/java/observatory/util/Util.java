package observatory.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import observatory.internetnlAPI.config.RequestType;

public class Util
{
    public static File getResultsFolder(File resultsFolder, RequestType type)
    {
        return new File(resultsFolder, type.getType());
    }

    public static Workbook openWorkbook(File workbookFile) throws InvalidFormatException, IOException
    {
        return new XSSFWorkbook(workbookFile);
    }

    public static Workbook openWorkbook(InputStream workbookInputStream) throws InvalidFormatException, IOException
    {
        return new XSSFWorkbook(workbookInputStream);
    }

    public static String[] getDomainsList(Workbook domains, String sheetName, RequestType type)
    {
        Sheet sheet = domains.getSheet(sheetName);

        if (sheet == null)
            throw new IllegalArgumentException(
                String.format("There is no list named %s in the specified domains file.", sheetName)
            );

        Iterator<Row> rows = sheet.iterator();

        if (!rows.hasNext())
            throw new IllegalArgumentException(
                String.format("Invalid format for domains file. There is no column named " + type.getType(), sheetName)
            );

        Row row = rows.next();
        int column = -1;
        // find column
        for (Cell cell : row)
        {
            String value = null;
            try {
                value = cell.getStringCellValue();
            } catch (Exception e) {}

            if (value.equals(type.getType()))
                column = cell.getColumnIndex();
        }

        if (column == -1)
            throw new IllegalArgumentException(
                String.format("Invalid format for domains file. There is no column named " + type.getType(), sheetName)
            );

        List<String> domainsList = new LinkedList<>();
        while (rows.hasNext())
        {
            row = rows.next();
            Cell cell = row.getCell(column);
            String domain = null;
            if (cell != null && cell.getCellType() == CellType.STRING
                && !(domain = cell.getStringCellValue()).isEmpty())
                domainsList.add(domain);
        }
            
        if (domainsList.isEmpty())
            throw new IllegalArgumentException(
                String.format("Invalid format for domains file. There are no domains to test. " + type.getType(), sheetName)
            );
        
        return domainsList.toArray(new String[0]);
    }
}
