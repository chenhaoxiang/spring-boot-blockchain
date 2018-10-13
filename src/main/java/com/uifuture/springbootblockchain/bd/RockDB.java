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
 * 暂时选择Redis，但是后期需要更改数据库，所以一定要做到数据库存储之间的兼容
 * 暂定10分钟生成一个区块
 * @author chenhx
 * @version RocksDB.java, v 0.1 2018-10-13 下午 4:46
 */
@Component
public class RockDB {
    @Autowired
    private RedisHandle redisHandle;
    /**
     * 区块链数据文件
     */
    private static final String DB_FILE = "blockchain.db";
    /**
     * 区块桶key
     */
    private static final String BLOCKS_BUCKET_KEY = DB_FILE + ".blocks";
    /**
     * 最新一个区块hash
     */
    private static final String LAST_BLOCK_HASH_KEY = "l";
    /**
     * 最新一个区块
     */
    private static final String LAST_BLOCK_KEY = "2";
    /**
     * 创世区块 genesisHash
     */
    private static final String BLOCK_GENESIS_HASH_KEY = "3";

    /**
     * 区块桶 支付简单数据
     */
    private static final String BLOCKS_BUCKET_PAY_KEY = BLOCKS_BUCKET_KEY + ".pay";

    private Map<String, byte[]> blocksBucketPay;
    /**
     * block buckets
     */
    private Map<String, byte[]> blocksBucket;

    public RockDB(RedisHandle redisHandle) {
        initBlockBucket(redisHandle);
    }

    /**
     * 初始化 blocks 数据桶
     * 初始化区块链以及当前的交易信息列表
     */
    private void initBlockBucket(RedisHandle redisHandle) {
        try {
            byte[] blockBucketByte = (byte[]) redisHandle.get(BLOCKS_BUCKET_KEY);
            if (blockBucketByte == null) {
                blocksBucket = Maps.newHashMap();
                blocksBucketPay = Maps.newHashMap();
                //TODO 分布式从其他注册的节点获取

                redisHandle.set(BLOCKS_BUCKET_KEY, SerializeUtils.serialize(blocksBucket));
                redisHandle.set(BLOCKS_BUCKET_PAY_KEY, SerializeUtils.serialize(blocksBucketPay));
            }else {
                System.out.println("初始化时获取的数据大小:" + blockBucketByte.length);
                blocksBucket = (Map<String, byte[]>) SerializeUtils.deserialize(blockBucketByte);
                byte[] blockBucketPayByte = (byte[]) redisHandle.get(BLOCKS_BUCKET_PAY_KEY);
                blocksBucketPay = (Map<String, byte[]>) SerializeUtils.deserialize(blockBucketPayByte);
            }
        } catch (Exception e) {
            throw new RuntimeException("Fail to init block bucket ! ", e);
        }
    }

    /**
     * 保存区块
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
     * @param blockHash
     * @return
     */
    public Block getBlock(String blockHash) {
        return (Block) SerializeUtils.deserialize(blocksBucket.get(blockHash));
    }


    /**
     * 保存最新一个区块的Hash值
     *
     * @param tipBlockHash
     */
    public void putLastBlockHash(String tipBlockHash) {
        try {
            blocksBucketPay.put(LAST_BLOCK_HASH_KEY, SerializeUtils.serialize(tipBlockHash));
            redisHandle.set(BLOCKS_BUCKET_PAY_KEY, SerializeUtils.serialize(blocksBucketPay));
        } catch (Exception e) {
            throw new RuntimeException("Fail to put last block hash ! ", e);
        }
    }

    /**
     * 查询最新的一个区块的Hash值
     *
     * @return
     */
    public String getLastBlockHash() {
        byte[] lastBlockHashBytes = blocksBucketPay.get(LAST_BLOCK_HASH_KEY);
        if (lastBlockHashBytes != null) {
            return (String) SerializeUtils.deserialize(lastBlockHashBytes);
        }
        return "";
    }

    /**
     * 查询最新的一个区块
     *
     * @return
     */
    public Block getLastBlock() {
        byte[] lastBlockHashBytes = blocksBucketPay.get(LAST_BLOCK_KEY);
        if (lastBlockHashBytes != null) {
            return (Block) SerializeUtils.deserialize(lastBlockHashBytes);
        }
        return null;
    }

    /**
     * 保存最新一个区块
     *
     * @param tipBlock
     */
    public void putLastBlock(Block tipBlock) {
        try {
            blocksBucketPay.put(LAST_BLOCK_KEY, SerializeUtils.serialize(tipBlock));
            redisHandle.set(BLOCKS_BUCKET_PAY_KEY, SerializeUtils.serialize(blocksBucketPay));
        } catch (Exception e) {
            throw new RuntimeException("Fail to put last block hash ! ", e);
        }
    }

    /**
     * 查询创世区块hash
     * genesis
     *
     * @return
     */
    public String getGenesisBlockHash() {
        byte[] lastBlockHashBytes = blocksBucketPay.get(BLOCK_GENESIS_HASH_KEY);
        if (lastBlockHashBytes != null) {
            return (String) SerializeUtils.deserialize(lastBlockHashBytes);
        }
        return "";
    }

    /**
     * 保存创世区块hash
     *
     * @param hash
     */
    public void putGenesisBlockHash(String hash) {
        try {
            blocksBucketPay.put(BLOCK_GENESIS_HASH_KEY, SerializeUtils.serialize(hash));
            redisHandle.set(BLOCKS_BUCKET_PAY_KEY, SerializeUtils.serialize(blocksBucketPay));
        } catch (Exception e) {
            throw new RuntimeException("Fail to put last block hash ! ", e);
        }
    }


    /**
     * 获取区块链
     *
     * @return property value of blocksBucket
     */
    public Map<String, byte[]> getBlocksBucket() {
        return blocksBucket;
    }

    /**
     * 重新设置区块链
     *
     * @param blocksBucket value to be assigned to property blocksBucket
     */
    public void setBlocksBucket(Map<String, byte[]> blocksBucket) {
        this.blocksBucket = blocksBucket;
    }

    /**
     * Getter method for property <tt>blocksBucketPay</tt>.
     *
     * @return property value of blocksBucketPay
     */
    public Map<String, byte[]> getBlocksBucketPay() {
        return blocksBucketPay;
    }

    /**
     * Setter method for property <tt>blocksBucketPay</tt>.
     *
     * @param blocksBucketPay value to be assigned to property blocksBucketPay
     */
    public void setBlocksBucketPay(Map<String, byte[]> blocksBucketPay) {
        this.blocksBucketPay = blocksBucketPay;
    }


}