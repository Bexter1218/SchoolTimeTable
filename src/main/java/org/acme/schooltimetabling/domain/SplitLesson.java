package org.acme.schooltimetabling.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class SplitLesson extends Lesson{

    private String teacher2;

    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    private Room room2;


    public SplitLesson(Long id, String subject, String teacher1, String teacher2, String studentGroup){
        super(id, subject, teacher1, studentGroup);
        this.teacher2 = teacher2;
    }

    public SplitLesson() {}

    public String getTeacher2() {
        return teacher2;
    }

    public Room getRoom2() {
        return room2;
    }

    public void setRoom2(Room room) {
        this.room2 = room;
    }

    public Room getRoom(String teacher){
        if(teacher.equals(this.getTeacher()))
            return room;
        if(teacher.equals(teacher2))
            return room2;
        return null;
    }

    @Override
    public boolean roomEquals() {
        if(room2 != null)
            return room.getName().equals(room2.getName());
        else
            return true;
    }
}
