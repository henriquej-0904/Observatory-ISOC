package observatory.tests.collection;

public class ListInfo
{
    /**
         * The id of the test.
         */
        private String testId;

        /**
         * If it is ok or an error occurred.
         */
        private boolean ok;

        /**
         * 
         */
        public ListInfo() {
        }

        /**
         * @param testId
         * @param ok
         */
        public ListInfo(String testId, boolean ok) {
            this.testId = testId;
            this.ok = ok;
        }

        public ListInfo(ListInfo info) {
            this.testId = info.testId;
            this.ok = info.ok;
        }

        /**
         * @return The id of the test.
         */
        public String getTestId() {
            return testId;
        }

        /**
         * @param testId The id of the test.
         */
        public void setTestId(String testId) {
            this.testId = testId;
        }

        /**
         * @return If it is ok or an error occurred.
         */
        public boolean getOk() {
            return ok;
        }

        /**
         * @param ok - If it is ok or an error occurred.
         */
        public void setOk(boolean ok) {
            this.ok = ok;
        }
}
