package com.example.studyrecordapi.repository;

import com.example.studyrecordapi.entity.StudyRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StudyRecord s where s.idx = :id")
    StudyRecord findByIdWithPessimisticLock(Long id);
}
