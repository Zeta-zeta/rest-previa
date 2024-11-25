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
                return Response.ok("Pelicula encontrada\n"+json, MediaType.APPLICATION_JSON).build();
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
                    .entity("Película creada correctamente")
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
            return Response.status(Response.Status.OK)
                    .entity("Pelicula eliminada correctamente")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
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

            if (movie != null) {
                movie.setCategory(updateMovie.getCategory());
                movie.setYear(updateMovie.getYear());
                movie.setOrigin_country(updateMovie.getOrigin_country());
                moviesRepository.save(movie);

                return Response.status(Response.Status.OK)
                        .entity("Pelicula actualizada correctamente")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Pelicula no encontrada").build();
        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al convertir a JSON")
                    .build();
        }
    }




}
