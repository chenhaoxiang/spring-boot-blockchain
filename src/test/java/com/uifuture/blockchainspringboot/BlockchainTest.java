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
        try {
            //创建钱包
//            String[] argss = {"createwallet"}; //18:35:21.926 [main] INFO com.uifuture.springbootblockchain.cli.CLI - wallet address : 18jmaiDq5bmGM1RUtYc7BnaFwxoGfkwSLT 18CYfSJJAPyhzfskzUtny8hQRcdokDmRaM
            //创建区块链
//            String[] argss = {"createblockchain", "-address", "18jmaiDq5bmGM1RUtYc7BnaFwxoGfkwSLT"};
            // 打印所有钱包地址
//            String[] argss = {"printaddresses"};
            //获取钱包余额 13Fg3H2DQaDHSCoYEj3Y11Tq7Q93ooxAmA 200000000
            //获取钱包余额 164mY79vVpcPhB8mjXmCLvjTmceazLfTTF 0
//            String[] argss = {"getbalance", "-address", "164mY79vVpcPhB8mjXmCLvjTmceazLfTTF"};
            //交易
            String[] argss = {"send", "-from", "18jmaiDq5bmGM1RUtYc7BnaFwxoGfkwSLT", "-to", "18CYfSJJAPyhzfskzUtny8hQRcdokDmRaM", "-amount", "1000"};
            //打印链
//            String[] argss = {"printchain"};
            //进行挖区块
//            String[] argss = {"mining", "-address", "164mY79vVpcPhB8mjXmCLvjTmceazLfTTF"};
            //帮助
//            String[] argss = {"h"};
            CLI cli = new CLI(argss);
            cli.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
