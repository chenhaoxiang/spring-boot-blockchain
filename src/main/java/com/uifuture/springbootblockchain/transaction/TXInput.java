/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.transaction;

import com.uifuture.springbootblockchain.util.BtcAddressUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * 交易输入
 *
 * @author chenhx
 * @version TXInput.java, v 0.1 2018-10-15 下午 6:23
 */
@Data
public class TXInput {

    /**
     * 交易Id的hash值
     * 包含了它所指向的UTXO的交易的Hash值。
     */
    private byte[] txId;
    /**
     * 交易输出索引
     * 定义了它所指向的UTXO在上一笔交易中交易输出数组的位置。
     */
    private int txOutputIndex;
    /**
     * 签名
     */
    private byte[] signature;
    /**
     * 公钥
     */
    private byte[] pubKey;

    public TXInput() {
    }

    public TXInput(byte[] txId, int txOutputIndex, byte[] signature, byte[] pubKey) {
        this.txId = txId;
        this.txOutputIndex = txOutputIndex;
        this.signature = signature;
        this.pubKey = pubKey;
    }

    /**
     * 检查公钥hash是否用于交易输入
     *
     * @param pubKeyHash
     * @return
     */
    public boolean usesKey(byte[] pubKeyHash) {
        byte[] lockingHash = BtcAddressUtils.ripeMD160Hash(this.getPubKey());
        return Arrays.equals(lockingHash, pubKeyHash);
    }


    public static void main(String[] args) {
        byte[] pubKey = {84, 104, 101, 32, 84, 105, 109, 101, 115, 32, 50, 48, 50, 50, 45, 48, 54, 45, 48, 49, 32, 49, 55, 58, 49, 48, 58, 48, 55, 32, 51, 54, 52, 32, 67, 104, 97, 110, 99, 101, 108, 108, 111, 114, 32, 111, 110, 32, 98, 114, 105, 110, 107, 32, 111, 102, 32, 115, 101, 99, 111, 110, 100, 32, 98, 97, 105, 108, 111, 117, 116, 32, 102, 111, 114, 32, 98, 97, 110, 107, 115};
        System.out.println(new String(pubKey));
    }
}
