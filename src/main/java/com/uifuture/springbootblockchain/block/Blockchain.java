/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.block;

import com.uifuture.springbootblockchain.bd.RockDB;
import com.uifuture.springbootblockchain.pow.PowResult;
import com.uifuture.springbootblockchain.pow.ProofOfWork;
import com.uifuture.springbootblockchain.util.ByteUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 区块链
 * AllArgsConstructor 所有参数的构造函数
 * @author chenhx
 * @version Blockchain.java, v 0.1 2018-10-11 下午 9:20
 */

@Data
public class Blockchain {
    private static Blockchain blockChain = null;
    private static RockDB rockDB;
    /**
     * 该实例变量用于当前的交易信息列表
     */
    private List<Map<String, Object>> currentTransactions;
    /**
     * 用于存储网络中其他节点的集合
     */
    private Set<String> nodes;

    private Blockchain() {
        init();
    }

    /**
     * 创建单例对象
     *
     * @return
     */
    public static Blockchain getInstance(RockDB rockDB) {
        if (blockChain == null) {
            synchronized (Blockchain.class) {
                if (blockChain == null) {
                    Blockchain.rockDB = rockDB;
                    blockChain = new Blockchain();
                }
            }
        }
        return blockChain;
    }

    private void init() {
        currentTransactions = new ArrayList<>();
        // 用于存储网络中其他节点的集合
        nodes = new HashSet<>();
    }

    /**
     * 注册节点
     *
     * @param address 节点地址
     * @throws MalformedURLException
     */
    public void registerNode(String address) throws MalformedURLException {
        URL url = new URL(address);
        String node = url.getHost() + ":" + (url.getPort() == -1 ? url.getDefaultPort() : url.getPort());
        nodes.add(node);
    }

    /**
     * @return 得到区块链中的最后一个区块的Hash
     */
    public String lastBlock() {
        return rockDB.getLastBlockHash();
    }


    /**
     * 创建区块
     * @return
     */
    public Block newBlock() {
        //TODO 在挖矿之前，需要同步最新的区块
        Block block;
        if (StringUtils.isBlank(rockDB.getLastBlockHash())) {
            //创世区块
            block = new Block(ByteUtils.GENESIS_ZERO_HASH);
            block.setGenesisHash(ByteUtils.GENESIS_ZERO_HASH);
            block.setData("Genesis Block");
        } else {
            block = new Block(rockDB.getLastBlockHash());
            //设置创世节点hash
            block.setGenesisHash(rockDB.getGenesisBlockHash());
            //设置交易数据,也就是说，下一个区块生成的交易数据，是只包含该区块交易前的数据，10分钟的生效时间。
            block.setData("data");
        }
        block.setTimeStamp(System.currentTimeMillis());
        block.setTarget(ProofOfWork.TARGET);
        ProofOfWork pow = ProofOfWork.newProofOfWork(block);
        //开始挖矿
        PowResult powResult = pow.run();
        block.setHash(powResult.getHash());
        block.setNonce(powResult.getNonce());
        System.out.println("当前挖到的区块:" + block);
        rockDB.putBlock(block);
        rockDB.putLastBlockHash(block.getHash());
        //TODO 挖到之后，需要同步到其他节点
        return block;
    }

    /**
     * 生成新交易信息，信息将加入到下一个待挖的区块中
     *
     * @param sender    发送方的地址
     * @param recipient 接收方的地址
     * @param amount    交易数量
     * @return 返回该交易事务的块的索引
     */
    public String newTransactions(String sender, String recipient, long amount) {
        Map<String, Object> transaction = new HashMap<>(4);
        transaction.put("sender", sender);
        transaction.put("recipient", recipient);
        transaction.put("amount", amount);

        getCurrentTransactions().add(transaction);
        return lastBlock();
    }

    /**
     * 检查是否是有效链，遍历每个区块验证hash和proof，来确定一个给定的区块链是否有效
     *
     * @param chain
     * @return
     */
    public boolean validChain(Map<String, byte[]> chain) {
//        Block lastBlock = chain.get(0);
//        int currentIndex = 1;
//        while (currentIndex < chain.size()) {
//            Block block = chain.get(currentIndex);
//            System.out.println(lastBlock.toString());
//            System.out.println(block.toString());
//            System.out.println("\n-------------------------\n");
//            // 检查block的hash是否正确
//            byte[] data = ByteUtils.prepareData(lastBlock);
//            String shaHex = DigestUtils.sha256Hex(data);
//            if (! block.getPrevBlockHash().equals(shaHex)) {
//                return false;
//            }
//            lastBlock = block;
//            currentIndex++;
//        }
        return true;
    }

    /**
     * 共识算法解决冲突，使用网络中最长的链. 遍历所有的邻居节点，并用上一个方法检查链的有效性， 如果发现有效更长链，就替换掉自己的链
     *
     * @return 如果链被取代返回true, 否则返回false
     * @throws IOException
     */
    public boolean resolveConflicts() throws IOException {
        Set<String> neighbours = this.nodes;
        Map<String, byte[]> newChain = null;

        // 寻找最长的区块链
        long maxLength = rockDB.getBlocksBucket().size();

        // 获取并验证网络中的所有节点的区块链
        for (String node : neighbours) {

            URL url = new URL("http://" + node + "/BlockChain_Java/chain");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"));
                StringBuffer responseData = new StringBuffer();
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    responseData.append(response);
                }
                bufferedReader.close();

                JSONObject jsonData = new JSONObject(bufferedReader.toString());
                long length = jsonData.getLong("size");
                Map<String, byte[]> chain = (Map<String, byte[]>) jsonData.getJSONArray("chain").toList();

                // 检查长度是否长，链是否有效
                if (length > maxLength && validChain(chain)) {
                    maxLength = length;
                    newChain = chain;
                }
            }

        }
        // 如果发现一个新的有效链比我们的长，就替换当前的链
        if (newChain != null) {
            rockDB.setBlocksBucket(newChain);
            return true;
        }
        return false;
    }
}