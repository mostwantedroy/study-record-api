package com.example.studyrecordapi.service;

import com.example.studyrecordapi.dto.CompleteVideoAnalysisRequest;
import com.example.studyrecordapi.dto.InitiateVideoAnalysisRequest;
import com.example.studyrecordapi.dto.VideoAnalysisResponse;
import com.example.studyrecordapi.entity.StudyRecord;
import com.example.studyrecordapi.repository.StudyRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyRecordService {

    private static final String VIDEO_ANALYSIS_API_URL = "http://ai.partimestudy.com/video-study-time-analyzer-v1";
    private static final String VIDEO_URL = "http://ai.partimestudy.com/study-record/video/";
    private static final int FRAME_INTERVAL = 30 * 60 * 5; // 30 fps X 60초 X 5분

    private final RestTemplate restTemplate;
    private final StudyRecordRepository studyRecordRepository;

    public void initiateVideoAnalysis(Long idx) {
        String videoUrl = VIDEO_URL + idx.toString();
        String callbackUrl = VIDEO_URL + idx.toString() + "/callback";

        StudyRecord studyRecord = studyRecordRepository.findById(idx).orElseThrow();

        int videoFrameCount = studyRecord.getVideoFrameCount();

        for (int startFrame = 0; startFrame < videoFrameCount; startFrame += FRAME_INTERVAL) {
            InitiateVideoAnalysisRequest request = InitiateVideoAnalysisRequest.builder()
                    .videoUrl(videoUrl)
                    .callbackUrl(callbackUrl)
                    .analysisStartFrame(startFrame)
                    .analysisLastFrame(startFrame + FRAME_INTERVAL)
                    .build();

            VideoAnalysisResponse response = askVideoAnalysis(request);

            reportMissingFrames(response, studyRecord);
        }
    }


    @Retryable(backoff = @Backoff(delay = 10000L))
    public VideoAnalysisResponse askVideoAnalysis(InitiateVideoAnalysisRequest request) {
        return restTemplate.postForEntity(VIDEO_ANALYSIS_API_URL, request, VideoAnalysisResponse.class).getBody();
    }

    @Transactional
    public void updateStudyTimeByPessimisticLock(Long idx, CompleteVideoAnalysisRequest request) {
        StudyRecord studyRecord = studyRecordRepository.findByIdWithPessimisticLock(idx);
        int videoFrameCount = studyRecord.getVideoFrameCount();
        videoFrameCount = videoFrameCount + request.getStudyMinute().get();
    }

    @Transactional
    public void updateStudyTime(Long idx, CompleteVideoAnalysisRequest request) {
        StudyRecord studyRecord = studyRecordRepository.findById(idx).orElseThrow();
        int videoFrameCount = studyRecord.getVideoFrameCount();
        videoFrameCount = videoFrameCount + request.getStudyMinute().get();
    }

    public void reportFailedAnalysis(Long idx) {
        log.error("Video Analysis Failed : studyRecord.idx = {}", idx);
    }

    private void reportMissingFrames(VideoAnalysisResponse response, StudyRecord studyRecord) {
        if (response.getMessage() == null) {
            log.error("Video Analysis Failed : studyRecord = {}", studyRecord);
        }
    }



}
