package com.github.bbonanno;

import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;

@Path("/myapp")
public class TestResource {

    @GET
    @Path("greet")
    public String greet() {
        return "Hello world";
    }

    @GET
    @Path("fail500")
    public String fail500() {
        throw new InternalServerErrorException();
    }
}