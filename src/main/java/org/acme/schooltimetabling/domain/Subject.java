package org.acme.schooltimetabling.domain;

public record Subject(String name, boolean abilityBased) {

    public boolean isAbilityBased() {
        return abilityBased;
    }

    public String getName() {
        return name;
    }
}
