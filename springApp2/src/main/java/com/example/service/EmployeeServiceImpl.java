package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.model.Person;
import com.example.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Override
    public List<Person> getAllEmployees() {
        return employeeRepository.findAll();
    }
    @Override
    public void saveEmployee(Person employee) {
        this.employeeRepository.save(employee);
    }
    @Override
    public Person getEmployeeById(long id) {
        Optional<Person> optional = employeeRepository.findById(id);
        Person employee = null;
        if (optional.isPresent()) {
            employee = optional.get();
        } else {
            throw new RuntimeException(" Employee not found for id :: " + id);
        }
        return employee;
    }
    @Override
    public void deleteEmployeeById(long id) {
        this.employeeRepository.deleteById(id);
    }
    @Override
    public Page<Person> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.employeeRepository.findAll(pageable);
    }
}
