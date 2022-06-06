/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.block;
import byte[];

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.uifuture.springbootblockchain.bd.RocksDBUtils;
import com.uifuture.springbootblockchain.transaction.Transaction;
import com.uifuture.springbootblockchain.util.ByteUtils;
import com.uifuture.springbootblockchain.util.SerializeUtils;
import com.uifuture.springbootblockchain.util.Sha256Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 区块链
 * AllArgsConstructor 所有参数的构造函数
 * @author chenhx
 * @version Blockchain.java, v 0.1 2018-10-11 下午 9:20
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Blockchain {

    private String lastBlockHash;

    /**
     * 从 DB 中恢复区块链数据
     *
     * @return
     */
    public static Blockchain initBlockchainFromDB() {
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();
        if (lastBlockHash == null) {
            throw new RuntimeException("ERROR: Fail to init blockchain from db. ");
        }
        return new Blockchain(lastBlockHash);
    }


    /**
     * <p> 创建区块链 </p>
     *
     * @param address 钱包地址
     * @param steams 数字藏品的字节流的md5
     * @return
     */
    public static Blockchain createBlockchain(String address,String steams) {
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            Transaction coinbase = new Transaction();
            coinbase.setFrom("00000000000000000000000000000000");
            coinbase.setTo(address);
            coinbase.setData(new String(DigestUtils.sha256(SerializeUtils.serialize(steams)), StandardCharsets.UTF_8));
            coinbase.setCreateTime(0L);
            coinbase.setHash(coinbase.hash());

            Block genesisBlock = Block.newGenesisBlock(coinbase);
            lastBlockHash = genesisBlock.getHash();
            RocksDBUtils.getInstance().putBlock(genesisBlock);
            RocksDBUtils.getInstance().putLastBlockHash(lastBlockHash);
        }
        return new Blockchain(lastBlockHash);
    }

    /**
     * 打包交易，进行挖矿
     *
     * @param transactions
     */
    public Block mineBlock(Transaction[] transactions) {
        // 挖矿前，先验证交易记录
        for (Transaction tx : transactions) {
            if (!this.verifyTransactions(tx)) {
                log.error("ERROR: Fail to mine block ! Invalid transaction ! tx=" + tx.toString());
                throw new RuntimeException("ERROR: Fail to mine block ! Invalid transaction ! ");
            }
        }
        Block lastBlock = RocksDBUtils.getInstance().getLastBlock();
        //创建新区块
        Block block = Block.newBlock(lastBlockHash, transactions, lastBlock.getHeight() + 1);
        this.addBlock(block);
        return block;
    }

    /**
     * <p> 添加区块  </p>
     *
     * @param block
     */
    private void addBlock(Block block) {
        RocksDBUtils.getInstance().putLastBlockHash(block.getHash());
        RocksDBUtils.getInstance().putBlock(block);
        this.lastBlockHash = block.getHash();
    }

    /**
     * <p> 添加区块  </p>
     *
     * @param block
     */
    public void saveBlock(Block block) {
        Block existBlock = RocksDBUtils.getInstance().getBlock(block.getHash());
        if (existBlock != null) {
            return;
        }
        // 保存区块数据
        RocksDBUtils.getInstance().putBlock(block);
        Block lastBlock = RocksDBUtils.getInstance().getLastBlock();

        if (block.getHeight() > lastBlock.getHeight()) {
            RocksDBUtils.getInstance().putLastBlockHash(block.getHash());
            this.lastBlockHash = block.getHash();
        }
    }

    /**
     * 获取区块链迭代器
     *
     * @return
     */
    public BlockchainIterator getBlockchainIterator() {
        return new BlockchainIterator(lastBlockHash);
    }

    /**
     * 查找所有的 交易
     *
     * @return
     */
    public Map<String, Transaction[]> findAllUTXOs() {
        Map<String, Transaction[]> allUTXOs = Maps.newHashMap();
        // 再次遍历所有区块中的交易输出
        for (BlockchainIterator blockchainIterator = this.getBlockchainIterator(); blockchainIterator.hashNext(); ) {
            Block block = blockchainIterator.next();
            allUTXOs.put(block.getHash(), block.getTransactions());
        }
        return allUTXOs;
    }

    /**
     * 依据交易hash查询交易信息
     *
     * @param hash 交易hash
     * @return
     */
    private Transaction findTransaction(String hash) {
        for (BlockchainIterator iterator = this.getBlockchainIterator(); iterator.hashNext(); ) {
            Block block = iterator.next();
            for (Transaction tx : block.getTransactions()) {
                if (tx.getHash().equals(hash)) {
                    return tx;
                }
            }
        }
        throw new RuntimeException("ERROR: Can not found tx by txId ! ");
    }

    /**
     * 单次交易签名
     * @param tx         交易数据
     * @param privateKey 私钥
     */
    public void signTransaction(Transaction tx, BCECPrivateKey privateKey) throws Exception {
        // 先来找到这笔新的交易中，交易输入所引用的前面的多笔交易的数据
        Map<String, Transaction> prevTxMap = Maps.newHashMap();
        Transaction prevTx = this.findTransaction(tx.getTxId());
        prevTxMap.put(Hex.encodeHexString(tx.getTxId()), prevTx);
        tx.sign(privateKey, prevTxMap);
    }

    /**
     * 交易签名验证
     *
     * @param tx
     */
    public boolean verifyTransactions(Transaction tx) {
        if (tx.isCoinbase()) {
            return true;
        }
        Map<String, Transaction> prevTx = Maps.newHashMap();
        Transaction transaction = this.findTransaction(tx.getTxId());
        prevTx.put(Hex.encodeHexString(tx.getTxId()), transaction);
        try {
            return tx.verify(prevTx);
        } catch (Exception e) {
            log.error("Fail to verify transaction ! transaction invalid ! ", e);
            throw new RuntimeException("Fail to verify transaction ! transaction invalid ! ", e);
        }
    }

    /**
     * 获取本地节点区块的最大高度
     *
     * @return
     */
    public long getBestHeight() {
        Block lastBlock = RocksDBUtils.getInstance().getLastBlock();
        return lastBlock.getHeight();
    }

    /**
     * 获取区块链中所有区块的hash值
     *
     * @return
     */
    public List<String> getAllBlockHash() {
        List<String> blockHashes = Lists.newArrayList();
        for (BlockchainIterator blockchainIterator = this.getBlockchainIterator(); blockchainIterator.hashNext(); ) {
            Block block = blockchainIterator.next();
            blockHashes.add(block.getHash());
        }
        return blockHashes;
    }

    /**
     * 根据hash查询区块
     *
     * @param hash
     * @return
     */
    public Block getBlockByHash(String hash) {
        return RocksDBUtils.getInstance().getBlock(hash);
    }

    /**
     * 区块链迭代器
     */
    public class BlockchainIterator {

        private String currentBlockHash;

        private BlockchainIterator(String currentBlockHash) {
            this.currentBlockHash = currentBlockHash;
        }

        /**
         * 是否有下一个区块
         *
         * @return
         */
        public boolean hashNext() {
            if (ByteUtils.ZERO_HASH.equals(currentBlockHash)) {
                return false;
            }
            Block lastBlock = RocksDBUtils.getInstance().getBlock(currentBlockHash);
            if (lastBlock == null) {
                return false;
            }
            // 创世区块直接放行
            if (ByteUtils.ZERO_HASH.equals(lastBlock.getPrevBlockHash())) {
                return true;
            }
            return RocksDBUtils.getInstance().getBlock(lastBlock.getPrevBlockHash()) != null;
        }

        /**
         * 返回区块
         *
         * @return
         */
        public Block next() {
            Block currentBlock = RocksDBUtils.getInstance().getBlock(currentBlockHash);
            if (currentBlock != null) {
                this.currentBlockHash = currentBlock.getPrevBlockHash();
                return currentBlock;
            }
            return null;
        }
    }

}
