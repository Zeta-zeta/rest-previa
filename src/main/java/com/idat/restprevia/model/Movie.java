package com.idat.restprevia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Movie {

    @Id
    @Column(unique = true)
    private String name;
    private String category;
    private String year;
    private String origin_country;
}
