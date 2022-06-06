/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.transaction;

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
     * 藏品的hash
     */
    private String value;
    /**
     * 公钥Hash - 也就是这个公钥收到这个藏品
     */
    private byte[] pubKeyHash;

    /**
     * 交易的时间
     */
    private long timestamp;

    public TXOutput() {
    }

    public TXOutput(String value, byte[] pubKeyHash) {
        this.value = value;
        this.pubKeyHash = pubKeyHash;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 创建交易输出
     *
     * @param value
     * @param address
     * @return
     */
    public static TXOutput newTXOutput(String value, String address) {
        // 反向转化为 byte 数组
        byte[] versionedPayload = Base58Check.base58ToBytes(address);
        byte[] pubKeyHash = Arrays.copyOfRange(versionedPayload, 1, versionedPayload.length);
        return new TXOutput(value, pubKeyHash);
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
