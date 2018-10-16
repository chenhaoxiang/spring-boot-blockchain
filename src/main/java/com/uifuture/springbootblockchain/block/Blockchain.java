/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.block;

import com.uifuture.springbootblockchain.bd.RockDB;
import com.uifuture.springbootblockchain.pow.PowResult;
import com.uifuture.springbootblockchain.pow.ProofOfWork;
import com.uifuture.springbootblockchain.transaction.SpendableOutputResult;
import com.uifuture.springbootblockchain.transaction.TransactionData;
import com.uifuture.springbootblockchain.transaction.entity.TXInput;
import com.uifuture.springbootblockchain.transaction.entity.TXOutput;
import com.uifuture.springbootblockchain.transaction.entity.Transaction;
import com.uifuture.springbootblockchain.util.ByteUtils;
import lombok.Data;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
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

    private String address;

    private Blockchain() {
        init();
    }

    /**
     * 创建单例对象
     *
     * @return
     */
    public static Blockchain getInstance(RockDB rockDB, String address) {
        if (blockChain == null) {
            synchronized (Blockchain.class) {
                if (blockChain == null) {
                    Blockchain.rockDB = rockDB;
                    String lastBlockHash = rockDB.getLastBlockHash();
                    if (StringUtils.isBlank(lastBlockHash)) {
                        // 创建 coinBase 交易
                        Transaction coinbaseTX = TransactionData.newTransactionsTX(rockDB, address, "");
                        newGenesisBlock(coinbaseTX);
                    }
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
     * 创建创世区块
     * @return
     */
    private static Block newGenesisBlock(Transaction base) {
        Block block;
        if (!StringUtils.isBlank(rockDB.getLastBlockHash())) {
            return null;
        }
        //创世区块
        block = new Block(ByteUtils.GENESIS_ZERO_HASH);
        block.setGenesisHash(ByteUtils.GENESIS_ZERO_HASH);
        block.setTransactions(new Transaction[]{base});
        block.setTimeStamp(System.currentTimeMillis());
        block.setTarget(ProofOfWork.TARGET);
        ProofOfWork pow = ProofOfWork.newProofOfWork(block);
        //开始挖矿
        PowResult powResult = pow.run();
        block.setHash(powResult.getHash());
        block.setNonce(powResult.getNonce());
        //TODO 校验
        rockDB.putBlock(block);
        rockDB.putLastBlockHash(block.getHash());
        rockDB.putGenesisBlockHash(block.getHash());
        rockDB.putLastBlock(block);
        //TODO 挖到之后，需要同步到其他节点
        return block;
    }

    /**
     * 创建区块
     * @return
     */
    public Block newBlock(Transaction[] transactions) {
        //TODO 在挖矿之前，需要同步最新的区块
        Block block = new Block(rockDB.getLastBlockHash());
        //设置创世节点hash
        block.setGenesisHash(rockDB.getGenesisBlockHash());
        //TODO 设置交易数据,也就是说，下一个区块生成的交易数据，是只包含该区块交易前的数据，10分钟的生效时间。
        block.setTransactions(transactions);

        block.setTimeStamp(System.currentTimeMillis());
        block.setTarget(ProofOfWork.TARGET);
        ProofOfWork pow = ProofOfWork.newProofOfWork(block);
        //开始挖矿
        PowResult powResult = pow.run();
        block.setHash(powResult.getHash());
        block.setNonce(powResult.getNonce());
        rockDB.putBlock(block);
        rockDB.putLastBlockHash(block.getHash());
        rockDB.putLastBlock(block);
        //TODO 挖到之后，需要同步到其他节点
        return block;
    }


    /**
     * 检查是否是有效链，遍历每个区块验证hash和proof，来确定一个给定的区块链是否有效
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

    /**
     * 查找钱包地址对应的所有未花费的交易
     *
     * @param address 钱包地址
     * @return
     */
    private Transaction[] findUnspentTransactions(String address) throws Exception {
        Map<String, int[]> allSpentTXOs = this.getAllSpentTXOs(address);
        Transaction[] unspentTxs = {};
        // 再次遍历所有区块中的交易输出
        for (BlockchainIterator blockchainIterator = new BlockchainIterator(rockDB.getLastBlockHash());
             blockchainIterator.hashNext(rockDB); ) {
            Block block = blockchainIterator.next(rockDB);
            for (Transaction transaction : block.getTransactions()) {

                String txId = Hex.encodeHexString(transaction.getTxId());

                int[] spentOutIndexArray = allSpentTXOs.get(txId);

                for (int outIndex = 0; outIndex < transaction.getOutputs().length; outIndex++) {
                    if (spentOutIndexArray != null && ArrayUtils.contains(spentOutIndexArray, outIndex)) {
                        continue;
                    }
                    // 保存不存在 allSpentTXOs 中的交易
                    if (transaction.getOutputs()[outIndex].canBeUnlockedWith(address)) {
                        unspentTxs = ArrayUtils.add(unspentTxs, transaction);
                    }
                }
            }
        }
        return unspentTxs;
    }


    /**
     * 从交易输入中查询区块链中所有已被花费了的交易输出
     *
     * @param address 钱包地址
     * @return 交易ID以及对应的交易输出下标地址
     * @throws Exception
     */
    private Map<String, int[]> getAllSpentTXOs(String address) throws Exception {
        // 定义TxId ——> spentOutIndex[]，存储交易ID与已被花费的交易输出数组索引值
        Map<String, int[]> spentTXOs = new HashMap<>(64);
        for (BlockchainIterator blockchainIterator = new BlockchainIterator(rockDB.getLastBlockHash());
             blockchainIterator.hashNext(rockDB); ) {
            Block block = blockchainIterator.next(rockDB);
            for (Transaction transaction : block.getTransactions()) {
                // 如果是 coinbase 交易，直接跳过，因为它不存在引用前一个区块的交易输出
                if (transaction.isCoinbase()) {
                    continue;
                }
                for (TXInput txInput : transaction.getInputs()) {
                    if (txInput.canUnlockOutputWith(address)) {
                        String inTxId = Hex.encodeHexString(txInput.getTxId());
                        int[] spentOutIndexArray = spentTXOs.get(inTxId);
                        if (spentOutIndexArray == null) {
                            spentTXOs.put(inTxId, new int[]{txInput.getTxOutputIndex()});
                        } else {
                            spentOutIndexArray = ArrayUtils.add(spentOutIndexArray, txInput.getTxOutputIndex());
                            spentTXOs.put(inTxId, spentOutIndexArray);
                        }
                    }
                }
            }
        }
        return spentTXOs;
    }


    /**
     * 查找钱包地址对应的所有UTXO
     *
     * @param address 钱包地址
     * @return
     */
    public TXOutput[] findUTXO(String address) throws Exception {
        Transaction[] unspentTxs = this.findUnspentTransactions(address);
        TXOutput[] utxos = {};
        if (unspentTxs == null || unspentTxs.length == 0) {
            return utxos;
        }
        for (Transaction tx : unspentTxs) {
            for (TXOutput txOutput : tx.getOutputs()) {
                if (txOutput.canBeUnlockedWith(address)) {
                    utxos = ArrayUtils.add(utxos, txOutput);
                }
            }
        }
        return utxos;
    }


    /**
     * 寻找能够花费的交易
     *
     * @param address 钱包地址
     * @param amount  花费金额
     */
    public SpendableOutputResult findSpendableOutputs(String address, int amount) throws Exception {
        Transaction[] unspentTXs = this.findUnspentTransactions(address);
        int accumulated = 0;
        Map<String, int[]> unspentOuts = new HashMap<>();
        for (Transaction tx : unspentTXs) {
            String txId = Hex.encodeHexString(tx.getTxId());
            for (int outId = 0; outId < tx.getOutputs().length; outId++) {
                TXOutput txOutput = tx.getOutputs()[outId];
                if (txOutput.canBeUnlockedWith(address) && accumulated < amount) {
                    accumulated += txOutput.getValue();
                    int[] outIds = unspentOuts.get(txId);
                    if (outIds == null) {
                        outIds = new int[]{outId};
                    } else {
                        outIds = ArrayUtils.add(outIds, outId);
                    }
                    unspentOuts.put(txId, outIds);
                    if (accumulated >= amount) {
                        break;
                    }
                }
            }
        }
        return new SpendableOutputResult(accumulated, unspentOuts);
    }


}