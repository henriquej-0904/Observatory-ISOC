package observatory;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Test;

import observatory.internetnlAPI.config.RequestType;
import observatory.internetnlAPI.config.results.TestResult;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    // private static class TestClass
    // {
    //     private String o;

    //     private Test1 ahaha;

    //     public TestClass() {}

    //     /**
    //      * @return the o
    //      */
    //     public String getO() {
    //         return o;
    //     }

    //     /**
    //      * @param o the o to set
    //      */
    //     public void setO(String o) {
    //         this.o = o;
    //     }

    //     /**
    //      * @return the ahaha
    //      */
    //     public Test1 getAhaha() {
    //         return ahaha;
    //     }

    //     /**
    //      * @param ahaha the ahaha to set
    //      */
    //     public void setAhaha(Test1 ahaha) {
    //         this.ahaha = ahaha;
    //     }
        
    // }

    // private static enum Test1
    // {
    //     VALUE ("value"),
    //     VALUE2 ("value2");

    //     private String value;

    //     private Test1(String value)
    //     {
    //         this.value = value;
    //     }

    //     @JsonValue
    //     public String getValue()
    //     {
    //         return value;
    //     }
    // }

    // /**
    //  * Rigorous Test :-)
    //  */
    // @Test
    // public void testJsonEnum()
    // {
    //     TestClass t = new TestClass();
    //     t.setO("Teste");
    //     t.setAhaha(Test1.VALUE2);

    //     ObjectMapper mapper = new ObjectMapper();

    //     try {
    //         String json = mapper.writeValueAsString(t);
    //         System.out.println(json);

    //         TestClass t2 = mapper.readValue(json, TestClass.class);

    //         System.out.println(mapper.writeValueAsString(t2));

    //         assertTrue(t.getAhaha() == t2.getAhaha());
    //     } catch (Exception e) {
    //         assertTrue(false);
    //     }
        

    // }

    // @Test
    // public void testInvalidEnumValueInJson()
    // {
    //     final String invalidJsonEnumValue = "{\"o\":\"Teste\",\"ahaha\":\"value3\"}";
    //     try {
    //         TestClass t = new ObjectMapper().readValue(invalidJsonEnumValue, TestClass.class);
    //         assertTrue(false);
    //     } catch (Exception e) {
    //         System.out.println(e.getMessage());
    //         assertTrue(true);
    //     }
    // }

    // private static class TestMap
    // {
    //     private Map<String, Integer> domains;

    //     /**
    //      * 
    //      */
    //     public TestMap() {
    //     }

    //     /**
    //      * @return the domains
    //      */
    //     public Map<String, Integer> getDomains() {
    //         return domains;
    //     }

    //     /**
    //      * @param domains the domains to set
    //      */
    //     public void setDomains(Map<String, Integer> domains) {
    //         this.domains = domains;
    //     }

        
    // }

    // @Test
    // public void testMapInJson()
    // {
    //     TestMap t = new TestMap();
    //     t.setDomains(Map.of("test1", 1, "test2", 2));

    //     try {
    //         ObjectMapper mapper = new ObjectMapper();

    //         String json = mapper.writeValueAsString(t);
    //         System.out.println(json);

    //         TestMap t2 = mapper.readValue(json, TestMap.class);
    //         assertTrue(true);
    //     } catch (Exception e) {
    //         assertTrue(false);
    //     }
    // }

    // @Test
    // public void testEnum()
    // {
    //     observatory.internetnlAPI.config.results.domain.Test[] all =
    //         observatory.internetnlAPI.config.results.domain.Test.values();

    //     List<observatory.internetnlAPI.config.results.domain.Test> web =
    //         observatory.internetnlAPI.config.results.domain.Test.values(RequestType.WEB);

    //     List<observatory.internetnlAPI.config.results.domain.Test> mail =
    //         observatory.internetnlAPI.config.results.domain.Test.values(RequestType.MAIL);

    //     System.out.println("Web:\n" + web.toString());
    //     System.out.println();
    //     System.out.println("Mail:\n" + mail.toString());
    //     System.out.println();

    //     List<observatory.internetnlAPI.config.results.domain.Test> allList =
    //         new LinkedList<>();

    //     allList.addAll(web);
    //     allList.addAll(mail);

    //     Assert.assertArrayEquals(all, allList.toArray());
    // }

    @Test
    public void testJsonTestResults()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();

            TestResult webResult = mapper.readValue(new File("examples/get.json"), TestResult.class);
            TestResult mailResult = mapper.readValue(new File("examples/get_email.json"), TestResult.class);

            System.out.println("web:\n");
            System.out.println(mapper.writeValueAsString(webResult));
            System.out.println();
            System.out.println("mail:\n");
            System.out.println(mapper.writeValueAsString(mailResult));

            assertTrue(true);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        
    }
}
