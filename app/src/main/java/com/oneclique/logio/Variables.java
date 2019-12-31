package com.oneclique.logio;

public interface Variables {
    /*enum QuestionType
    {
        DIAGRAM("DIAGRAM"),
        DRAG_AND_DROP("DRAG_AND_DROP"),
        IDENTIFICATION("IDENTIFICATION"),
        MULTIPLECHOICE("MULTIPLE_CHOICE"),
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
        public static final String MULTIPLECHOICE = "MULTIPLE_CHOICE";
        public static final String TRUTH_TABLE = "TRUTH_TABLE";
    }

}
