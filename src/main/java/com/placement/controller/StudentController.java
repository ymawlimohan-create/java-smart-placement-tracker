package com.placement.controller;

import com.placement.dto.LoginRequest;
import com.placement.dto.RegisterRequest;
import com.placement.dto.StudentResponse;
import com.placement.model.Student;
import com.placement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentController(StudentRepository studentRepository,
                             PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (isBlank(req.name()) || isBlank(req.email()) || isBlank(req.password())
                || isBlank(req.branch()) || req.cgpa() == null) {
            return error(HttpStatus.BAD_REQUEST, "All fields are required");
        }

        if (req.cgpa() < 0 || req.cgpa() > 10) {
            return error(HttpStatus.BAD_REQUEST, "CGPA must be between 0 and 10");
        }

        String email = req.email().trim().toLowerCase();

        if (studentRepository.findByEmail(email).isPresent()) {
            return error(HttpStatus.CONFLICT, "Email already registered");
        }

        Student student = new Student();

        student.setName(req.name().trim());
        student.setEmail(email);

        // Store encrypted password
        student.setPassword(passwordEncoder.encode(req.password()));

        student.setBranch(req.branch().trim());
        student.setCgpa(req.cgpa());

        Student saved = studentRepository.save(student);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StudentResponse.from(saved));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        if (isBlank(req.email()) || isBlank(req.password())) {
            return error(HttpStatus.BAD_REQUEST,
                    "Email and password are required");
        }

        Student student = studentRepository
                .findByEmail(req.email().trim().toLowerCase())
                .orElse(null);

        if (student == null ||
                !passwordEncoder.matches(req.password(), student.getPassword())) {

            return error(HttpStatus.UNAUTHORIZED,
                    "Invalid email or password");
        }

        return ResponseEntity.ok(StudentResponse.from(student));
    }

    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentResponse::from)
                .toList();
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