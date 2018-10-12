/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.bd;

import com.google.common.collect.Maps;
import com.uifuture.springbootblockchain.block.Block;
import com.uifuture.springbootblockchain.util.SerializeUtils;
import com.uifuture.springbootblockchain.util.redis.RedisHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 存储
 * @author chenhx
 * @version RocksDB.java, v 0.1 2018-10-11 下午 9:28
 */
@Component
public class RocksDB {
    @Autowired
    private RedisHandle redisHandle;
    /**
     * 区块链数据文件
     */
    private static final String DB_FILE = "blockchain.db";
    /**
     * 区块桶前缀
     */
    private static final String BLOCKS_BUCKET_KEY = "blocks";
    /**
     * 最新一个区块
     */
    private static final String LAST_BLOCK_KEY = "l";

    /**
     * block buckets
     */
    private Map<String, byte[]> blocksBucket;

    public RocksDB(RedisHandle redisHandle) {
        initBlockBucket(redisHandle);
    }

    /**
     * 初始化 blocks 数据桶
     */
    private void initBlockBucket(RedisHandle redisHandle) {
        try {
            byte[] blockBucketKey = (byte[]) redisHandle.get(BLOCKS_BUCKET_KEY);
            if (blocksBucket == null)  {
                blocksBucket = Maps.newHashMap();
                redisHandle.set(BLOCKS_BUCKET_KEY, SerializeUtils.serialize(blocksBucket));
            }else {
                blocksBucket = (Map<String, byte[]>)SerializeUtils.deserialize(blockBucketKey);
            }
        } catch (Exception e) {
            throw new RuntimeException("Fail to init block bucket ! ", e);
        }
    }

    /**
     * 保存最新一个区块的Hash值
     *
     * @param tipBlockHash
     */
    public void putLastBlockHash(String tipBlockHash) {
        try {
            blocksBucket.put(LAST_BLOCK_KEY, SerializeUtils.serialize(tipBlockHash));
            redisHandle.set(BLOCKS_BUCKET_KEY, SerializeUtils.serialize(blocksBucket));
        } catch (Exception e) {
            throw new RuntimeException("Fail to put last block hash ! ", e);
        }
    }

    /**
     * 查询最新一个区块的Hash值
     *
     * @return
     */
    public String getLastBlockHash() {
        byte[] lastBlockHashBytes = blocksBucket.get(LAST_BLOCK_KEY);
        if (lastBlockHashBytes != null) {
            return (String) SerializeUtils.deserialize(lastBlockHashBytes);
        }
        return "";
    }

    /**
     * 保存区块
     *
     * @param block
     */
    public void putBlock(Block block) {
        try {
            blocksBucket.put(block.getHash(), SerializeUtils.serialize(block));
            redisHandle.set(BLOCKS_BUCKET_KEY, SerializeUtils.serialize(blocksBucket));
        } catch (Exception e) {
            throw new RuntimeException("Fail to put block ! ", e);
        }
    }

    /**
     * 查询区块
     *
     * @param blockHash
     * @return
     */
    public Block getBlock(String blockHash) {
        return (Block) SerializeUtils.deserialize(blocksBucket.get(blockHash));
    }

}