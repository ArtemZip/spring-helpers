package com.github.artemzip.example;


import com.github.artemzip.example.repository.ExampleEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ExampleApp {

    @Autowired
    ExampleEntityRepository repository;

    @Bean
    public CommandLineRunner tmp () {
        return ctx -> {
            ExampleEntity exampleEntity = new ExampleEntity();
            exampleEntity.setName("ahoj");
            repository.save(exampleEntity);
            ExampleEntity exampleEntity1 = new ExampleEntity();
            exampleEntity1.setName("ahojka");
            repository.save(exampleEntity1);


            repository.findAllByName("ahoj").forEach( e -> System.out.println(e.getName()));
            System.out.println(repository.getNameById(1L) + " <<");

        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ExampleApp.class);
    }
}
