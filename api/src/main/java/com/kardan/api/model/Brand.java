package com.kardan.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(schema = "kardanapp", name="brands")
@Data
@Where(clause = "state=true")
public class Brand extends CommonEntity{


    @Column(name = "name")
    private String name;


  @JsonIgnore
    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<Model> models;
}