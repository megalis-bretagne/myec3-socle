package org.myec3.socle.core.domain.model;

public class OrganismMemberStatus {

    private String key;

    private String name;

    public OrganismMemberStatus() {
    }

    public OrganismMemberStatus(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
