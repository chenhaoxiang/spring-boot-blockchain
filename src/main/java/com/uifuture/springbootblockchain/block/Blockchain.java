/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.block;

import com.uifuture.springbootblockchain.bd.RocksDB;
import com.uifuture.springbootblockchain.util.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 区块链
 * AllArgsConstructor 所有参数的构造函数
 * @author chenhx
 * @version Blockchain.java, v 0.1 2018-10-11 下午 9:20
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blockchain {

    private String lastBlockHash;

    /**
     * <p> 创建区块链 </p>
     *
     * @return
     */
    public static Blockchain newBlockchain(RocksDB rocksDB) {
        String lastBlockHash = rocksDB.getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            Block genesisBlock = Block.newGenesisBlock();
            lastBlockHash = genesisBlock.getHash();
            rocksDB.putBlock(genesisBlock);
            rocksDB.putLastBlockHash(lastBlockHash);
        }
        return new Blockchain(lastBlockHash);
    }

    /**
     * <p> 添加区块  </p>
     *
     * @param data
     */
    public void addBlock(String data,RocksDB rocksDB) throws Exception {
        String lastBlockHash = rocksDB.getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            throw new Exception("Fail to add block into blockchain ! ");
        }
        this.addBlock(Block.newBlock(lastBlockHash, data),rocksDB);
    }

    /**
     * <p> 添加区块  </p>
     *
     * @param block
     */
    public void addBlock(Block block,RocksDB rocksDB) {
        rocksDB.putLastBlockHash(block.getHash());
        rocksDB.putBlock(block);
        this.lastBlockHash = block.getHash();
    }


    /**
     * 区块链迭代器
     */
    public class BlockchainIterator {

        private String currentBlockHash;

        public BlockchainIterator(String currentBlockHash) {
            this.currentBlockHash = currentBlockHash;
        }

        /**
         * 是否有下一个区块
         *
         * @return
         */
        public boolean hashNext(RocksDB rocksDB) {
            if (ByteUtils.ZERO_HASH.equals(currentBlockHash)) {
                return false;
            }
            Block lastBlock = rocksDB.getBlock(currentBlockHash);
            if (lastBlock == null) {
                return false;
            }
            // 创世区块直接放行
            if (ByteUtils.ZERO_HASH.equals(lastBlock.getPrevBlockHash())) {
                return true;
            }
            return rocksDB.getBlock(lastBlock.getPrevBlockHash()) != null;
        }


        /**
         * 返回区块
         *
         * @return
         */
        public Block next(RocksDB rocksDB) {
            Block currentBlock = rocksDB.getBlock(currentBlockHash);
            if (currentBlock != null) {
                this.currentBlockHash = currentBlock.getPrevBlockHash();
                return currentBlock;
            }
            return null;
        }
    }

    public BlockchainIterator getBlockchainIterator() {
        return new BlockchainIterator(lastBlockHash);
    }

}