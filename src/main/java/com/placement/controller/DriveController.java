package com.placement.controller;

import com.placement.model.Drive;
import com.placement.model.Student;
import com.placement.repository.DriveRepository;
import com.placement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drives")
public class DriveController {

    private final DriveRepository driveRepository;
    private final StudentRepository studentRepository;

    public DriveController(DriveRepository driveRepository,
                           StudentRepository studentRepository) {
        this.driveRepository = driveRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping
    public ResponseEntity<?> addDrive(@RequestBody Drive drive) {

        if (isBlank(drive.getCompany()) || isBlank(drive.getRole())) {
            return error(HttpStatus.BAD_REQUEST,
                    "Company and role are required");
        }

        if (drive.getPackageLpa() < 0) {
            return error(HttpStatus.BAD_REQUEST,
                    "Package cannot be negative");
        }

        if (drive.getMinCgpa() < 0 || drive.getMinCgpa() > 10) {
            return error(HttpStatus.BAD_REQUEST,
                    "Minimum CGPA must be between 0 and 10");
        }

        drive.setId(null);
        drive.setCompany(drive.getCompany().trim());
        drive.setRole(drive.getRole().trim());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(driveRepository.save(drive));
    }

    @GetMapping
    public List<Drive> getAllDrives() {
        return driveRepository.findAll();
    }

    @GetMapping("/eligible/{studentId}")
    public ResponseEntity<?> getEligibleDrives(
            @PathVariable Long studentId) {

        Student student = studentRepository
                .findById(studentId)
                .orElse(null);

        if (student == null) {
            return error(HttpStatus.NOT_FOUND,
                    "Student not found");
        }

        return ResponseEntity.ok(
                driveRepository.findByMinCgpaLessThanEqual(
                        student.getCgpa()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDrive(@PathVariable Long id) {

        if (!driveRepository.existsById(id)) {
            return error(HttpStatus.NOT_FOUND,
                    "Drive not found");
        }

        driveRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private ResponseEntity<Map<String, String>> error(
            HttpStatus status, String message) {

        return ResponseEntity
                .status(status)
                .body(Map.of("message", message));
    }
}