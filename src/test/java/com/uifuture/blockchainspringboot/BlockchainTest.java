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

    @Test
    public void testCli() {
        Blockchain blockChain = Blockchain.getInstance(rockDB);
        System.out.println("当前区块:" + blockChain.newGenesisBlock());
        System.out.println("----------------");
        System.out.println("当前区块:" + blockChain.newBlock());
        System.out.println("----------------");
        System.out.println("当前区块:" + blockChain.newBlock());
        System.out.println("----------------");
        System.out.println("当前区块:" + blockChain.newBlock());
        System.out.println("----------------");
        System.out.println("当前区块:" + blockChain.newBlock());
        System.out.println("----------------");
        System.out.println("----------------");
        System.out.println("创世区块:" + rockDB.getGenesisBlockHash());
        System.out.println("----------------");
        System.out.println("上一个区块:" + rockDB.getLastBlock());
        System.out.println("----------------");
        System.out.println("上一个区块Hash:" + rockDB.getLastBlockHash());
        // 查看整个区块链大小
        System.out.println("length:" + rockDB.getBlocksBucket().size());
    }
}