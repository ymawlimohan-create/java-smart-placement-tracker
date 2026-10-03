package com.placement.repository;

import com.placement.model.Drive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriveRepository extends JpaRepository<Drive, Long> {

    List<Drive> findByMinCgpaLessThanEqual(double cgpa);
}