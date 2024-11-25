package com.idat.restprevia.repository;

import com.idat.restprevia.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoviesRepository extends JpaRepository<Movie, String> {
    Movie findByName(String name);
}
