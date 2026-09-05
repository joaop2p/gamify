package com.example.gamify.models.entities;

import com.example.gamify.models.dtos.output.AtributesOutPutDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "attributes")
public class Attributes {
    @Id
    @Getter
    @UuidGenerator
    private UUID id;

    @Setter
    @Getter
    @OneToOne(mappedBy = "attributes")
    @JsonIgnore
    private Profile profile;

    @Setter
    @Getter
    @Column(name = "strength", columnDefinition = "INT DEFAULT 0")
    private int strength;

    @Setter
    @Getter
    @Column(name = "spirituality", columnDefinition = "INT DEFAULT 0")
    private int spirituality;

    @Setter
    @Getter
    @Column(name = "intelligence", columnDefinition = "INT DEFAULT 0")
    private int intelligence;

    @Setter
    @Getter
    @Column(name="charisma", columnDefinition = "INT DEFAULT 0")
    private int charisma;

    @Setter
    @Getter
    @Column(name = "spatial_perception", columnDefinition = "INT DEFAULT 0")
    private int spatialPerception;

    @Setter
    @Getter
    @Column(name = "self_awareness", columnDefinition = "INT DEFAULT 0")
    private int selfAwareness;

    @Setter
    @Getter
    @Column(name = "language", columnDefinition = "INT DEFAULT 0")
    private int language;

    @Setter
    @Getter
    @Column(name = "logical_reasoning", columnDefinition = "INT DEFAULT 0")
    private int logicalReasoning;

    @Setter
    @Getter
    @Column(name = "musicality", columnDefinition = "INT DEFAULT 0")
    private int musicality;

    public Attributes(){}
    public Attributes(Profile profile){
        this.profile = profile;
    }

    public AtributesOutPutDTO toDTO() {
        return new AtributesOutPutDTO(
                this.strength,
                this.spirituality,
                this.intelligence,
                this.charisma,
                this.spatialPerception,
                this.selfAwareness,
                this.language,
                this.logicalReasoning,
                this.musicality
        );
    }
}
