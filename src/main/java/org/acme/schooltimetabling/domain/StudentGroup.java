package org.acme.schooltimetabling.domain;

public record StudentGroup(String name, String headTeacher) {

    public String getName() {
        return name;
    }

    public String getHeadTeacher() {
        return headTeacher;
    }

}
