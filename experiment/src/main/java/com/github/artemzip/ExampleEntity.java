package com.github.artemzip;

import com.github.artemzip.annotation.JpaRepository;
import com.github.artemzip.annotation.RestCrudController;
import com.github.artemzip.annotation.structure.Method;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;


@Entity
@RestCrudController("hello")
@JpaRepository({
        @Method(name = "findAllByName", returnType = List.class, args = {String.class}),
        @Method(name = "findAllByNameAndId", returnType = List.class, args = {String.class, Long.class}),
        @Method(name = "getNameById", returnType = String.class, args = {
                Long.class }, query = "Select u.name from ExampleEntity u where u.id = ?1") })
public class ExampleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
