package com.takamakachain.walletweb.resources;

import com.h2tcoin.takamakachain.exceptions.wallet.UnlockWalletException;
import com.h2tcoin.takamakachain.exceptions.wallet.WalletException;
import com.h2tcoin.takamakachain.globalContext.FixedParameters;
import com.h2tcoin.takamakachain.globalContext.KeyContexts;
import static com.h2tcoin.takamakachain.globalContext.KeyContexts.WalletCypher.BCQTESLA_PS_1;
import static com.h2tcoin.takamakachain.globalContext.KeyContexts.WalletCypher.BCQTESLA_PS_1_R2;
import com.h2tcoin.takamakachain.utils.F;
import com.h2tcoin.takamakachain.utils.Log;
import com.h2tcoin.takamakachain.utils.simpleWallet.SWTracker;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeyStoreBCED25519;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeyStoreBCQTESLAPSSC1Round1;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeyStoreBCQTESLAPSSC1Round2;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeystoreInterface;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author
 */
@Path("javaee8")
public class JavaEE8Resource {

    @Context
    ServletContext servletContext;

    @GET
    public Response ping() {
        return Response
                .ok("ping")
                .build();
    }

    @GET
    @Path("getPage/{pageid}")
    @Produces(MediaType.TEXT_HTML)
    public String getPage(@PathParam("pageid") String pageid) throws FileNotFoundException, IOException, InterruptedException {
        BufferedReader br = new BufferedReader(new FileReader(servletContext.getRealPath("/templates") + "/" + pageid + ".html"));
        String line;
        String contentResponse = "";
        while ((line = br.readLine()) != null) {
            contentResponse += line;
        }
        return contentResponse;
    }
    
    @POST
    @Path("signedRequest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signedRequest(SignedRequestBean srb) throws WalletException {
        SignedResponseBean signedResponse = new SignedResponseBean();
        signedResponse.setRequest(srb);
        signedResponse.setSignedResponse(srb.getRt().name());
        
        switch (srb.getWallet().getWalletCypher()) {
            case "BCQTESLA_PS_1":
                SWTracker.i().setIwk(new InstanceWalletKeyStoreBCQTESLAPSSC1Round1(srb.getWallet().getWalletName(), srb.getWallet().getWalletPassword()));

            case "BCQTESLA_PS_1_R2":
                SWTracker.i().setIwk(new InstanceWalletKeyStoreBCQTESLAPSSC1Round2(srb.getWallet().getWalletName(), srb.getWallet().getWalletPassword()));

            case "Ed25519BC":
                SWTracker.i().setIwk(new InstanceWalletKeyStoreBCED25519(srb.getWallet().getWalletName(), srb.getWallet().getWalletPassword()));

        }
        
        
        
        System.out.println(srb.getWallet().getWalletName());
        System.out.println(srb.getWallet().getWalletPassword());
        
        signedResponse.setWalletAddress(SWTracker.i().getIwk().getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber()));
        
        return Response.status(200).entity(signedResponse).build();
    }

    @POST
    @Path("createWallet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createWallet(WalletBean wallet) throws WalletException {
        System.out.println(wallet.getWalletName());
        System.out.println(wallet.getWalletCypher());
        
        switch (wallet.getWalletCypher()) {
            case "Ed25519BC":
                F.b("ED25519BC");
                SWTracker.i().getNewWalletBean().setCypher(KeyContexts.WalletCypher.Ed25519BC);
                break;

            case "BCQTESLA_PS_1_R2":
                F.b("BCQTESLA_PS_2");
                SWTracker.i().getNewWalletBean().setCypher(KeyContexts.WalletCypher.BCQTESLA_PS_1_R2);
                break;

            default:
                F.b("Default to ED25519BC");
                SWTracker.i().getNewWalletBean().setCypher(KeyContexts.WalletCypher.Ed25519BC);
                break;
        }

        InstanceWalletKeystoreInterface iwk = null;

        String internalName = FixedParameters.USER_WALLETS_PREFIX + (new Date()).getTime() + FixedParameters.USER_WALLETS_FILE_EXTENSION;
        String password = wallet.getWalletPassword();
        try {
            switch (SWTracker.i().getNewWalletBean().getCypher()) {
                case BCQTESLA_PS_1_R2:
                    iwk = new InstanceWalletKeyStoreBCQTESLAPSSC1Round2(internalName, password);

                    break;

                case Ed25519BC:
                    iwk = new InstanceWalletKeyStoreBCED25519(internalName, password);
                    System.out.println(iwk.getPublicKeyAtIndexURL64(0));

                    break;
                default:
                    F.rb("NOT IMPLEMENTED");

            }

        } catch (UnlockWalletException e) {

        }

        return Response.status(200).entity("ciao").type(MediaType.TEXT_PLAIN).build();
    }

//    @POST
//    @Path("printForm")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response printForm(WalletBean wallet) {
//        //process parameters
//        System.out.println(wallet.getEmail());
//        return Response.status(200).build();
//    }
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
