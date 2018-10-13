/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.block;

import lombok.Data;

import java.math.BigInteger;

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
    private String data;
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
}