package com.acme.ttx.service;

import com.acme.ttx.entity.Student;
import com.acme.ttx.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class StudentWriteService {
    private final StudentRepository repo;

}
