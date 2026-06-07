package com.example.booking_tour.Component;

import com.example.booking_tour.Model.Customer;
import com.example.booking_tour.Model.Employee;
import com.example.booking_tour.Repository.CustomerRepository;
import com.example.booking_tour.Repository.EmployeeRepository;
import com.example.booking_tour.Services.CustomerServices;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabasePasswordMigrator implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabasePasswordMigrator(CustomerRepository customerRepository,
                                    EmployeeRepository employeeRepository,
                                    PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        migrateCustomerPasswords();
        migrateEmployeePasswords();
    }

    private void migrateCustomerPasswords() {
        try {
            List<Customer> customers = customerRepository.findAll();
            int count = 0;
            for (Customer customer : customers) {
                String rawPassword = customer.getPasswordHash();
                if (rawPassword != null && !rawPassword.trim().isEmpty() && !CustomerServices.isAlreadyHashed(rawPassword)) {
                    String encoded = passwordEncoder.encode(rawPassword);
                    customer.setPasswordHash(encoded);
                    customerRepository.save(customer);
                    count++;
                }
            }
            if (count > 0) {
                System.out.println(">>> DatabasePasswordMigrator: Đã di cư thành công " + count + " mật khẩu khách hàng sang BCrypt.");
            } else {
                System.out.println(">>> DatabasePasswordMigrator: Không phát hiện mật khẩu khách hàng nào cần di cư.");
            }
        } catch (Exception e) {
            System.err.println(">>> DatabasePasswordMigrator: Lỗi khi di cư mật khẩu khách hàng: " + e.getMessage());
        }
    }

    private void migrateEmployeePasswords() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            int count = 0;
            for (Employee employee : employees) {
                String rawPassword = employee.getPasswordHash();
                if (rawPassword != null && !rawPassword.trim().isEmpty() && !CustomerServices.isAlreadyHashed(rawPassword)) {
                    String encoded = passwordEncoder.encode(rawPassword);
                    employee.setPasswordHash(encoded);
                    employeeRepository.save(employee);
                    count++;
                }
            }
            if (count > 0) {
                System.out.println(">>> DatabasePasswordMigrator: Đã di cư thành công " + count + " mật khẩu nhân viên sang BCrypt.");
            } else {
                System.out.println(">>> DatabasePasswordMigrator: Không phát hiện mật khẩu nhân viên nào cần di cư.");
            }
        } catch (Exception e) {
            System.err.println(">>> DatabasePasswordMigrator: Lỗi khi di cư mật khẩu nhân viên: " + e.getMessage());
        }
    }
}
