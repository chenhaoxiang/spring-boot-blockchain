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
//            String[] argss = {"createwallet"}; //1MUjVDaxnD3dnKQ5MZXGVycckvTzA6kdmH 164mY79vVpcPhB8mjXmCLvjTmceazLfTTF
            //创建区块链
//            String[] argss = {"createblockchain", "-address", "1MUjVDaxnD3dnKQ5MZXGVycckvTzA6kdmH","-amount", hash};
            // 打印所有钱包地址
//            String[] argss = {"printaddresses"};
            //获取钱包藏品的交易记录
//            String[] argss = {"getbalance", "-address", "1MUjVDaxnD3dnKQ5MZXGVycckvTzA6kdmH"};
            //校验这个藏品是不是这个钱包的
//            String[] argss = {"check", "-address", "1MUjVDaxnD3dnKQ5MZXGVycckvTzA6kdmH", "-amount", hash};
//            String[] argss = {"check", "-address", "164mY79vVpcPhB8mjXmCLvjTmceazLfTTF", "-amount", hash};
            //交易
            String[] argss = {"send", "-from", "1MUjVDaxnD3dnKQ5MZXGVycckvTzA6kdmH", "-to", "164mY79vVpcPhB8mjXmCLvjTmceazLfTTF", "-amount", hash};
            //打印链 1
//            Block(version=1.0.0, hash=0000b9c61e34dff9df572428ec7aabd2305aaed657318143311f2813ba94beb5, prevBlockHash=0000000000000000000000000000000000000000000000000000000000000000, transactions=[Transaction(txId=[-23, 31, 13, 3, -43, 121, 107, 15, 29, 41, -11, -11, 13, -71, -26, 26, -11, -35, 27, 0, -114, 95, 34, 100, -94, -85, -18, -11, 2, 99, 14, -47], inputs=[TXInput(txId=[], txOutputIndex=-1, signature=null, pubKey=[-27, -120, -101, -28, -72, -106, -27, -116, -70, -27, -99, -105, -26, -105, -74, -23, -105, -76, -17, -68, -102, 32, 50, 48, 50, 50, 45, 48, 54, 45, 48, 54, 32, 49, 55, 58, 49, 55, 58, 53, 54, 32, 54, 49, 49, 32, -27, -114, -69, -28, -72, -83, -27, -65, -125, -27, -116, -106, -27, -116, -70, -27, -99, -105, -23, -109, -66, -25, -102, -124, -27, -68, -128, -27, -89, -117])], outputs=[TXOutput(value=6bc1b805ac4f5c8ab19a316334388cc6cc1332d0db1b758f3609608230ca9975, pubKeyHash=[-32, -95, 54, 108, 41, 20, 118, -88, -29, -36, -122, 56, -15, 53, -53, 48, -127, -75, 5, 63], timestamp=1654507076804)], createTime=1654507076804)], timeStamp=1654507076838, nonce=27916, height=0, merkleRoot=2c0763bf8f31b8c6c81a30883f054c61abdc0de5e9362c3d2a563c5d1aae702e, target=3533694129556768659166595001485837031654967793751237916243212402585239552), validate = true
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
