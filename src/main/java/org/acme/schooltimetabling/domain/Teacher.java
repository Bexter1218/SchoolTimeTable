package org.acme.schooltimetabling.domain;

import java.util.List;

public record Teacher(String name, List<Timeslot> available) {


    public String getName() {
        return name;
    }


    public List<Timeslot> getAvailable() {
        return available;
    }

}
