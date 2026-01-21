package gr.hua.dit.mycitygov.config;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalTime;


@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final RequestRepository requestRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           RequestTypeRepository requestTypeRepository,
                           RequestRepository requestRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.requestRepository = requestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        // Departments
        Department techDept = initDepartment("Technical Service", "08:00 - 15:00", LocalTime.of(8, 0), LocalTime.of(15, 0));
        Department civilDept = initDepartment("Civil Registry", "09:00 - 14:00", LocalTime.of(9, 0), LocalTime.of(14, 0));
        Department kepDept = initDepartment("KEP (Citizen Service)", "08:00 - 20:00", LocalTime.of(8, 0), LocalTime.of(20, 0));

        // Users
        initUser("admin@mycity.gov", "Super", "Admin", "000000000", null, Role.ADMIN, null);

        User citizen = initUser("citizen@mycity.gov", "Giannis", "Papadopoulos", "123456788", "11111111111", Role.CITIZEN, null);

        initUser("tech@mycity.gov", "Petros", "Technikos", "999999991", null, Role.EMPLOYEE, techDept);
        initUser("kep@mycity.gov", "Maria", "Dimitriou", "999999992", null, Role.EMPLOYEE, kepDept);
        initUser("kep1@mycity.gov", "Giorgos", "Kepou", "999999912", null, Role.EMPLOYEE, kepDept);

        // Request Types
        RequestType potholeType = initRequestType("Repair Pothole", techDept, 10);
        RequestType birthType = initRequestType("Birth Certificate", kepDept, 1);

        // Mock Requests
        if (requestRepository.count() == 0 && citizen != null) {
            createRequest(citizen, potholeType, "Dangerous pothole on Venizelou str, near the pharmacy.");
            createRequest(citizen, birthType, "Urgent: Needed for school registration.");
            log.info("Mock requests created.");
        }

        log.info("Data initialization finished.");
    }

    private Department initDepartment(String name, String hoursStr, LocalTime open, LocalTime close) {
        return departmentRepository.findByName(name).orElseGet(() -> {
            Department d = new Department();
            d.setName(name);
            d.setWorkingHours(hoursStr);
            d.setOpenTime(open);
            d.setCloseTime(close);
            d.setActive(true);
            return departmentRepository.save(d);
        });
    }
    private User initUser(String email, String firstName, String lastName, String afm, String amka, Role role, Department department) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("1234")); // Default password for dev
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAfm(afm);
            user.setAmka(amka);
            user.setRole(role);
            user.setDepartment(department);

            if (role == Role.CITIZEN) {
                user.setPhoneNumber("+306900000000");
            }

            log.info("Created user: {}", email);
            return userRepository.save(user);
        });
    }

    private RequestType initRequestType(String name, Department dept, int sla) {
        return requestTypeRepository.findByName(name).orElseGet(() -> {
            RequestType rt = new RequestType();
            rt.setName(name);
            rt.setDepartment(dept);
            rt.setSlaDays(sla);
            rt.setActive(true);
            return requestTypeRepository.save(rt);
        });
    }

    private void createRequest(User citizen, RequestType type, String description) {
        Request req = new Request();
        req.setCitizen(citizen);
        req.setRequestType(type);
        req.setDescription(description);
        req.setStatus(RequestStatus.SUBMITTED);
        requestRepository.save(req);
    }
}