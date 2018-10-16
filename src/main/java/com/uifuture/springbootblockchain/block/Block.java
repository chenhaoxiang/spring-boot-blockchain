/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.block;

import com.uifuture.springbootblockchain.transaction.entity.Transaction;
import com.uifuture.springbootblockchain.util.ByteUtils;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * 区块
 * 暂定区块体积为4M
 * @author chenhx
 * @version Block.java, v 0.1 2018-10-11 下午 9:16
 */
@Data
public class Block {
    /**
     * 工作量证明计数器
     */
    private BigInteger nonce;
    /**
     * 区块hash值
     */
    private String hash;
    /**
     * 前一个区块的hash值
     */
    private String prevBlockHash;
    /**
     * 区块数据（交易数据）
     */
    private Transaction[] transactions;
    /**
     * 区块创建时间(单位:ns)
     */
    private long timeStamp;
    /**
     * 当前难度值
     */
    private BigInteger target;
    /**
     * 创世区块hash值,起源hash
     */
    private String genesisHash;

    public Block(String prevBlockHash) {
        this.prevBlockHash = prevBlockHash;
    }

    /**
     * 对区块中的交易信息进行Hash计算
     *
     * @return
     */
    public byte[] hashTransaction() {
        byte[][] txIdArrays = new byte[this.getTransactions().length][];
        for (int i = 0; i < this.getTransactions().length; i++) {
            txIdArrays[i] = this.getTransactions()[i].getTxId();
        }
        return DigestUtils.sha256(ByteUtils.merge(txIdArrays));
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Block{");
        sb.append(super.toString());
        sb.append(",");
        sb.append("nonce=").append(nonce);
        sb.append(", hash='").append(hash).append('\'');
        sb.append(", prevBlockHash='").append(prevBlockHash).append('\'');
        sb.append(", transactions=").append(Arrays.toString(transactions));
        sb.append(", timeStamp=").append(timeStamp);
        sb.append(", target=").append(target);
        sb.append(", genesisHash='").append(genesisHash).append('\'');
        sb.append('}');
        return sb.toString();
    }
}