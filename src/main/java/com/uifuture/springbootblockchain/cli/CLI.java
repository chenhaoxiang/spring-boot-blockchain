/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.cli;

import com.uifuture.springbootblockchain.bd.RocksDB;
import com.uifuture.springbootblockchain.block.Block;
import com.uifuture.springbootblockchain.block.Blockchain;
import com.uifuture.springbootblockchain.pow.ProofOfWork;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * 程序命令行工具入口
 * @author chenhx
 * @version CLI.java, v 0.1 2018-10-11 下午 9:19
 */
public class CLI {

    private String[] args;
    private Options options = new Options();

    public CLI(String[] args) {
        this.args = args;
        options.addOption("h", "help", false, "show help");
        options.addOption("add", "addblock", true, "add a block to the blockchain");
        options.addOption("print", "printchain", false, "print all the blocks of the blockchain");
    }

    /**
     * 命令行解析入口
     */
    public void parse(RocksDB rocksDB) {
        this.validateArgs(args);
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                help();
            }
            if (cmd.hasOption("add")) {
                String data = cmd.getOptionValue("add");
                addBlock(data,rocksDB);
            }
            if (cmd.hasOption("print")) {
                printChain(rocksDB);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 打印帮助信息
     */
    private void help() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Main", options);
        System.exit(0);
    }

    /**
     * 添加区块
     *
     * @param data
     */
    private void addBlock(String data,RocksDB rocksDB) throws Exception {
        Blockchain blockchain = Blockchain.newBlockchain(rocksDB);
        blockchain.addBlock(data,rocksDB);
    }

    /**
     * 打印出区块链中的所有区块
     */
    private void printChain(RocksDB rocksDB) {
        Blockchain blockchain = Blockchain.newBlockchain(rocksDB);
        for (Blockchain.BlockchainIterator iterator = blockchain.getBlockchainIterator(); iterator.hashNext(rocksDB); ) {
            Block block = iterator.next(rocksDB);

            if (block != null) {
                boolean validate = ProofOfWork.newProofOfWork(block).validate();
                System.out.println(block.toString() + ", validate = " + validate);
            }
        }
    }

}