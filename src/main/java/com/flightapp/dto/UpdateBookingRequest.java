package com.flightapp.dto;

import com.flightapp.model.enums.MealType;

import jakarta.validation.constraints.NotNull;

public class UpdateBookingRequest {

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
}