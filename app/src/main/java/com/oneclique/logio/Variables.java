package com.oneclique.logio;

public interface Variables {
    /*enum QuestionType
    {
        DIAGRAM("DIAGRAM"),
        DRAG_AND_DROP("DRAG_AND_DROP"),
        IDENTIFICATION("IDENTIFICATION"),
        MULTIPLE_CHOICE("MULTIPLE_CHOICE"),
        TRUTH_TABLE("TRUTH_TABLE");
        private String value;

        public String getValue()
        {
            return this.value;
        }

        QuestionType(String value)
        {
            this.value = value;
        }
    }*/

    class QuestionType{
        public static final String DIAGRAM ="DIAGRAM";
        public static final String DRAG_AND_DROP = "DRAG_AND_DROP";
        public static final String IDENTIFICATION = "IDENTIFICATION";
        public static final String MULTIPLE_CHOICE = "MULTIPLE_CHOICE";
        public static final String TRUTH_TABLE = "TRUTH_TABLE";
    }

    class QuestionCategory{
        public static final String AND ="AND";
        public static final String NOT = "NOT";
        public static final String OR = "OR";
        public static final String XNOR = "XNOR";
        public static final String XOR = "XOR";
        public static final String INTEGRATED_CIRCUIT = "Integrated Circuit";
    }

}
