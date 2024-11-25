package com.idat.restprevia.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idat.restprevia.model.Movie;
import com.idat.restprevia.repository.MoviesRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
@Path("/movies")
public class MoviesController {

    @Autowired
    private MoviesRepository moviesRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovies(){
        try {
            List<Movie> products = moviesRepository.findAll();
            String json = objectMapper.writeValueAsString(products);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (JsonProcessingException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al convertir a JSON")
                    .build();
        }
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieId(@PathParam("name") String name){
        try {
            Movie movie = moviesRepository.findByName(name);
            if(movie!=null){
                String json = objectMapper.writeValueAsString(movie);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Pelicula no encontrada").type(MediaType.TEXT_PLAIN).build();
        } catch (JsonProcessingException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al convertir a JSON")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovie(String json){
        try {
            Movie movie = objectMapper.readValue(json, Movie.class);
            moviesRepository.save(movie);
            String newjson = objectMapper.writeValueAsString(movie);
            return Response.status(Response.Status.CREATED)
                    .entity(newjson)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (JsonProcessingException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al convertir a JSON").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @DELETE
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovie(@PathParam("name") String name) {
        Movie movie = moviesRepository.findByName(name);
        if (movie != null) {
            moviesRepository.delete(movie);
            return Response.ok(movie, MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Pelicula no encontrada")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }

    @PUT
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMovie(@PathParam("name") String name, String json) {
        try {
            // Convertir el JSON recibido a un objeto Movie
            Movie updateMovie = objectMapper.readValue(json, Movie.class);

            // Buscar la película existente por su nombre (clave primaria)
            Movie movie = moviesRepository.findByName(name);

            // Si no existe la película, devolver error 404
            if (movie == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Pelicula no encontrada con el nombre: " + name)
                        .build();
            }

            // Si el 'name' de la película recibida es diferente al actual, manejar este cambio
            if (!movie.getName().equals(updateMovie.getName())) {
                // Aquí podrías verificar si el nuevo nombre ya existe, para evitar duplicados
                Movie existingMovie = moviesRepository.findByName(updateMovie.getName());
                if (existingMovie != null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Ya existe una película con el nombre: " + updateMovie.getName())
                            .build();
                }

            }

            // Actualizar los demás campos de la película
            movie.setCategory(updateMovie.getCategory());
            movie.setYear(updateMovie.getYear());
            movie.setOrigin_country(updateMovie.getOrigin_country());

            // Guardar los cambios en el repositorio (la entidad se debe actualizar, no insertar una nueva)
            moviesRepository.save(movie);

            // Devolver la película actualizada
            return Response.ok(movie, MediaType.APPLICATION_JSON).build();
        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al convertir a JSON")
                    .build();
        }
    }




}
