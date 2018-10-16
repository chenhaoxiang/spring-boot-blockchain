/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.blockchainspringboot;

import com.uifuture.springbootblockchain.bd.RockDB;
import com.uifuture.springbootblockchain.block.Blockchain;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 测试
 *
 * @author chenhx
 * @version BlockchainTest.java, v 0.1 2018-10-11 下午 9:23
 */
public class BlockchainTest extends SpringBootBlockchainApplicationTests {

    @Autowired
    private RockDB rockDB;

    /**
     * 新地址  1
     */
    @Test
    public void testCli() {
        Blockchain blockChain = Blockchain.getInstance(rockDB, "1314520ab0145711d0e866d98c1ea0911e5f2ffb75c9ea6931de917ec3667775");
        System.out.println("当前区块:" + rockDB.getLastBlock());
        //转给地址 1,1000000  1UB


//        System.out.println("----------------");
//        System.out.println("当前区块:" + blockChain.newBlock(new Transaction[]{}));
//        System.out.println("----------------");
//        System.out.println("当前区块:" + blockChain.newBlock(new Transaction[]{}));
//        System.out.println("----------------");
//        System.out.println("当前区块:" + blockChain.newBlock(new Transaction[]{}));
//        System.out.println("----------------");
//        System.out.println("当前区块:" + blockChain.newBlock(new Transaction[]{}));
//        System.out.println("----------------");
//        System.out.println("----------------");
//        System.out.println("创世区块:" + rockDB.getGenesisBlockHash());
//        System.out.println("----------------");
//        System.out.println("上一个区块:" + rockDB.getLastBlock());
//        System.out.println("----------------");
        System.out.println("上一个区块Hash:" + rockDB.getLastBlockHash());
        // 查看整个区块链大小
        System.out.println("length:" + rockDB.getBlocksBucket().size());
    }
}