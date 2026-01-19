package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public long countActiveDepartments() {
        return departmentRepository.countByActiveTrue();
    }

    @Transactional
    public void updateSchedule(Long departmentId, LocalTime openTime, LocalTime closeTime) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new RuntimeException("Department not found"));
        department.setOpenTime(openTime);
        department.setCloseTime(closeTime);

        String scheduleString = openTime.toString() + " - " + closeTime.toString();
        department.setWorkingHours(scheduleString);

        departmentRepository.save(department);
    }

    @Transactional
    public void createDepartment(String name, String startTime, String endTime) {
        Department dept = new Department();
        dept.setName(name);

        dept.setWorkingHours(startTime + " - " + endTime);

        dept.setActive(true);
        departmentRepository.save(dept);
    }
}