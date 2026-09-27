package com.janconnect.config;

import com.janconnect.entity.Department;
import com.janconnect.entity.User;
import com.janconnect.repository.DepartmentRepository;
import com.janconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedDepartments();
        seedDemoUsers();
    }

    private void seedDepartments() {
        String[][] departments = {
            {"water", "Water Supply", "Municipal water supply and distribution"},
            {"electricity", "Electricity", "Electrical infrastructure and power supply"},
            {"roads", "Roads", "Road maintenance and infrastructure"},
            {"drainage", "Drainage", "Storm drainage and sewage systems"},
            {"garbage", "Garbage/Waste Management", "Solid waste collection and disposal"},
            {"street_lights", "Street Lights", "Street lighting maintenance"},
            {"transport", "Transport", "Public transport and traffic"},
            {"healthcare", "Healthcare", "Public health and medical services"},
            {"sanitation", "Sanitation", "Public sanitation and hygiene"},
            {"certificates", "Revenue & Certificates", "Government certificates and documents"},
            {"property_tax", "Property Tax", "Property tax assessment and collection"},
            {"police", "Police", "Law enforcement and public safety"},
            {"schemes", "Government Schemes", "Social welfare schemes and benefits"},
            {"other", "Other", "Other civic issues"},
        };

        for (String[] dept : departments) {
            if (!departmentRepository.existsByName(dept[1])) {
                departmentRepository.save(Department.builder()
                        .code(dept[0])
                        .name(dept[1])
                        .description(dept[2])
                        .build());
                log.info("Seeded department: {}", dept[1]);
            }
        }
    }

    private void seedDemoUsers() {
        // Demo user 1
        if (!userRepository.existsByEmail("demo@janconnect.com")) {
            userRepository.save(User.builder()
                    .name("Demo User")
                    .email("demo@janconnect.com")
                    .mobile("9876543210")
                    .password(passwordEncoder.encode("Demo@123"))
                    .role(User.Role.USER)
                    .build());
            log.info("Seeded demo user: demo@janconnect.com");
        }

        // Demo user 2
        if (!userRepository.existsByEmail("citizen@janconnect.com")) {
            userRepository.save(User.builder()
                    .name("Citizen User")
                    .email("citizen@janconnect.com")
                    .mobile("9876543211")
                    .password(passwordEncoder.encode("Citizen@123"))
                    .role(User.Role.USER)
                    .build());
            log.info("Seeded demo user: citizen@janconnect.com");
        }

        // Admin user
        if (!userRepository.existsByEmail("admin@janconnect.com")) {
            userRepository.save(User.builder()
                    .name("Admin User")
                    .email("admin@janconnect.com")
                    .mobile("9876543212")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(User.Role.ADMIN)
                    .build());
            log.info("Seeded admin user: admin@janconnect.com");
        }
    }
}
