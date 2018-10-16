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
        // 12dLri3xQY3CCdZC6qpAULsUZCVhfkWQyr
        try {
//            String[] argss = {"createwallet"};
//            String[] argss = {"createblockchain", "-address", "12dLri3xQY3CCdZC6qpAULsUZCVhfkWQyr"};
//            String[] argss = {"printaddresses"};
            String[] argss = {"getbalance", "-address", "12dLri3xQY3CCdZC6qpAULsUZCVhfkWQyr"};
//            String[] argss = {"send", "-from", "1BjZzivUJzGRt6VqNkX8vZ3DVbVwLwETpR", "-to", "1NPeKwxHehoWK1SY6LqHxgVYsB7Ud65BkP", "-amount", "1"};
//            String[] argss = {"printchain"};
            //TODO 挖矿需要修改，前面的没有增加上去
//            String[] argss = {"mining", "-address", "12dLri3xQY3CCdZC6qpAULsUZCVhfkWQyr"};

            CLI cli = new CLI(argss);
            cli.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
