package com.pplflw.prototype.domains;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "employers")
public class Employer {
    
    @Id
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "active")
    private Boolean active;

    private Employer(){
    }
    
    public Employer(Long id, String name, Boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
