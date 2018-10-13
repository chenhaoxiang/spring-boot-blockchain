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
        System.out.println("----------------");
        blockChain.newBlock();
        System.out.println("----------------");
        blockChain.newBlock();
        System.out.println("----------------");
        blockChain.newBlock();
        System.out.println("----------------");
        blockChain.newBlock();
        System.out.println("----------------");
        // 查看整个区块链大小
        System.out.println("length:" + rockDB.getBlocksBucket().size());
        System.out.println("----------------");
    }
}