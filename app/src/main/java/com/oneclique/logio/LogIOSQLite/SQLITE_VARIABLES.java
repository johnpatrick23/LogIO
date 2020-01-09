package com.oneclique.logio.LogIOSQLite;

public class SQLITE_VARIABLES {

    final static String DB_NAME = "logio.db";

    public static final int DB_VERSION = 3;

    public final static class Table_Users{
        public final static String DB_TABLE_NAME = "tbl_users";
        public final static String DB_COL_ID = "a_id";
        public final static String DB_COL_USERNAME = "a_username";
        public final static String DB_COL_LAST_USED = "a_last_used";
        public final static String DB_COL_LEVEL_1_STARS = "a_level_1_stars";
        public final static String DB_COL_LEVEL_2_STARS = "a_level_2_stars";
        public final static String DB_COL_LEVEL_3_STARS = "a_level_3_stars";
        public final static String DB_COL_LEVEL_4_STARS = "a_level_4_stars";
        public final static String DB_COL_LEVEL_5_STARS = "a_level_5_stars";
        public final static String DB_COL_LEVEL_6_STARS = "a_level_6_stars";
        public final static String DB_COL_LEVEL_7_STARS = "a_level_7_stars";
        public static final String DB_COL_NUMBER_OF_ACCESS = "a_number_of_access";
        public static final String DB_COL_HINT = "a_hint";
        public static final String DB_COL_ADD_TIME = "a_add_time";
        public static final String DB_COL_SLOW_TIME = "a_slow_time";
    }

    public final static class Table_User_Achievements{
        public final static String DB_TABLE_NAME = "tbl_user_achievements";
        public final static String DB_COL_ID = "a_id";
        public final static String DB_COL_LEVEL = "a_level";
        public final static String DB_COL_STARS = "a_stars";
        public final static String DB_COL_TIME_FINISHED= "a_time_finished";
        public final static String DB_COL_DESCRIPTION = "a_description";
        public static final String DB_COL_NUMBER_OF_TRIES = "a_number_of_tries";
        public static final String DB_COL_USERNAME = "a_username";
    }

    public final static class Table_User_Logs{
        public final static String DB_TABLE_NAME = "tbl_user_logs";
        public final static String DB_COL_ID = "a_id";
        public final static String DB_COL_USERNAME = "a_username";
        public final static String DB_COL_LEVEL = "a_level";
        public final static String DB_COL_POPUP_MESSAGE_TIME = "a_popup_message_time";
        public final static String DB_COL_STAR = "a_star";
        public static final String DB_COL_AVERAGE_TIME = "a_average_time";
    }

    public final static class Table_Questions{
        public final static String DB_TABLE_NAME = "tbl_questions";
        public final static String DB_COL_ID = "a_id";
        public final static String DB_COL_LEVEL = "a_level";
        public final static String DB_COL_QUESTION_TYPE = "a_questiontype";
        public final static String DB_COL_QUESTION = "a_question";
        public final static String DB_COL_CHOICES = "a_choices";
        public final static String DB_COL_ANSWER = "a_answer";
        public final static String DB_COL_TIME_DURATION = "a_timeduration";
        public final static String DB_COL_CATEGORY = "a_category";
        public final static String DB_COL_INSTRUCTION = "a_instruction";
    }

}
