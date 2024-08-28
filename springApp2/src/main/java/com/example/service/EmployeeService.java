package com.example.service;

import java.util.List;
import com.example.model.Person;
import org.springframework.data.domain.Page;
public interface EmployeeService {
    List<Person> getAllEmployees();
    void saveEmployee(Person employee);
    Person getEmployeeById(long id);
    void deleteEmployeeById(long id);
    Page<Person> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
}
