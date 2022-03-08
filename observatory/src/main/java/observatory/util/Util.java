package observatory.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import observatory.internetnlAPI.config.RequestType;
import observatory.tests.index.Index;

public class Util
{
    public static File getCurrentWorkingDir()
    {
        return new File(System.getProperty("user.dir"));
    }

    public static Index getIndexIfExists(File indexFile) throws IOException, InvalidFormatException
    {
        Index index;
        if (indexFile.isFile()) // if index exists then load it
            index = Index.fromFile(indexFile);
        else // create a new index
            index = Index.empty(indexFile);

        return index;
    }

    /**
     * Get the results location of the specified type.
     * @param resultsFolder - The results root directory.
     * @param type - The type of results.
     * 
     * @return The results location of the specified type.
     */
    public static File getResultsFolder(File resultsFolder, RequestType type)
    {
        return new File(resultsFolder, type.getType());
    }

    /**
     * Open a workbook from a file.
     * @param workbookFile
     * @return The workbook.
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static Workbook openWorkbook(File workbookFile) throws IOException, InvalidFormatException
    {
        try {
            return new XSSFWorkbook(workbookFile);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidFormatException(e);
        }
    }

    /**
     * Open a workbook from a input stream (read-only).
     * 
     * @param workbookInputStream
     * @return The workbook.
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static Workbook openWorkbook(InputStream workbookInputStream) throws IOException, InvalidFormatException
    {
        try {
            return new XSSFWorkbook(workbookInputStream);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidFormatException(e);
        }
    }

    /**
     * Get a list of domains from the specified sheet name.
     * 
     * @param domains
     * @param sheetName
     * @param type
     * @return A list of domains.
     */
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
