/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.blockchainspringboot;

import com.uifuture.springbootblockchain.bd.RocksDB;
import com.uifuture.springbootblockchain.cli.CLI;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 测试
 * @author chenhx
 * @version BlockchainTest.java, v 0.1 2018-10-11 下午 9:23
 */
public class TestBlockchain extends SpringBootBlockchainApplicationTests {

    @Autowired
    private RocksDB rocksDB;
    @Test
    public void testCli() {
        try {
//            String[] args = {"-addblock", "Send 2.0 BTC to chenhx"};
            String args[] = {"-print"};
            CLI cli = new CLI(args);
            cli.parse(rocksDB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}