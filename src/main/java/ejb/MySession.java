/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import com.h2tcoin.takamakachain.globalContext.KeyContexts;
import io.takamaka.code.whitelisting.internal.database.java.math.BigInteger;
import javax.ejb.Stateless;

/**
 *
 * @author isacco.borsani@h2tcoin.com
 */
@Stateless
public class MySession implements MySessionLocal {
    
    private String walletAddress, walletName, walletPassword;
    private int walletIndex;
    private KeyContexts.WalletCypher walletCypher;
    private BigInteger tkgBalance, tkrBalance, frozenTkg, frozenTkr;

    @Override
    public String getWalletAddress() {
        return walletAddress;
    }

    @Override
    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public String getWalletName() {
        return walletName;
    }

    @Override
    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    @Override
    public String getWalletPassword() {
        return walletPassword;
    }

    @Override
    public void setWalletPassword(String walletPassword) {
        this.walletPassword = walletPassword;
    }

    @Override
    public int getWalletIndex() {
        return walletIndex;
    }

    @Override
    public void setWalletIndex(int walletIndex) {
        this.walletIndex = walletIndex;
    }

    @Override
    public KeyContexts.WalletCypher getWalletCypher() {
        return walletCypher;
    }

    @Override
    public void setWalletCypher(KeyContexts.WalletCypher walletCypher) {
        this.walletCypher = walletCypher;
    }

    @Override
    public BigInteger getTkgBalance() {
        return tkgBalance;
    }

    @Override
    public void setTkgBalance(BigInteger tkgBalance) {
        this.tkgBalance = tkgBalance;
    }

    @Override
    public BigInteger getTkrBalance() {
        return tkrBalance;
    }

    @Override
    public void setTkrBalance(BigInteger tkrBalance) {
        this.tkrBalance = tkrBalance;
    }

    @Override
    public BigInteger getFrozenTkg() {
        return frozenTkg;
    }

    @Override
    public void setFrozenTkg(BigInteger frozenTkg) {
        this.frozenTkg = frozenTkg;
    }

    @Override
    public BigInteger getFrozenTkr() {
        return frozenTkr;
    }

    @Override
    public void setFrozenTkr(BigInteger frozenTkr) {
        this.frozenTkr = frozenTkr;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
