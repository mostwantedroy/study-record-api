package com.example.studyrecordapi.controller;

import com.example.studyrecordapi.dto.CompleteVideoAnalysisRequest;
import com.example.studyrecordapi.facade.RedissonLockFacade;
import com.example.studyrecordapi.service.StudyRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyRecordController {

    private final RedissonLockFacade redissonLockFacade;
    private final StudyRecordService studyRecordService;

    @PostMapping("/video/{idx}/analysis")
    public void initiateVideoAnalysis(@PathVariable Long idx) {
        studyRecordService.initiateVideoAnalysis(idx);
    }

    @PostMapping("/video/{idx}/callback")
    public void completeVideoAnalysis(@PathVariable Long idx, @RequestBody CompleteVideoAnalysisRequest request) {
        if (!request.isSuccess()) {
            studyRecordService.reportFailedAnalysis(idx);
            return;
        }

        studyRecordService.updateStudyTimeByPessimisticLock(idx, request);

        // DB 부하가 커질 경우
//        redissonLockFacade.updateStudyTimeByDistributedLock(idx, request);
    }

}
