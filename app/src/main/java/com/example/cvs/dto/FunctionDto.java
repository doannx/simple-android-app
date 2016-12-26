package com.example.cvs.dto;

/**
 * Created by john on 12/25/16.
 */

public class FunctionDto {
    private int id;
    private String description;

    public FunctionDto(String description) {
        this.description = description;
    }

    public FunctionDto(int id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionDto) {
            FunctionDto c = (FunctionDto) obj;
            if (c.getDescription().equals(this.description) && c.getId() == id) return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
