/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.controller;

import com.uifuture.springbootblockchain.block.Block;
import com.uifuture.springbootblockchain.block.Blockchain;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenhx
 * @version IndexController.java, v 0.1 2018-10-13 下午 4:21
 */
@Controller
@RequestMapping("ui")
public class IndexController {

    /**
     * 该Servlet用于输出整个区块链的数据
     * @param req
     * @param resp
     * @throws IOException
     */
    @RequestMapping("chain")
    public void chain(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Blockchain blockChain = Blockchain.initBlockchainFromDB();
        Map<String, Object> response = new HashMap<String, Object>();
        List<Block> blocks = new ArrayList<>();
        while (blockChain.getBlockchainIterator().hashNext()){
            blocks.add(blockChain.getBlockchainIterator().next());
        }
        response.put("blocks", blocks);
        response.put("length", blockChain.getAllBlockHash().size());

        JSONObject jsonResponse = new JSONObject(response);
        resp.setContentType("application/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.println(jsonResponse);
        printWriter.close();
    }

    /**
     * 该Servlet用于接收并处理新的交易信息
     * @param req
     * @param resp
     * @throws IOException
     */
    @RequestMapping("transactions/new")
    public void transactionsNew(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("utf-8");
        // 读取客户端传递过来的数据并转换成JSON格式
        BufferedReader reader = req.getReader();
        String input = null;
        StringBuffer requestBody = new StringBuffer();
        while ((input = reader.readLine()) != null) {
            requestBody.append(input);
        }
        JSONObject jsonValues = new JSONObject(requestBody.toString());

        // 检查所需要的字段是否位于POST的data中
        String[] required = { "sender", "recipient", "amount" };
        for (String string : required) {
            if (!jsonValues.has(string)) {
                // 如果没有需要的字段就返回错误信息
                resp.sendError(400, "Missing values");
            }
        }

        // 新建交易信息
//        Blockchain blockChain = Blockchain.getInstance();
//        int index = blockChain.newTransactions(jsonValues.getString("sender"), jsonValues.getString("recipient"),
//                jsonValues.getLong("amount"));
        int index = 1;

        // 返回json格式的数据给客户端
        resp.setContentType("application/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.println(new JSONObject().append("message", "Transaction will be added to Block " + index));
        printWriter.close();
    }

    /**
     *  该Servlet用于运行工作算法的证明来获得下一个证明，也就是所谓的挖矿
     * @param req
     * @param resp
     * @throws IOException
     */
    @RequestMapping("mine")
    public void mine(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        Blockchain blockChain = Blockchain.getInstance();
//        Map<String, Object> lastBlock = blockChain.lastBlock();
//        long lastProof = Long.parseLong(lastBlock.get("proof") + "");
//        long proof = blockChain.proofOfWork(lastProof);
//
//        // 给工作量证明的节点提供奖励，发送者为 "0" 表明是新挖出的币
//        String uuid = (String) req.getServletContext().getAttribute("uuid");
//        blockChain.newTransactions("0", uuid, 1);
//
//        // 构建新的区块
//        Map<String, Object> newBlock = blockChain.newBlock(proof, null);
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "New Block Forged");
//        response.put("index", newBlock.get("index"));
//        response.put("transactions", newBlock.get("transactions"));
//        response.put("proof", newBlock.get("proof"));
//        response.put("previous_hash", newBlock.get("previous_hash"));
//
//        // 返回新区块的数据给客户端
//        resp.setContentType("application/json");
//        PrintWriter printWriter = resp.getWriter();
//        printWriter.println(new JSONObject(response));
//        printWriter.close();
    }

}
