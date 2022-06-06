/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.cli;

import com.uifuture.springbootblockchain.bd.RocksDBUtils;
import com.uifuture.springbootblockchain.block.Block;
import com.uifuture.springbootblockchain.block.Blockchain;
import com.uifuture.springbootblockchain.pow.ProofOfWork;
import com.uifuture.springbootblockchain.transaction.SpendableOutputResult;
import com.uifuture.springbootblockchain.transaction.TXOutput;
import com.uifuture.springbootblockchain.transaction.Transaction;
import com.uifuture.springbootblockchain.transaction.UTXOSet;
import com.uifuture.springbootblockchain.util.Base58Check;
import com.uifuture.springbootblockchain.wallet.Wallet;
import com.uifuture.springbootblockchain.wallet.WalletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Set;

/**
 * 命令行解析器
 *
 * @author chenhx
 * @version CLI.java, v 0.1 2018-10-15 下午 6:47
 */

@Slf4j
public class CLI {

    private String[] args;
    private Options options = new Options();

    public CLI(String[] args) {
        this.args = args;

        Option helpCmd = Option.builder("h").desc("show help").build();
        options.addOption(helpCmd);

        Option address = Option.builder("address").hasArg(true).desc("Source wallet address").build();
        Option sendFrom = Option.builder("from").hasArg(true).desc("Source wallet address").build();
        Option sendTo = Option.builder("to").hasArg(true).desc("Destination wallet address").build();
        Option sendAmount = Option.builder("amount").hasArg(true).desc("Amount to send").build();

        options.addOption(address);
        options.addOption(sendFrom);
        options.addOption(sendTo);
        options.addOption(sendAmount);
    }

    public static void main(String[] args) {
        CLI cli = new CLI(args);
        cli.parse();
    }

    /**
     * 命令行解析入口
     */
    public void parse() {
        this.validateArgs(args);
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            switch (args[0]) {
                case "createblockchain":
                    //创建区块
                    String createblockchainAddress = cmd.getOptionValue("address");
                    String sendAmount = cmd.getOptionValue("amount");
                    if (StringUtils.isBlank(createblockchainAddress)
                        || StringUtils.isBlank(sendAmount)
                        ) {
                        help();
                    }
                    this.createBlockchain(createblockchainAddress,sendAmount);
                    break;
                case "mining":
                    //创建区块
                    String createblockAddress = cmd.getOptionValue("address");
                    if (StringUtils.isBlank(createblockAddress)) {
                        help();
                    }
                    this.mining(createblockAddress);
                    break;
                case "getbalance":
                    String getBalanceAddress = cmd.getOptionValue("address");
                    if (StringUtils.isBlank(getBalanceAddress)) {
                        help();
                    }
                    this.getBalance(getBalanceAddress);
                    break;
                case "check":
                    getBalanceAddress = cmd.getOptionValue("address");
                    sendAmount = cmd.getOptionValue("amount");
                    if (StringUtils.isBlank(getBalanceAddress)
                            || StringUtils.isBlank(sendAmount)
                    ) {
                        help();
                    }
                    Boolean check = this.check(getBalanceAddress,sendAmount);
                    log.info("校验钱包是否拥有藏品的结果:{}",check);
                    break;
                case "send":
                    String sendFrom = cmd.getOptionValue("from");
                    String sendTo = cmd.getOptionValue("to");
                    sendAmount = cmd.getOptionValue("amount");
                    if (StringUtils.isBlank(sendFrom) ||
                            StringUtils.isBlank(sendTo) ||
                            StringUtils.isBlank(sendAmount)) {
                        help();
                    }
                    this.send(sendFrom, sendTo, sendAmount);
                    break;
                case "createwallet":
                    this.createWallet();
                    break;
                case "printaddresses":
                    this.printAddresses();
                    break;
                case "printchain":
                    this.printChain();
                    break;
                case "h":
                    this.help();
                    break;
                default:
                    this.help();
            }
        } catch (Exception e) {
            log.error("Fail to parse cli command ! ", e);
        } finally {
            RocksDBUtils.getInstance().closeDB();
        }
    }

    /**
     * 校验藏品
     * @param
     * @param sendAmount
     */
    private Boolean check(String address, String sendAmount) {
        // 检查钱包地址是否合法
        try {
            Base58Check.base58ToBytes(address);
        } catch (Exception e) {
            log.error("ERROR: invalid wallet address", e);
            throw new RuntimeException("ERROR: invalid wallet address", e);
        }

        // 得到公钥Hash值
        byte[] versionedPayload = Base58Check.base58ToBytes(address);
        byte[] pubKeyHash = Arrays.copyOfRange(versionedPayload, 1, versionedPayload.length);

        Blockchain blockchain = Blockchain.createBlockchain(address,"");
        UTXOSet utxoSet = new UTXOSet(blockchain);

        SpendableOutputResult spendableOutputResult = utxoSet.findSpendableOutputs(pubKeyHash,sendAmount);
        log.info("返回结果：{}",spendableOutputResult);
        if(spendableOutputResult.getValue()==-1 || spendableOutputResult.getUnspentOuts()==null){
            return false;
        }
        return true;
    }

    /**
     * 挖矿
     */
    private void mining(String address) {
        // 检查钱包地址是否合法
        try {
            Base58Check.base58ToBytes(address);
        } catch (Exception e) {
            log.error("ERROR: sender address invalid ! address=" + address, e);
            throw new RuntimeException("ERROR: sender address invalid ! address=" + address, e);
        }
        Blockchain blockchain = Blockchain.createBlockchain(address,"");
        //挖矿奖励
        Transaction rewardTX = Transaction.newCoinbaseTX(address, "","");
        //TODO 交易需要发送到其他的节点上去  还需要获取其他的交易
        Block block = blockchain.mineBlock(new Transaction[]{rewardTX});
        new UTXOSet(blockchain).update(block);
        log.info("Done ! ");
    }

    /**
     * 创建区块链
     *
     * @param address
     */
    private void createBlockchain(String address,String value) {
        Blockchain blockchain = Blockchain.createBlockchain(address,value);
        UTXOSet utxoSet = new UTXOSet(blockchain);
        utxoSet.reIndex();
        log.info("Done ! ");
    }

    /**
     * 验证入参
     *
     * @param args
     */
    private void validateArgs(String[] args) {
        if (args == null || args.length < 1) {
            help();
        }
    }

    /**
     * 创建钱包
     *
     * @throws Exception
     */
    private void createWallet() {
        Wallet wallet = WalletUtils.getInstance().createWallet();
        log.info("wallet address : " + wallet.getAddress());
    }

    /**
     * 打印钱包地址
     */
    private void printAddresses() {
        Set<String> addresses = WalletUtils.getInstance().getAddresses();
        if (addresses == null || addresses.isEmpty()) {
            log.info("There isn't address");
            return;
        }
        for (String address : addresses) {
            log.info("Wallet address: " + address);
        }
    }

    /**
     *  查询钱包 交易过的藏品
     *
     * @param address 钱包地址
     */
    private void getBalance(String address) {
        // 检查钱包地址是否合法
        try {
            Base58Check.base58ToBytes(address);
        } catch (Exception e) {
            log.error("ERROR: invalid wallet address", e);
            throw new RuntimeException("ERROR: invalid wallet address", e);
        }

        // 得到公钥Hash值
        byte[] versionedPayload = Base58Check.base58ToBytes(address);
        byte[] pubKeyHash = Arrays.copyOfRange(versionedPayload, 1, versionedPayload.length);

        Blockchain blockchain = Blockchain.createBlockchain(address,"");
        UTXOSet utxoSet = new UTXOSet(blockchain);

        //查询到这个人获取到的藏品。这里有个问题，不一定是最新的
        TXOutput[] txOutputs = utxoSet.findUTXOs(pubKeyHash);
        int balance = 0;
        if (txOutputs != null && txOutputs.length > 0) {
            for (TXOutput txOutput : txOutputs) {
//                balance += txOutput.getValue();
                log.info("txOutput of '{}'\n", txOutput);
            }
        }
        log.info("Balance of '{}': {}\n", new Object[]{address, balance});
    }

    /**
     * 转账
     *
     * @param from
     * @param to
     * @param amount
     * @throws Exception
     */
    private void send(String from, String to, String amount) throws Exception {
        // 检查钱包地址是否合法
        try {
            Base58Check.base58ToBytes(from);
        } catch (Exception e) {
            log.error("ERROR: sender address invalid ! address=" + from, e);
            throw new RuntimeException("ERROR: sender address invalid ! address=" + from, e);
        }
        // 检查钱包地址是否合法
        try {
            Base58Check.base58ToBytes(to);
        } catch (Exception e) {
            log.error("ERROR: receiver address invalid ! address=" + to, e);
            throw new RuntimeException("ERROR: receiver address invalid ! address=" + to, e);
        }
        if (StringUtils.isEmpty(amount)) {
            log.error("ERROR: amount invalid ! amount=" + amount);
            throw new RuntimeException("ERROR: amount invalid ! amount=" + amount);
        }
        Blockchain blockchain = Blockchain.createBlockchain(from,amount);
        // 新交易
        Transaction transaction = Transaction.newUTXOTransaction(from, to, amount, blockchain);
        // 奖励 - 没有奖励
//        Transaction rewardTx = Transaction.newCoinbaseTX(from, "",amount);
        Block newBlock = blockchain.mineBlock(new Transaction[]{transaction});
        new UTXOSet(blockchain).update(newBlock);
        log.info("Success!");
    }

    /**
     * 打印帮助信息
     */
    private void help() {
        System.out.println("Usage:");
        System.out.println("  createwallet - 生成一个新的密钥对并将其保存到钱包文件中");
        System.out.println("  printaddresses - 打印所有钱包地址");
        System.out.println("  getbalance -address ADDRESS - 获取地址的余额");
        System.out.println("  createblockchain -address ADDRESS - 创建一个区块链，并向指定地址发送创世奖励");
        System.out.println("  printchain - 打印区块链的所有块");
        System.out.println("  send -from FROM -to TO -amount AMOUNT - Send AMOUNT of coins from FROM address to TO");
        System.exit(0);
    }

    /**
     * 打印出区块链中的所有区块
     */
    private void printChain() {
        Blockchain blockchain = Blockchain.initBlockchainFromDB();
        for (Blockchain.BlockchainIterator iterator = blockchain.getBlockchainIterator(); iterator.hashNext(); ) {
            Block block = iterator.next();
            if (block != null) {
                boolean validate = ProofOfWork.newProofOfWork(block).validate();
                log.info(block.toString() + ", validate = " + validate);
            }
        }
    }
}
