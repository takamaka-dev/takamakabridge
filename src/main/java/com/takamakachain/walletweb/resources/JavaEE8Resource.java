package com.takamakachain.walletweb.resources;

import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashAlgorithmNotFoundException;
import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashEncodeException;
import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashProviderNotFoundException;
import com.h2tcoin.takamakachain.exceptions.wallet.UnlockWalletException;
import com.h2tcoin.takamakachain.exceptions.wallet.WalletException;
import com.h2tcoin.takamakachain.globalContext.FixedParameters;
import com.h2tcoin.takamakachain.globalContext.KeyContexts;
import static com.h2tcoin.takamakachain.globalContext.KeyContexts.WalletCypher.BCQTESLA_PS_1_R2;
import com.h2tcoin.takamakachain.saturn.exceptions.SaturnException;
import com.h2tcoin.takamakachain.utils.F;
import com.h2tcoin.takamakachain.utils.Log;
import com.h2tcoin.takamakachain.utils.simpleWallet.SWTracker;
import com.h2tcoin.takamakachain.utils.simpleWallet.panels.support.ApiBalanceBean;
import com.h2tcoin.takamakachain.utils.simpleWallet.panels.support.identicon.IdentiColorHelper;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmSignUtils;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmTextUtils;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeyStoreBCED25519;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeyStoreBCQTESLAPSSC1Round1;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeyStoreBCQTESLAPSSC1Round2;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeystoreInterface;
import static com.takamakachain.walletweb.resources.support.CryptoHelper.decryptPasswordHEX;
import static com.takamakachain.walletweb.resources.support.CryptoHelper.encryptPasswordHEX;
import com.takamakachain.walletweb.resources.support.ProjectHelper;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
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
import static com.takamakachain.walletweb.resources.support.CryptoHelper.getWebSessionSecret;
import static com.takamakachain.walletweb.resources.support.ProjectHelper.ENC_LABEL;
import static com.takamakachain.walletweb.resources.support.ProjectHelper.ENC_SEP;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author
 */
@Path("javaee8")
public class JavaEE8Resource {

    private static String salt;
    private static KeyStore internalKeystore;
    private static SecretKey webSessionSecret;

    @PostConstruct
    public static final void init() {
        try {
            //TODO modificare per chiamata da riga di comando
            ProjectHelper.initProject(System.getProperty("user.home"));
            internalKeystore = ProjectHelper.getInternalKeystore();
            webSessionSecret = getWebSessionSecret(internalKeystore);

        } catch (IOException | SaturnException | ClassNotFoundException | URISyntaxException | HashEncodeException | HashAlgorithmNotFoundException | HashProviderNotFoundException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            Logger.getLogger(JavaEE8Resource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

    private static final String doGetBalancePost(String passedUrl, String address) throws MalformedURLException, ProtocolException, IOException {
        String r = null;
        URL url = new URL(passedUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String data = "address=" + address;

        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        int status = http.getResponseCode();

        switch (status) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                r = sb.toString();
                break;
            default:
                return null;
        }

        http.disconnect();

        return r;
    }

    @POST
    @Path("getWalletBalances")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static final Response getWalletBalances(SignedResponseBean srb) {

        if (TkmTextUtils.isNullOrBlank(srb.getWalletAddress()) || (srb.getWalletAddress().length() != 44 && srb.getWalletAddress().length() != 19840)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String balanceOfEndpoint = "https://dev.takamaka.io/api/V2/nodeapi/balanceof/";
        String jsonResponseBalanceBean = null;
        try {
            jsonResponseBalanceBean = doGetBalancePost(balanceOfEndpoint, srb.getWalletAddress());
        } catch (MalformedURLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (ProtocolException e) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        if (TkmTextUtils.isNullOrBlank(jsonResponseBalanceBean)) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        System.out.println(jsonResponseBalanceBean);
        ApiBalanceBean apiBalanceBean = TkmTextUtils.getApiBalanceBeanFromJson(jsonResponseBalanceBean);
        if (apiBalanceBean == null) {
            System.out.println("null decode");
            Log.log(Level.SEVERE, "null decode");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).entity(apiBalanceBean).build();
    }

    @POST
    @Path("getWalletCrc")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static final Response getWalletCrc(SignedResponseBean srb) {

        System.out.println(srb.getWalletAddress());

        if (TkmTextUtils.isNullOrBlank(srb.getWalletAddress()) || (srb.getWalletAddress().length() != 44 && srb.getWalletAddress().length() != 19840)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String crc = TkmSignUtils.getHexCRC(srb.getWalletAddress());

        WalletCrcResponseBean wCrc = new WalletCrcResponseBean();
        wCrc.setAddress(srb.getWalletAddress());
        wCrc.setCrcAddress(crc.toUpperCase());

        return Response.status(Response.Status.OK).entity(wCrc).build();

    }

    @POST
    @Path("getWalletIdenticon")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static final Response getWalletIdenticon(SignedResponseBean srb) {

        System.out.println(srb.getWalletAddress());

        if (TkmTextUtils.isNullOrBlank(srb.getWalletAddress()) || (srb.getWalletAddress().length() != 44 && srb.getWalletAddress().length() != 19840)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        System.out.println(IdentiColorHelper.getAvatarBase64URL256(srb.getWalletAddress()));

        String walletIdenticonUrl64 = IdentiColorHelper.getAvatarBase64URL256(srb.getWalletAddress());

        walletIdenticonUrl64 = walletIdenticonUrl64.replace(".", "=").replace("-", "+").replace("_", "/");

        if (TkmTextUtils.isNullOrBlank(walletIdenticonUrl64)) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        WalletIdenticonResponseBean wi = new WalletIdenticonResponseBean();
        if (srb.getWalletAddress().length() == 19840) {
            wi.setAddress("");
        } else {
            wi.setAddress(srb.getWalletAddress());
        }
        wi.setIdenticonUrlBase64(walletIdenticonUrl64);

        return Response.status(Response.Status.OK).entity(wi).build();

    }

    /**
     * status codes https://developer.mozilla.org/it/docs/Web/HTTP/Status
     *
     * @param srb
     * @return
     */
    @POST
    @Path("signedRequest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static final Response signedRequest(SignedRequestBean srb) {
        String plainPass;
        boolean passwordEncoded = false;
        SignedResponseBean signedResponse = new SignedResponseBean();
        //isEncriptedPasswordWithAES256§fc1c35134a497afb7a28da9297b7810e
        if (srb == null) {
            System.out.println("Empty request");
            return Response.status(400).entity(signedResponse).build();
        }
        signedResponse.setRequest(srb);
        signedResponse.setSignedResponse(srb.getRt().name());
        signedResponse.setWalletKey(srb.getWallet().getAddressNumber());

        WalletBean wb = srb.getWallet();
        if (TkmTextUtils.isNullOrBlank(wb.getWalletPassword())) {
            System.out.println("Empty password");
            return Response.status(400).entity(signedResponse).build();
        }
        if (TkmTextUtils.isNullOrBlank(wb.getWalletName())) {
            System.out.println("Empty wallet name");
            return Response.status(400).entity(signedResponse).build();
        }

        plainPass = wb.getWalletPassword();

        if (wb.getWalletPassword().contains(ENC_SEP)) {
            System.out.println("Possible password encrypted");
            String[] spResult = wb.getWalletPassword().split(ENC_SEP, 2);
            if (spResult.length == 2) {
                if (!TkmTextUtils.isNullOrBlank(spResult[0]) && !TkmTextUtils.isNullOrBlank(spResult[1])) {
                    //check flag
                    if (spResult[0].equals(ENC_LABEL)) {
                        try {
                            //try to decode
                            IvParameterSpec ivParameterSpec = ProjectHelper.getIVParameterSpec(wb.getWalletName());
                            plainPass = decryptPasswordHEX(spResult[1], ivParameterSpec, webSessionSecret);
                            passwordEncoded = true;
                        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
                            System.out.println("Malformed Encrypted Secret");
                            return Response.status(400).entity(signedResponse).build();
                        }
                    } else {
                        System.out.println("password with " + ENC_SEP + " ... continue");
                    }
                } else {
                    System.out.println("password with " + ENC_SEP + " ... continue");
                    //return Response.status(500).entity(signedResponse).build();
                }
            } else {
                System.out.println("Decode Error");
                return Response.status(500).entity(signedResponse).build();
            }
        }

        try {
            System.out.println("req");
            InstanceWalletKeystoreInterface iwk;
            System.out.println("srb.getWallet().getWalletCypher(): " + srb.getWallet().getWalletCypher());
            switch (srb.getWallet().getWalletCypher()) {
                case "BCQTESLA_PS_1":
                    //SWTracker.i().setIwk(new InstanceWalletKeyStoreBCQTESLAPSSC1Round1(srb.getWallet().getWalletName(), srb.getWallet().getWalletPassword()));
                    iwk = new InstanceWalletKeyStoreBCQTESLAPSSC1Round1(srb.getWallet().getWalletName(), plainPass);
                    break;
                case "BCQTESLA_PS_1_R2":
                    //SWTracker.i().setIwk(new InstanceWalletKeyStoreBCQTESLAPSSC1Round2(srb.getWallet().getWalletName(), plainPass));
                    iwk = new InstanceWalletKeyStoreBCQTESLAPSSC1Round2(srb.getWallet().getWalletName(), plainPass);
                    break;
                case "Ed25519BC":
                    //SWTracker.i().setIwk(new InstanceWalletKeyStoreBCED25519(srb.getWallet().getWalletName(), plainPass));
                    iwk = new InstanceWalletKeyStoreBCED25519(srb.getWallet().getWalletName(), plainPass);
                    break;
                default:
                    System.out.println("Unsupported Cypher " + srb.getWallet().getWalletCypher());
                    iwk = null;
            }

            if (iwk == null) {
                return Response.status(401).entity(signedResponse).build();
            }

            //gestisci le richieste
            switch(srb.getRt()){
            case GET_ADDRESS:
                signedResponse.setWalletAddress(iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber()));
                break;
                default:
                    //401
            }
            
            
            if (!passwordEncoded) {
                try {
                    IvParameterSpec ivParameterSpec;
                    ivParameterSpec = ProjectHelper.getIVParameterSpec(wb.getWalletName());
                    String encryptPasswordHEX = encryptPasswordHEX(plainPass, ivParameterSpec, webSessionSecret);
                    signedResponse.getRequest().getWallet().setWalletPassword(ENC_LABEL + ENC_SEP + encryptPasswordHEX);
                } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
                    ex.printStackTrace();
                    System.out.println("ENC ERROR");
                    return Response.status(500).entity(signedResponse).build();
                }
            }

            System.out.println(srb.getWallet().getWalletName());
            System.out.println(srb.getWallet().getWalletPassword());
            

            return Response.status(200).entity(signedResponse).build();
        } catch (UnlockWalletException ex) {
            return Response.status(401).entity(signedResponse).build();
        } catch (WalletException ex) {
            return Response.status(500).entity(signedResponse).build();
        }
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
