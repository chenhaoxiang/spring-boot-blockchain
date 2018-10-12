/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.block;

import com.uifuture.springbootblockchain.pow.PowResult;
import com.uifuture.springbootblockchain.pow.ProofOfWork;
import com.uifuture.springbootblockchain.util.ByteUtils;
import lombok.Data;

import java.time.Instant;

/**
 * 区块
 * @author chenhx
 * @version Block.java, v 0.1 2018-10-11 下午 9:16
 */
@Data
public class Block {
    public Block() {
    }

    public Block(String hash, String prevBlockHash, String data, long timeStamp,long nonce) {
        this();
        this.hash = hash;
        this.prevBlockHash = prevBlockHash;
        this.data = data;
        this.timeStamp = timeStamp;
        this.nonce = nonce;
    }
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
     * 工作量证明计数器
     */
    private long nonce;

    /**
     * <p> 创建创世区块 </p>
     *
     * @return
     */
    public static Block newGenesisBlock() {
        return Block.newBlock(ByteUtils.ZERO_HASH, "Genesis Block");
    }

    /**
     * <p> 创建新区块 </p>
     *
     * @param previousHash
     * @param data
     * @return
     */
    public static Block newBlock(String previousHash, String data) {
        Block block = new Block("", previousHash, data, Instant.now().getEpochSecond(), 0);
        ProofOfWork pow = ProofOfWork.newProofOfWork(block);
        //开始挖矿
        PowResult powResult = pow.run();
        block.setHash(powResult.getHash());
        block.setNonce(powResult.getNonce());
        return block;
    }

}