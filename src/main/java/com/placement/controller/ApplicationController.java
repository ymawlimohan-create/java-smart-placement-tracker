package com.placement.controller;

import com.placement.model.Drive;
import com.placement.model.JobApplication;
import com.placement.model.Student;
import com.placement.repository.ApplicationRepository;
import com.placement.repository.DriveRepository;
import com.placement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private static final Set<String> VALID_STATUSES =
            Set.of("APPLIED", "SHORTLISTED", "SELECTED", "REJECTED");

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final DriveRepository driveRepository;

    public ApplicationController(
            ApplicationRepository applicationRepository,
            StudentRepository studentRepository,
            DriveRepository driveRepository) {

        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.driveRepository = driveRepository;
    }

    @PostMapping
    public ResponseEntity<?> apply(
            @RequestParam Long studentId,
            @RequestParam Long driveId) {

        Student student = studentRepository
                .findById(studentId)
                .orElse(null);

        if (student == null) {
            return error(HttpStatus.NOT_FOUND,
                    "Student not found");
        }

        Drive drive = driveRepository
                .findById(driveId)
                .orElse(null);

        if (drive == null) {
            return error(HttpStatus.NOT_FOUND,
                    "Drive not found");
        }

        if (student.getCgpa() < drive.getMinCgpa()) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "Not eligible: CGPA below "
                            + drive.getMinCgpa());
        }

        if (applicationRepository
                .existsByStudentIdAndDriveId(studentId, driveId)) {

            return error(HttpStatus.CONFLICT,
                    "Already applied");
        }

        JobApplication application = new JobApplication();

        application.setStudentId(studentId);
        application.setDriveId(driveId);
        application.setStatus("APPLIED");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(applicationRepository.save(application));
    }

    @GetMapping("/student/{studentId}")
    public List<JobApplication> getApplicationsByStudent(
            @PathVariable Long studentId) {

        return applicationRepository
                .findByStudentId(studentId);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        String newStatus = status.trim().toUpperCase();

        if (!VALID_STATUSES.contains(newStatus)) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "Status must be one of: APPLIED, SHORTLISTED, SELECTED, REJECTED");
        }

        JobApplication application = applicationRepository
                .findById(id)
                .orElse(null);

        if (application == null) {
            return error(HttpStatus.NOT_FOUND,
                    "Application not found");
        }

        application.setStatus(newStatus);

        return ResponseEntity.ok(
                applicationRepository.save(application));
    }

    private ResponseEntity<Map<String, String>> error(
            HttpStatus status, String message) {

        return ResponseEntity
                .status(status)
                .body(Map.of("message", message));
    }
}