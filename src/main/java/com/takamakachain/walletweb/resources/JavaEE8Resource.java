package com.takamakachain.walletweb.resources;

import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashAlgorithmNotFoundException;
import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashEncodeException;
import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashProviderNotFoundException;
import com.h2tcoin.takamakachain.exceptions.wallet.TransactionCanNotBeCreatedException;
import com.h2tcoin.takamakachain.exceptions.wallet.UnlockWalletException;
import com.h2tcoin.takamakachain.exceptions.wallet.WalletException;
import com.h2tcoin.takamakachain.globalContext.FixedParameters;
import com.h2tcoin.takamakachain.globalContext.KeyContexts;
import static com.h2tcoin.takamakachain.globalContext.KeyContexts.WalletCypher.BCQTESLA_PS_1_R2;
import com.h2tcoin.takamakachain.saturn.exceptions.SaturnException;
import com.h2tcoin.takamakachain.tkmdata.exceptions.TkmDataException;
import com.h2tcoin.takamakachain.transactions.InternalTransactionBean;
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
import java.net.ProtocolException;
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
import com.takamakachain.walletweb.resources.support.TransactionsHelper;
import com.takamakachain.walletweb.resources.support.WebHelper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.xml.sax.SAXException;

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

    //@Context
    //ServletContext servletContext;
    @GET
    public static final Response ping() {
        return Response
                .ok("ping")
                .build();
    }

    @GET
    @Path("getPage/{pageid}")
    @Produces(MediaType.TEXT_HTML)
    public static final String getPage(@PathParam("pageid") String pageid) throws FileNotFoundException, IOException, InterruptedException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String resourceName = pageid + ".html";
        InputStream resourceAsStream = classLoader.getResourceAsStream("templates/" + resourceName);
        String toString = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8.name());
        return toString;
    }

    @POST
    @Path("getWalletBalances")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static final Response getWalletBalances(SignedResponseBean srb) throws ProtocolException, IOException, IOException {

        if (TkmTextUtils.isNullOrBlank(srb.getWalletAddress()) || (srb.getWalletAddress().length() != 44 && srb.getWalletAddress().length() != 19840)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String balanceOfEndpoint = "https://dev.takamaka.io/api/V2/nodeapi/balanceof/";
        String jsonResponseBalanceBean = null;
        jsonResponseBalanceBean = ProjectHelper.doPost(balanceOfEndpoint, "address", srb.getWalletAddress());

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
    @Path("uploadBlob")
    @Consumes(MediaType.APPLICATION_JSON)
    public static final Response uploadBlobFromJson(TransactionMessageBean tmb) {
        JSONObject jsonObject = ProjectHelper.isJSONValid(tmb.getMessage());
        if (jsonObject == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        InternalTransactionBean itb = null;
        itb.setEpoch(null);
        itb.setGreenValue(null);
        itb.setRedValue(null);
        itb.setSlot(null);
        itb.setTo(null);
        itb.setTransactionHash(null);

        return Response.status(Response.Status.CREATED).
                entity(jsonObject.toString()).build();

    }

    @POST
    @Path("getFileMeta")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public static final Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException, SAXException, TikaException {
        String base64file;

        byte[] byteFile = toByteArray(uploadedInputStream);

        File temp = new File(fileDetail.getFileName());
        FileUtils.writeByteArrayToFile(temp, byteFile);

        InputStream targetStreamMeta = new FileInputStream(temp);

        BodyContentHandler bodyContentHandler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, fileDetail.getFileName());
        Parser parser = new AutoDetectParser();
        parser.parse(targetStreamMeta, bodyContentHandler, metadata, new ParseContext());

        FilePropertiesBean fpb = new FilePropertiesBean();

        Arrays.stream(metadata.names()).parallel().forEach((String meta) -> {
            String value = metadata.get(meta);
            boolean selected = false;
            if (!TkmTextUtils.isNullOrBlank(value)) {
                selected = true;
            }
            fpb.add(meta, metadata.get(meta), false, selected);
        });

        if (byteFile.length > 4004215) {
            temp.delete();
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {

            base64file = TkmSignUtils.fromByteArrayToB64URL(byteFile);

            fpb.setFileContent(base64file);

            fpb.setFileSize(ProjectHelper.convertToStringRepresentationFileSize(base64file.getBytes().length));
            temp.delete();
            return Response.status(Response.Status.OK).entity(fpb).build();

        }

    }

    public static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }

    @POST
    @Path("getWalletIdenticon")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static final Response getWalletIdenticon(SignedResponseBean srb) {
        String walletAddress = !TkmTextUtils.isNullOrBlank(srb.getPassedData()) ? srb.getPassedData() : srb.getWalletAddress();
        System.out.println(srb.getPassedData());
        if (TkmTextUtils.isNullOrBlank(walletAddress) || (walletAddress.length() != 44 && walletAddress.length() != 19840)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String walletIdenticonUrl64 = IdentiColorHelper.getAvatarBase64URL256(walletAddress);

        walletIdenticonUrl64 = walletIdenticonUrl64.replace(".", "=").replace("-", "+").replace("_", "/");

        if (TkmTextUtils.isNullOrBlank(walletIdenticonUrl64)) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        WalletIdenticonResponseBean wi = new WalletIdenticonResponseBean();
        if (walletAddress.length() == 19840) {
            wi.setAddress("");
        } else {
            wi.setAddress(walletAddress);
        }
        wi.setIdenticonUrlBase64(walletIdenticonUrl64);

        return Response.status(Response.Status.OK).entity(wi).build();

    }

    public static final InstanceWalletKeystoreInterface validateWalletCredentials(WalletBean wb, SignedResponseBean signedResponse) throws IOException {
        if (TkmTextUtils.isNullOrBlank(wb.getWalletPassword())) {
            System.out.println("Empty password");
            return null;
        }
        if (TkmTextUtils.isNullOrBlank(wb.getWalletName())) {
            System.out.println("Empty wallet name");
            return null;
        }

        String plainPass = wb.getWalletPassword();
        boolean passwordEncoded = false;
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
                            ex.printStackTrace();
                            return null;
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
                return null;
            }
        }

        InstanceWalletKeystoreInterface iwk = null;
        try {
            switch (wb.getWalletCypher()) {
                case "BCQTESLA_PS_1":
                    //SWTracker.i().setIwk(new InstanceWalletKeyStoreBCQTESLAPSSC1Round1(srb.getWallet().getWalletName(), srb.getWallet().getWalletPassword()));
                    iwk = new InstanceWalletKeyStoreBCQTESLAPSSC1Round1(wb.getWalletName(), plainPass);
                    break;
                case "BCQTESLA_PS_1_R2":
                    //SWTracker.i().setIwk(new InstanceWalletKeyStoreBCQTESLAPSSC1Round2(srb.getWallet().getWalletName(), plainPass));
                    iwk = new InstanceWalletKeyStoreBCQTESLAPSSC1Round2(wb.getWalletName(), plainPass);
                    break;
                case "Ed25519BC":
                    //SWTracker.i().setIwk(new InstanceWalletKeyStoreBCED25519(srb.getWallet().getWalletName(), plainPass));
                    iwk = new InstanceWalletKeyStoreBCED25519(wb.getWalletName(), plainPass);
                    break;
                default:
                    System.out.println("Unsupported Cypher " + wb.getWalletCypher());
                    iwk = null;
            }
            
            if (!passwordEncoded && null != signedResponse) {
                try {
                    IvParameterSpec ivParameterSpec;
                    ivParameterSpec = ProjectHelper.getIVParameterSpec(wb.getWalletName());
                    String encryptPasswordHEX = encryptPasswordHEX(plainPass, ivParameterSpec, webSessionSecret);
                    signedResponse.getRequest().getWallet().setWalletPassword(ENC_LABEL + ENC_SEP + encryptPasswordHEX);
                } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
                    ex.printStackTrace();
                    System.out.println("ENC ERROR");
                    return null;
                }
            }
            
        } catch (UnlockWalletException ex) {
            ex.printStackTrace();
            return null;
        }

        return iwk;

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
    public static final Response signedRequest(SignedRequestBean srb) throws TransactionCanNotBeCreatedException, IOException, ProtocolException, ProtocolException, TkmDataException, TkmDataException, TkmDataException, TkmDataException {
        SignedResponseBean signedResponse = new SignedResponseBean();
        //isEncriptedPasswordWithAES256§fc1c35134a497afb7a28da9297b7810e
        if (srb == null) {
            System.out.println("Empty request");
            return Response.status(400).entity(signedResponse).build();
        }
        signedResponse.setRequest(srb);
        signedResponse.setSignedResponse(srb.getRt().name());
        signedResponse.setWalletKey(srb.getWallet().getAddressNumber());

        InstanceWalletKeystoreInterface iwk;

        try {
            iwk = validateWalletCredentials(srb.getWallet(), signedResponse);

            if (iwk == null) {
                return Response.status(401).entity(signedResponse).build();
            }

            try {
                //gestisci le richieste
                boolean b = TransactionsHelper.manageRequests(srb, signedResponse, iwk);
                System.out.println("boolean transaction: " + b);
                if (!b) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(signedResponse).build();
                }
            } catch (IOException ex) {
                Logger.getLogger(JavaEE8Resource.class.getName()).log(Level.SEVERE, null, ex);
            }

            

            System.out.println(srb.getWallet().getWalletName());
            System.out.println(srb.getWallet().getWalletPassword());

            return Response.status(Response.Status.OK).entity(signedResponse).build();
        } catch (UnlockWalletException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(signedResponse).build();
        } catch (WalletException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(signedResponse).build();
        }
    }

    @POST
    @Path("createWallet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public static final Response createWallet(WalletBean wallet) throws WalletException {
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
    public static final String ping(@PathParam("name") String name) {
        return "Hello " + name;
    }

}
