/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.wallet;

import com.google.common.collect.Maps;
import com.uifuture.springbootblockchain.util.Base58Check;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 钱包工具类
 *
 * @author chenhx
 * @version WalletUtils.java, v 0.1 2018-10-16 下午 6:09
 */

@Slf4j
public class WalletUtils {

    /**
     * 钱包文件
     */
    private final static String WALLET_FILE = "wallet.dat";
    /**
     * 加密算法
     */
    private static final String ALGORITHM = "AES";
    /**
     * 密文
     */
    private static final byte[] CIPHER_TEXT = "2oF@5sC%DNf32y!TmiZi!tG9W5rLaniD".getBytes();
    /**
     * 钱包工具实例
     */
    private volatile static WalletUtils instance;

    private WalletUtils() {
        initWalletFile();
    }

    public static WalletUtils getInstance() {
        if (instance == null) {
            synchronized (WalletUtils.class) {
                if (instance == null) {
                    instance = new WalletUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化钱包文件
     */
    private void initWalletFile() {
        File file = new File(WALLET_FILE);
        if (!file.exists()) {
            this.saveToDisk(new Wallets());
        } else {
            this.loadFromDisk();
        }
    }

    /**
     * 获取所有的钱包地址
     *
     * @return
     */
    public Set<String> getAddresses() {
        Wallets wallets = this.loadFromDisk();
        return wallets.getAddresses();
    }

    /**
     * 获取钱包数据
     *
     * @param address 钱包地址
     * @return
     */
    public Wallet getWallet(String address) {
        Wallets wallets = this.loadFromDisk();
        return wallets.getWallet(address);
    }

    /**
     * 创建钱包
     *
     * @return
     */
    public Wallet createWallet() {
        Wallet wallet = new Wallet();
        Wallets wallets = this.loadFromDisk();
        wallets.addWallet(wallet);
        this.saveToDisk(wallets);
        return wallet;
    }

    /**
     * 保存钱包数据
     */
    private void saveToDisk(Wallets wallets) {
        try {
            if (wallets == null) {
                log.error("Fail to save wallet to file ! wallets is null ");
                throw new Exception("ERROR: Fail to save wallet to file !");
            }
            SecretKeySpec sks = new SecretKeySpec(CIPHER_TEXT, ALGORITHM);
            // Create cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            SealedObject sealedObject = new SealedObject(wallets, cipher);
            // Wrap the output stream
            @Cleanup CipherOutputStream cos = new CipherOutputStream(
                    new BufferedOutputStream(new FileOutputStream(WALLET_FILE)), cipher);
            @Cleanup ObjectOutputStream outputStream = new ObjectOutputStream(cos);
            outputStream.writeObject(sealedObject);
        } catch (Exception e) {
            log.error("Fail to save wallet to disk !", e);
            throw new RuntimeException("Fail to save wallet to disk !");
        }
    }

    /**
     * 加载钱包数据
     */
    private Wallets loadFromDisk() {
        try {
            SecretKeySpec sks = new SecretKeySpec(CIPHER_TEXT, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, sks);
            @Cleanup CipherInputStream cipherInputStream = new CipherInputStream(
                    new BufferedInputStream(new FileInputStream(WALLET_FILE)), cipher);
            @Cleanup ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
            SealedObject sealedObject = (SealedObject) inputStream.readObject();
            return (Wallets) sealedObject.getObject(cipher);
        } catch (Exception e) {
            log.error("Fail to load wallet from disk ! ", e);
            throw new RuntimeException("Fail to load wallet from disk ! ");
        }
    }

    /**
     * 钱包存储对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Wallets implements Serializable {

        private static final long serialVersionUID = -2542070981569243131L;

        private Map<String, Wallet> walletMap = Maps.newHashMap();

        /**
         * 添加钱包
         *
         * @param wallet
         */
        private void addWallet(Wallet wallet) {
            try {
                this.walletMap.put(wallet.getAddress(), wallet);
            } catch (Exception e) {
                log.error("Fail to add wallet ! ", e);
                throw new RuntimeException("Fail to add wallet !");
            }
        }

        /**
         * 获取所有的钱包地址
         *
         * @return
         */
        Set<String> getAddresses() {
            if (walletMap == null) {
                log.error("Fail to get address ! walletMap is null ! ");
                throw new RuntimeException("Fail to get addresses ! ");
            }
            return walletMap.keySet();
        }

        /**
         * 获取钱包数据
         *
         * @param address 钱包地址
         * @return
         */
        Wallet getWallet(String address) {
            // 检查钱包地址是否合法
            try {
                Base58Check.base58ToBytes(address);
            } catch (Exception e) {
                log.error("Fail to get wallet ! address invalid ! address=" + address, e);
                throw new RuntimeException("Fail to get wallet ! ");
            }
            Wallet wallet = walletMap.get(address);
            if (wallet == null) {
                log.error("Fail to get wallet ! wallet don`t exist ! address=" + address);
                throw new RuntimeException("Fail to get wallet ! ");
            }
            return wallet;
        }
    }
}
