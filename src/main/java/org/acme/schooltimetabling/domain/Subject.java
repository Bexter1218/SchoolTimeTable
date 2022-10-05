package org.acme.schooltimetabling.domain;

public class Subject{
    private final String name;
    private final boolean abilityBased;

    public Subject(String name, boolean abilityBased){
        this.name = name;
        this.abilityBased = abilityBased;
    }

    public boolean isAbilityBased() {
        return abilityBased;
    }

    public String getName(){
        return name;
    }
}
