package org.acme.schooltimetabling.domain;

import java.util.List;

public class Teacher {
    private String name;
    private List<Timeslot> available;

    public Teacher(String name, List<Timeslot> available) {
        this.name = name;
        this.available = available;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Timeslot> getAvailable() {
        return available;
    }

    public void setAvailable(List<Timeslot> available) {
        this.available = available;
    }
}
