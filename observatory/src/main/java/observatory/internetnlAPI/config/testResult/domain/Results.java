package observatory.internetnlAPI.config.testResult.domain;

import java.util.EnumMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import observatory.util.Util;

/**
 * Represents the results of all categories and tests of a domain.
 * 
 * @author Henrique Campos Ferreira
 */
public class Results
{
    private Map<String, Result> categories;

    private Map<String, Result> tests;

    private Map<String, Object> custom;

    private EnumMap<Category, Result> categoriesEnumMap;

    private EnumMap<Test, Result> testsEnumMap;

    private EnumMap<CustomTest, Object> customEnumMap;

    /**
     * 
     */
    public Results() {
    }


    //#region Getters & Setters

    /**
     * @return the categories
     */
    public Map<String, Result> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(Map<String, Result> categories) {
        this.categories = categories;
    }

    /**
     * @return the tests
     */
    public Map<String, Result> getTests() {
        return tests;
    }

    /**
     * @param tests the tests to set
     */
    public void setTests(Map<String, Result> tests) {
        this.tests = tests;
    }

    /**
     * @return the custom
     */
    public Map<String, Object> getCustom() {
        return custom;
    }

    /**
     * @param custom the custom to set
     */
    public void setCustom(Map<String, Object> custom) {
        this.custom = custom;
    }



    /**
     * @return the categories converted to an Enum Map
     */
    @JsonIgnore
    public EnumMap<Category, Result> getCategoriesEnum() {

        if (categoriesEnumMap == null)
            categoriesEnumMap = Util.toEnumMap(categories,
                Category::getEnumValue, Category.class);

        return categoriesEnumMap;
    }

    /**
     * @return the tests converted to an Enum Map
     */
    @JsonIgnore
    public EnumMap<Test, Result> getTestsEnum() {

        if (testsEnumMap == null)
            testsEnumMap = Util.toEnumMap(tests,
                Test::getEnumValue, Test.class);

        return testsEnumMap;
    }

    /**
     * @return the custom converted to an Enum Map
     */
    @JsonIgnore
    public EnumMap<CustomTest, Object> getCustomEnum() {

        if (customEnumMap == null)
            customEnumMap = Util.toEnumMap(custom,
                CustomTest::getEnumValue, CustomTest.class);

        return customEnumMap;
    }

    //#endregion

}
