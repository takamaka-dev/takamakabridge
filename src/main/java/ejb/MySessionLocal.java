/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import com.h2tcoin.takamakachain.globalContext.KeyContexts;
import java.math.BigInteger;
import javax.ejb.Local;

/**
 *
 * @author isacco.borsani@h2tcoin.com
 */
@Local
public interface MySessionLocal {
    public String getWalletAddress();

    public void setWalletAddress(String walletAddress);

    public String getWalletName();

    public void setWalletName(String walletName);

    public String getWalletPassword();

    public void setWalletPassword(String walletPassword);

    public int getWalletIndex();

    public void setWalletIndex(int walletIndex);

    public KeyContexts.WalletCypher getWalletCypher();

    public void setWalletCypher(KeyContexts.WalletCypher walletCypher);

    public BigInteger getTkgBalance();

    public void setTkgBalance(BigInteger tkgBalance);

    public BigInteger getTkrBalance();

    public void setTkrBalance(BigInteger tkrBalance);

    public BigInteger getFrozenTkg();

    public void setFrozenTkg(BigInteger frozenTkg);

    public BigInteger getFrozenTkr();

    public void setFrozenTkr(BigInteger frozenTkr);
    
}
