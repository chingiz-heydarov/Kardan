package com.kardan.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(schema = "kardanapp", name="models")
@Data
public class Model {
    @Id
    @GeneratedValue(strategy  = GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    private String modelName;

    @ManyToOne
    @JoinColumn(name="brand_id", referencedColumnName = "id")
    private Brand brand;

    @JsonIgnore
    @OneToMany(mappedBy = "model")
    private List<Gen> gens;
}