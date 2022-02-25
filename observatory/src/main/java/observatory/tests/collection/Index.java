package observatory.tests.collection;

import java.util.Map;

class Index
{
    private Map<String, ListInfo> index;

    /**
     * 
     */
    public Index() {
    }

    

    /**
     * @param index
     */
    public Index(Map<String, ListInfo> index) {
        this.index = index;
    }



    /**
     * @return the index
     */
    public Map<String, ListInfo> getIndex() {
        return index;
    }



    /**
     * @param index the index to set
     */
    public void setIndex(Map<String, ListInfo> index) {
        this.index = index;
    }
}
