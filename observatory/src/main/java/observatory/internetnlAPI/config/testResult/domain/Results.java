package observatory.internetnlAPI.config.testResult.domain;

import java.util.Map;

/**
 * Represents the results of all categories and tests of a domain.
 * 
 * @author Henrique Campos Ferreira
 */
public class Results
{
    private Map<Category, Result> categories;

    private Map<Test, Result> tests;

    private Map<CustomTest, Object> custom;

    /**
     * 
     */
    public Results() {
    }

    //#region Getters & Setters

    /**
     * @return the categories
     */
    public Map<Category, Result> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(Map<Category, Result> categories) {
        this.categories = categories;
    }

    /**
     * @return the tests
     */
    public Map<Test, Result> getTests() {
        return tests;
    }

    /**
     * @param tests the tests to set
     */
    public void setTests(Map<Test, Result> tests) {
        this.tests = tests;
    }

    /**
     * @return the custom
     */
    public Map<CustomTest, Object> getCustom() {
        return custom;
    }

    /**
     * @param custom the custom to set
     */
    public void setCustom(Map<CustomTest, Object> custom) {
        this.custom = custom;
    }

    //#endregion

}
