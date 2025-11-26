package com.example.product_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.example.product_management.entity.Product;
import com.example.product_management.repository.ProductRepository;
import java.util.List;

@SpringBootApplication
public class ProductManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductManagementApplication.class, args);
    }
    
    // Temporary test - remove after verification
    @Bean
    CommandLineRunner test(ProductRepository repository) {
        return args -> {
            System.out.println("=== Testing Repository ===");
            
            // Count all products
            long count = repository.count();
            System.out.println("Total products: " + count);
            
            // Find all products
            List<Product> products = repository.findAll();
            products.forEach(System.out::println);
            
            // Find by category
            List<Product> electronics = repository.findByCategory("Electronics");
            System.out.println("\nElectronics: " + electronics.size());
            
            System.out.println("=== Test Complete ===");
        };
    }
}

