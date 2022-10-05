package org.acme.schooltimetabling.domain;

public class StudentGroup {
    private final String name;
    private final String headTeacher;

    public StudentGroup(String name, String headTeacher){
        this.name = name;
        this.headTeacher = headTeacher;
    }


    public String getName() {
        return name;
    }

    public String getHeadTeacher() {
        return headTeacher;
    }
}
