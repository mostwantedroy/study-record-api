package com.example.studyrecordapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InitiateVideoAnalysisRequest {

    private String callbackUrl;

    private String videoUrl;

    private int analysisStartFrame;

    private int analysisLastFrame;

    @Builder
    public InitiateVideoAnalysisRequest(String callbackUrl, String videoUrl, int analysisStartFrame, int analysisLastFrame) {
        this.callbackUrl = callbackUrl;
        this.videoUrl = videoUrl;
        this.analysisStartFrame = analysisStartFrame;
        this.analysisLastFrame = analysisLastFrame;
    }
}
