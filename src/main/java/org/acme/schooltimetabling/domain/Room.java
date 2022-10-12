package org.acme.schooltimetabling.domain;

import java.util.List;

public record Room(String name, List<String> teachableHere) {

    public String getName() {
        return name;
    }

    public List<String> getTeachable() {return teachableHere;}

    @Override
    public String toString() {
        return name;
    }

}