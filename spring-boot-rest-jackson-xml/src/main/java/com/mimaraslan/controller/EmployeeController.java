package com.mimaraslan.controller;

import com.mimaraslan.exception.ResourceNotFoundException;
import com.mimaraslan.model.Department;
import com.mimaraslan.model.Employee;
import com.mimaraslan.repository.DepartmentRepository;
import com.mimaraslan.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.AcceptLanguage;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;


    // LIST ALL
    @GetMapping("employees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("employees/all")
    public ResponseEntity<List<Employee>> getAllEmployees2() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    // ADD
    @PostMapping("employees")
    public Employee createEmployee (@Valid @RequestBody Employee employee){
        return employeeRepository.save(employee);
    }

    @PostMapping("employees/add/v2")
    public ResponseEntity<Employee> createEmployeeV2 (@Valid @RequestBody Employee employee){
        return ResponseEntity.ok(employeeRepository.save(employee));
    }

    @PostMapping("employees/add/v3")
    public ResponseEntity<Object> createEmployeeV3 (@Valid @RequestBody Employee employee){
        Employee employeeObj = employeeRepository.save(employee);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(employeeObj.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    // LIST ID
    @GetMapping("employees/{id}")
    public ResponseEntity<Employee> getEmployeeId(@PathVariable long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Employee not exist with id: "+ id));
        return ResponseEntity.ok(employee);
    }

    @GetMapping("employees/get/{id}")
    public ResponseEntity<Optional<Employee>> getEmployeeId2(@PathVariable long id){
       Optional<Employee> empObj = employeeRepository.findById(id);
        if(empObj.isPresent()){
            return ResponseEntity.ok(empObj);
        }else{
            throw new ResourceNotFoundException("Employee not exist with id: "+ id);
        }
    }

    // UPDATE
    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable long id, @RequestBody Employee employee){
        Employee updateEmployee = employeeRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Employee not exist with id: "+ id));
        updateEmployee.setFirstName(employee.getFirstName());
        updateEmployee.setLastName(employee.getLastName());
        updateEmployee.setEmailId(employee.getEmailId());
        employeeRepository.save(updateEmployee);
        return ResponseEntity.ok(updateEmployee);
    }

    // DELETE
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<HttpStatus> deleteEmployee(@PathVariable long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id: " + id));
        employeeRepository.delete(employee);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // http://localhost:8082/api/v1/employees/{id}/departments
    @GetMapping("employees/{id}/departments")
    public List<Department> getDepartments(@PathVariable long id) {
        Optional<Employee> employeeObj = employeeRepository.findById(id);
        if (!employeeObj.isPresent()){
            throw new ResourceNotFoundException("id: "+ id);
        }
        return employeeObj.get().getDepartments();
    }

    // http://localhost:8082/api/v1/employees/{id}/departments
    @PostMapping("employees/{id}/departments")
    public ResponseEntity<Object> createDepartments (@PathVariable long id, @RequestBody Department department){
        Optional<Employee> employeeObj = employeeRepository.findById(id);
        if (!employeeObj.isPresent()){
            throw new ResourceNotFoundException("id: "+ id);
        }
        Employee employee = employeeObj.get();

        department.setEmployee(employee);
        departmentRepository.save(department);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(department.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}