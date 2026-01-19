package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.model.RequestType;
import gr.hua.dit.mycitygov.core.repository.DepartmentRepository;
import gr.hua.dit.mycitygov.core.repository.RequestTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RequestTypeService {

    private final RequestTypeRepository requestTypeRepository;
    private final DepartmentRepository departmentRepository;

    public RequestTypeService(RequestTypeRepository requestTypeRepository,
                              DepartmentRepository departmentRepository) {
        this.requestTypeRepository = requestTypeRepository;
        this.departmentRepository = departmentRepository;
    }

    public long countAllRequests() {

        return requestTypeRepository.count();
    }

    public List<RequestType> findAll() {

        return requestTypeRepository.findAll();
    }

    public List<RequestType> findAllActive() {
        return requestTypeRepository.findAllByActiveTrue();
    }

    @Transactional
    public void createType(String name, String description, Integer slaDays, Long departmentId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));
        RequestType requestType = new RequestType(
                name,
                description,
                slaDays,
                true,
                department
        );

        requestTypeRepository.save(requestType);
    }

    @Transactional
    public void toggleStatus(Long id) {
        RequestType requestType = requestTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request Type not found with id: " + id));

        requestType.setActive(!requestType.getActive());

        requestTypeRepository.save(requestType);
    }
}