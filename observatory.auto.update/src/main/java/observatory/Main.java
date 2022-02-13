package observatory;

import java.io.File;

import com.gembox.spreadsheet.SpreadsheetInfo;

import observatory.internetnlAPI.InternetnlAPI;
import observatory.internetnlAPI.InternetnlAPIWithPythonScripts;
import observatory.update.TestDomains;

public class Main {
    public static void main(String[] args)
    {
        // Init Excel Library
        SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
        
        try
        (
            InternetnlAPI api =
                new InternetnlAPIWithPythonScripts(new File("internet.nl-python-scripts/batch.py"));
        )
        {
            TestDomains tests = new TestDomains(new File("config/domains.xlsx"), new File("results"), api);
            tests.Start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
