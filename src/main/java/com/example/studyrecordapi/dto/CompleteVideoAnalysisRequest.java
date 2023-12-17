package com.example.studyrecordapi.dto;

import lombok.Getter;

import java.util.Optional;

@Getter
public class CompleteVideoAnalysisRequest {
    private boolean success;

    private Optional<Integer> studyMinute;

    private Optional<String> detailResultFileUrl;

    private Optional<String> errorMessage;
}
