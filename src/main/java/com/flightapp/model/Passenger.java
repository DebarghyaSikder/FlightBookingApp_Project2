package com.flightapp.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.flightapp.model.enums.Gender;

public class Passenger {

    @NotBlank(message = "Passenger name is required")
    private String name;

    @NotNull(message = "Passenger gender is required")
    private Gender gender;

    @NotNull(message = "Passenger age is required")
    @Min(value = 0, message = "Age cannot be negative")
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}