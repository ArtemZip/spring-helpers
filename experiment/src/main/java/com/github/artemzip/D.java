package com.github.artemzip;


import com.github.artemzip.repository.TmpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class D {

    @Autowired
    TmpRepository repository;

    public void init() {
        System.out.println("hey");
    }

    public static void main(String [] args) {
        SpringApplication.run(D.class);
    }

    @Bean
    public CommandLineRunner tmp () {
        return ctx -> {
            Tmp tmp = new Tmp();
            tmp.setName("ahoj");
            repository.save(tmp);
            Tmp tmp1 = new Tmp();
            tmp1.setName("ahojka");
            repository.save(tmp1);


            repository.findAllByName("ahoj").forEach( e -> System.out.println(e.getName()));
            System.out.println(repository.getNameById(1L) + " <<");
        };
    }
}
