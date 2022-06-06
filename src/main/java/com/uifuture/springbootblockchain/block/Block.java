/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.block;

import com.uifuture.springbootblockchain.pow.PowResult;
import com.uifuture.springbootblockchain.pow.ProofOfWork;
import com.uifuture.springbootblockchain.transaction.MerkleTree;
import com.uifuture.springbootblockchain.transaction.Transaction;
import com.uifuture.springbootblockchain.util.ByteUtils;
import com.uifuture.springbootblockchain.util.SerializeUtils;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * 区块
 * 暂定区块体积为4M
 * @author chenhx
 * @version Block.java, v 0.1 2018-10-11 下午 9:16
 */
@Slf4j
@Data
@ToString
public class Block {
    private String version = "1.0.0";
    /**
     * 区块hash值
     */
    private String hash;
    /**
     * 前一个区块的hash值
     */
    private String prevBlockHash;
    /**
     * 藏品信息
     */
    private Transaction[] transactions;
    /**
     * 区块创建时间(单位:毫秒)
     */
    private long timeStamp;
    /**
     * 区块高度
     */
    private int height;
    /**
     * 创世区块的hash
     */
    private String rootHash;
    /**
     * 本区块的签名，需要结合之前的区块
     * 历史的区块数据 每次md5 + 本数据 + 输入人的私钥 进行MD5后再 sha256
     */
    private String signature;
    /**
     * <p> 创建创世区块 </p>
     *
     * @param coinbase
     * @return
     */
    public static Block newGenesisBlock(Transaction coinbase) {
        return Block.newBlock(ByteUtils.ZERO_HASH, new Transaction[]{coinbase}, 0);
    }

    /**
     * <p> 创建新区块 </p>
     *
     * @param previousHash
     * @param transactions
     * @return
     */
    public static Block newBlock(String previousHash, Transaction[] transactions, int height) {
        Block block = new Block();
        block.setPrevBlockHash(previousHash);
        block.setTimeStamp(System.currentTimeMillis());
        block.setHeight(height);
        block.setTransactions(transactions);
        block.setSignature(block.hashSignature());
        log.info("block={}", JSONObject.valueToString(block));

        ProofOfWork pow = ProofOfWork.newProofOfWork(block);
        //计算
        PowResult powResult = pow.run();
        block.setHash(powResult.getHash());
        log.info("block={}", block);
        return block;
    }

    /**
     * TODO 对区块中的藏品信息进行Hash计算,签名
     *
     * @return
     */
    public String hashSignature() {
        Transaction[] transaction = this.getTransactions();
        StringBuilder hashs = new StringBuilder();
        for (Transaction transaction1 : transaction) {
            hashs.append(transaction1.getHash());
        }
        return new String(DigestUtils.sha256(SerializeUtils.serialize(hashs.toString())), StandardCharsets.UTF_8);
    }
}
