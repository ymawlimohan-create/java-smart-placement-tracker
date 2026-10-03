package com.placement.repository;

import com.placement.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByStudentId(Long studentId);

    boolean existsByStudentIdAndDriveId(Long studentId, Long driveId);
}