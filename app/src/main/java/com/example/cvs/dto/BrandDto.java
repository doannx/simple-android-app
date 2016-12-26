package com.example.cvs.dto;

/**
 * Created by john on 12/25/16.
 */

public class BrandDto {
    private int id;
    private String brand;
    private String generic;
    private int functionId;

    public BrandDto(String brand, String generic, int functionId) {
        this.brand = brand;
        this.generic = generic;
        this.functionId = functionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getGeneric() {
        return generic;
    }

    public void setGeneric(String generic) {
        this.generic = generic;
    }

    public int getFunctionId() {
        return functionId;
    }

    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }
}
