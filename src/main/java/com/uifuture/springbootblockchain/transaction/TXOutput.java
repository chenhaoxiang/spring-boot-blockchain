/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.transaction;

import com.uifuture.springbootblockchain.collection.Collection;
import com.uifuture.springbootblockchain.util.Base58Check;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * 交易输出
 *
 * @author chenhx
 * @version TXOutput.java, v 0.1 2018-10-15 下午 6:23
 */
@Data
public class TXOutput {

    /**
     * 交易的藏品
     */
    private String hash;

    /**
     * 公钥Hash
     */
    private byte[] pubKeyHash;

    public TXOutput() {
    }

    public TXOutput(String hash, byte[] pubKeyHash) {
        this.hash = hash;
        this.pubKeyHash = pubKeyHash;
    }

    /**
     * 创建交易输出
     *
     * @param hash
     * @param address 接收人
     * @return
     */
    public static TXOutput newTXOutput(String hash, String address) {
        // 反向转化为 byte 数组
        byte[] versionedPayload = Base58Check.base58ToBytes(address);
        byte[] pubKeyHash = Arrays.copyOfRange(versionedPayload, 1, versionedPayload.length);
        return new TXOutput(hash, pubKeyHash);
    }

    /**
     * 检查交易输出是否能够使用指定的公钥
     *
     * @param pubKeyHash
     * @return
     */
    public boolean isLockedWithKey(byte[] pubKeyHash) {
        return Arrays.equals(this.getPubKeyHash(), pubKeyHash);
    }

}
