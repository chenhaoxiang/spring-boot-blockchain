/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.transaction.cli;

import com.uifuture.springbootblockchain.block.Blockchain;
import com.uifuture.springbootblockchain.transaction.TransactionData;
import com.uifuture.springbootblockchain.transaction.entity.TXOutput;
import com.uifuture.springbootblockchain.transaction.entity.Transaction;

/**
 * @author chenhx
 * @version CLI.java, v 0.1 2018-10-15 下午 6:47
 */
public class CLI {
    private String[] args;

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
     * 查询钱包余额
     *
     * @param address 钱包地址
     */
    private void getBalance(Blockchain blockchain, String address) throws Exception {
        TXOutput[] txOutputs = blockchain.findUTXO(address);
        int balance = 0;
        if (txOutputs != null && txOutputs.length > 0) {
            for (TXOutput txOutput : txOutputs) {
                balance += txOutput.getValue();
            }
        }
        System.out.printf("Balance of '%s': %d\n", address, balance);
    }

    /**
     * 转账
     *
     * @param from
     * @param to
     * @param amount
     */
    private void send(Blockchain blockchain, String from, String to, int amount) throws Exception {
        Transaction transaction = TransactionData.newUTXOTransaction(from, to, amount, blockchain);
        //TODO 将交易推送到每个节点 同步交易
        System.out.println("Success!");
    }

    /**
     * 打印帮助信息
     */
    private void help() {
        System.out.println("Usage:");
        System.out.println("  getbalance -address ADDRESS - Get balance of ADDRESS");
        System.out.println("  createblockchain -address ADDRESS - Create a blockchain and send genesis block reward to ADDRESS");
        System.out.println("  printchain - Print all the blocks of the blockchain");
        System.out.println("  send -from FROM -to TO -amount AMOUNT - Send AMOUNT of coins from FROM address to TO");
        System.exit(0);
    }


}