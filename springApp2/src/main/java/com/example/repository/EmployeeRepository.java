package com.example.repository;

import com.example.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EmployeeRepository extends JpaRepository<Person, Long> {
}
