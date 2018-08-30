package com.artemis.kahn.dao.mongo.entity;

import com.artemis.kahn.dao.mongo.persistence.MongoEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "footprint")
public class Footprint implements MongoEntity {
    /**
     *
     */
    private static final long serialVersionUID = 6816481840987426653L;
    private String id;

    @Id
    @Column(name = ID)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
