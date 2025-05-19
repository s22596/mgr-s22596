package org.mgr.mgr_s22596;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // PJATK
    public static final String PJATK_STUDENT_NUMBER = "s22596";

    public static final String PJATK_BASIC_PATH = "\\\\win-zet.pjwstk.edu.pl\\";
    public static final String PJATK_PUBLIC_DRIVE_NAME = "public";
    public static final String PJATK_USER_DISK_NAME = String.format("Users\\%s", PJATK_STUDENT_NUMBER);

    // MongoDB
    public static final String MONGO_DB_ADDRESS = "mongodb://localhost:27017";
    public static final String MONGO_DB_NAME = String.format("mgr-%s", PJATK_STUDENT_NUMBER);
    public static final String MONGO_DB_NAME_TEST = String.format("test");
}
