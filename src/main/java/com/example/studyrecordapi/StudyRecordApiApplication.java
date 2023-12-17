package com.example.studyrecordapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class StudyRecordApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyRecordApiApplication.class, args);
    }

}
