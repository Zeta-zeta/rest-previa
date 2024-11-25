package com.idat.restprevia.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idat.restprevia.model.Product;
import com.idat.restprevia.repository.ProductRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
@Path("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProduct(){
        try {
            List<Product> products = productRepository.findAll();
            String json = objectMapper.writeValueAsString(products);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (JsonProcessingException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al convertir a JSON")
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductId(@PathParam("id") Long id){
        try {
            Product product = productRepository.findById(id).orElse(null);
            if(product!=null){
                String json = objectMapper.writeValueAsString(product);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Persona no encontrada").type(MediaType.TEXT_PLAIN).build();
        } catch (JsonProcessingException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al convertir a JSON")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProduct(String json){
        try {
            Product product = objectMapper.readValue(json, Product.class);
            if (product.getId() != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("No se permite enviar un ID al crear una nueva persona.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            productRepository.save(product);
            String newjson = objectMapper.writeValueAsString(product);
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
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePerson(@PathParam("id") Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            productRepository.delete(product);
            return Response.ok(product, MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Producto no encontrado")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePerson(@PathParam("id") Long id, String json) {
        try {
            Product updateProduct = objectMapper.readValue(json, Product.class);
            Product product = productRepository.findById(id).orElse(null);
            if (product != null) {
                product.setName(updateProduct.getName());
                product.setStock(updateProduct.getStock());
                product.setPrice(updateProduct.getPrice());
                product.setMarca(updateProduct.getMarca());
                productRepository.save(product);
                return Response.ok(product, MediaType.APPLICATION_JSON).build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Producto no encontrado").build();
        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al convertir a JSON")
                    .build();
        }
    }




}
