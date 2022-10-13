package org.acme.schooltimetabling.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Lesson {

    @PlanningId
    private Long id;

    private String subject;
    private String teacher;
    private String studentGroup;
    private String teacher2;
    private String studentGroup2;
    private String pairingID;

    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    protected Timeslot timeslot;
    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    protected Room room;
    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    private Room room2;

    public Lesson() {
    }

    public Lesson(Long id, String subject, String teacher, String studentGroup) {
        this.id = id;
        this.subject = subject;
        this.teacher = teacher;
        this.studentGroup = studentGroup;
    }

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getStudentGroup() {
        return studentGroup;
    }

    public String getTeacher2() {
        if(teacher2 != null)
            return teacher2;
        return "";
    }

    public String getStudentGroup2() {
        if(studentGroup2 != null)
            return studentGroup2;
        return "";
    }

    public String getPairingID() {
        if(pairingID != null)
            return pairingID;
        return "";
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }


    public Room getRoom() {
        return room;
    }

    public Room getRoom(String teacher){
        if(teacher.equals(this.getTeacher()))
            return room;
        if(teacher.equals(teacher2))
            return room2;
        return null;
    }

    public Room getRoom2() {
        return room2;
    }

    public void setTeacher2(String teacher2) {
        this.teacher2 = teacher2;
    }

    public void setStudentGroup2(String studentGroup2) {
        this.studentGroup2 = studentGroup2;
    }

    public void setPairingID(String pairingID) {
        this.pairingID = pairingID;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setRoom2(Room room) {
        this.room2 = room;
    }





    @Override
    public String toString() {
        return subject + "(" + id + ")";
    }

    public boolean roomEquals() {
        if(room2 != null)
            return room.getName().equals(room2.getName());
        else
            return true;
    }

}