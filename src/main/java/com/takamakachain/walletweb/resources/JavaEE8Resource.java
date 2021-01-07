package com.takamakachain.walletweb.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author
 */
@Path("javaee8")
public class JavaEE8Resource {

    @GET
    public Response ping() {
        return Response
                .ok("ping")
                .build();
    }

    @POST
    @Path("printForm")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response printForm(WalletBean wallet) {
        //process parameters
        System.out.println(wallet.getEmail());
        return Response.status(200).build();
    }
    
//    @POST
//    @Path("printForm")
//    public void printFormOld() {
//        System.out.println("ciao mondo");
//    }

    @GET
    @Path("testy/{name}")
    public String ping(@PathParam("name") String name) {
        return "Hello " + name;
    }

}
