/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.blockchainspringboot;

import com.uifuture.springbootblockchain.cli.CLI;

/**
 * 测试
 *
 * @author chenhx
 * @version BlockchainTest.java, v 0.1 2018-10-11 下午 9:23
 */
public class BlockchainTest {

    public static void main(String[] args) {
        // 1AycK28gqDpWJ4e6oo2oqG9Smr43K3QcTW 200 100
        // 15J63sETC6WuoVKZbtKxbWXsiA9W6THRAv
        try {
//            String[] argss = {"createwallet"};
//            String[] argss = {"createblockchain", "-address", "1AycK28gqDpWJ4e6oo2oqG9Smr43K3QcTW"};
            //获取地址
//            String[] argss = {"printaddresses"};
            //获取钱包余额
//            String[] argss = {"getbalance", "-address", "1AycK28gqDpWJ4e6oo2oqG9Smr43K3QcTW"};
//            String[] argss = {"send", "-from", "1AycK28gqDpWJ4e6oo2oqG9Smr43K3QcTW"
//                    , "-to", "15J63sETC6WuoVKZbtKxbWXsiA9W6THRAv", "-amount", "180000235"};
            //打印链
            String[] argss = {"printchain"};
//            String[] argss = {"mining", "-address", "1AycK28gqDpWJ4e6oo2oqG9Smr43K3QcTW"};
            CLI cli = new CLI(argss);
            cli.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
