package com.placement.dto;

public record RegisterRequest(String name, String email, String password, String branch, Double cgpa) {
}