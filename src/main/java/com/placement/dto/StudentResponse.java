package com.placement.dto;

import com.placement.model.Student;

public record StudentResponse(Long id, String name, String email, String branch, double cgpa) {

    public static StudentResponse from(Student s) {
        return new StudentResponse(
                s.getId(),
                s.getName(),
                s.getEmail(),
                s.getBranch(),
                s.getCgpa()
        );
    }
}