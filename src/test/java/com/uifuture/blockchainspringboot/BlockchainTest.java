/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.blockchainspringboot;

import cn.hutool.core.io.FileUtil;
import com.uifuture.springbootblockchain.cli.CLI;
import com.uifuture.springbootblockchain.util.SerializeUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 测试
 *
 * @author chenhx
 * @version BlockchainTest.java, v 0.1 2018-10-11 下午 9:23
 */
public class BlockchainTest {

    public static void main(String[] args) {
        // 1AycK28gqDpWJ4e6oo2oqG9Smr43K3QcTW 219999765  179999060
        // 15J63sETC6WuoVKZbtKxbWXsiA9W6THRAv 180000235  540000705
        try {
            //读取图片流
            byte[] files = FileUtil.readBytes("/Users/chenhx/Desktop/a/anyi.png");
            String hash = DigestUtils.sha256Hex(files);
            //6bc1b805ac4f5c8ab19a316334388cc6cc1332d0db1b758f3609608230ca9975
            System.out.println("图片Hash："+hash);

            //创建钱包
//            String[] argss = {"createwallet"}; //187LFoek3DRL9RJKigh7EDLieyXc93A5W8 1NhdZqBXgYjor7opMpf77L4MLTnQh2ZBFT
            //创建区块链
//            String[] argss = {"createblockchain", "-address", "187LFoek3DRL9RJKigh7EDLieyXc93A5W8","-amount", hash};
            // 打印所有钱包地址
//            String[] argss = {"printaddresses"};
            //获取钱包藏品的交易记录
//            String[] argss = {"getbalance", "-address", "1MUjVDaxnD3dnKQ5MZXGVycckvTzA6kdmH"};
            //校验这个藏品是不是这个钱包的
//            String[] argss = {"check", "-address", "187LFoek3DRL9RJKigh7EDLieyXc93A5W8", "-amount", hash};
//            String[] argss = {"check", "-address", "1NhdZqBXgYjor7opMpf77L4MLTnQh2ZBFT", "-amount", hash};
            //交易
            String[] argss = {"send", "-from", "1NhdZqBXgYjor7opMpf77L4MLTnQh2ZBFT", "-to", "187LFoek3DRL9RJKigh7EDLieyXc93A5W8", "-amount", hash};
            //打印链 1
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
