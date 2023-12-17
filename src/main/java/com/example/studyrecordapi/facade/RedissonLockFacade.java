package com.example.studyrecordapi.facade;

import com.example.studyrecordapi.dto.CompleteVideoAnalysisRequest;
import com.example.studyrecordapi.service.StudyRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockFacade {

    private final RedissonClient redissonClient;
    private final StudyRecordService studyRecordService;

    public void updateStudyTimeByDistributedLock(Long idx, CompleteVideoAnalysisRequest request) {
        String studyRecordKey = "STUDY_RECORD:" + idx.toString();
        RLock lock = redissonClient.getLock(studyRecordKey);

        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                log.error("LOCK ACQUISITION FAILED");
                return;
            }

            studyRecordService.updateStudyTime(idx, request);
        } catch (Exception e) {
            log.error("UPDATE STUDY TIME FAILED");
        } finally {
            lock.unlock();
        }
    }


}
