/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.block;

import com.uifuture.springbootblockchain.bd.RockDB;
import com.uifuture.springbootblockchain.util.ByteUtils;

/**
 * 区块链迭代器
 *
 * @author chenhx
 * @version BlockchainIterator.java, v 0.1 2018-10-15 下午 7:00
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
    public boolean hashNext(RockDB rockDB) {
        if (ByteUtils.GENESIS_ZERO_HASH.equals(currentBlockHash)) {
            return false;
        }
        Block lastBlock = rockDB.getBlock(currentBlockHash);
        if (lastBlock == null) {
            return false;
        }
        // 创世区块直接放行
        if (ByteUtils.GENESIS_ZERO_HASH.equals(lastBlock.getPrevBlockHash())) {
            return true;
        }
        return rockDB.getBlock(lastBlock.getPrevBlockHash()) != null;
    }


    /**
     * 返回区块
     *
     * @return
     */
    public Block next(RockDB rockDB) {
        Block currentBlock = rockDB.getBlock(currentBlockHash);
        if (currentBlock != null) {
            this.currentBlockHash = currentBlock.getPrevBlockHash();
            return currentBlock;
        }
        return null;
    }
}