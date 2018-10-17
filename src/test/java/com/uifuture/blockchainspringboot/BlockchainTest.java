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
        // 1GurUqY7GTXX3HhcCAXiptqNC2pGW4tkwE   200
        //15VZSPVaoyCKQxzERi5mvx1iYH8v2GUEJd  200
        try {
//            String[] argss = {"createwallet"};
//            String[] argss = {"createblockchain", "-address", "1GurUqY7GTXX3HhcCAXiptqNC2pGW4tkwE"};
            //获取地址
//            String[] argss = {"printaddresses"};
            //获取钱包余额
//            String[] argss = {"getbalance", "-address", "1GurUqY7GTXX3HhcCAXiptqNC2pGW4tkwE"};
//            String[] argss = {"send", "-from", "1GurUqY7GTXX3HhcCAXiptqNC2pGW4tkwE"
//                    , "-to", "15VZSPVaoyCKQxzERi5mvx1iYH8v2GUEJd", "-amount", "400000000"};
            //打印链
//            String[] argss = {"printchain"};
            String[] argss = {"mining", "-address", "1GurUqY7GTXX3HhcCAXiptqNC2pGW4tkwE"};
            CLI cli = new CLI(argss);
            cli.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
