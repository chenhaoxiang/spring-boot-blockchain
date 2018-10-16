/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.transaction;

import com.uifuture.springbootblockchain.bd.RockDB;
import com.uifuture.springbootblockchain.block.Blockchain;
import com.uifuture.springbootblockchain.transaction.entity.TXInput;
import com.uifuture.springbootblockchain.transaction.entity.TXOutput;
import com.uifuture.springbootblockchain.transaction.entity.Transaction;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * 交易数据
 * @author chenhx
 * @version TransactionData.java, v 0.1 2018-10-13 下午 7:46
 */
public class TransactionData {

    private static final Integer OUT_PUT_VALUE = 210000;
    private static final Integer CREATION_VALUE = 100000000;

    /**
     * 生成新交易信息，信息将加入到下一个待挖的区块中
     *
     * @param to   收账的钱包地址
     * @param from 解锁脚本数据 - 发送方地址
     *             如果为挖矿奖励，则为区块的hash地址，且该hash对应的奖励交易只会有一块
     * @return
     */
    public static Transaction newTransactionsTX(RockDB rockDB, String to, String from) {
        if (StringUtils.isBlank(from)) {
            from = String.format("Reward to '%s'", to);
        }
        // 创建交易输入 交易输入为空
        TXInput txInput = new TXInput(new byte[]{}, 0, from);
        //获取当前区块大小
        Integer size = rockDB.getBlocksBucket().size();
        Integer multiple = size / OUT_PUT_VALUE + 1;
        Integer value = CREATION_VALUE / multiple;
        // 创建交易输出
        TXOutput txOutput = new TXOutput(value, to);
        // 创建交易
        Transaction tx = new Transaction(null, new TXInput[]{txInput}, new TXOutput[]{txOutput});
        // 设置交易ID
        tx.setTxId("".getBytes());
        return tx;
    }


    /**
     * 从 from 向  to 支付一定的 amount 的金额
     *
     * @param from       支付钱包地址
     * @param to         收款钱包地址
     * @param amount     交易金额
     * @param blockchain 区块链
     * @return
     */
    public static Transaction newUTXOTransaction(String from, String to, int amount, Blockchain blockchain) throws Exception {
        SpendableOutputResult result = blockchain.findSpendableOutputs(from, amount);
        int accumulated = result.getAccumulated();
        Map<String, int[]> unspentOuts = result.getUnspentOuts();
        if (accumulated < amount) {
            throw new Exception("ERROR: Not enough funds");
        }
        Iterator<Map.Entry<String, int[]>> iterator = unspentOuts.entrySet().iterator();
        TXInput[] txInputs = {};
        while (iterator.hasNext()) {
            Map.Entry<String, int[]> entry = iterator.next();
            String txIdStr = entry.getKey();
            int[] outIdxs = entry.getValue();
            byte[] txId = Hex.decodeHex(txIdStr);
            for (int outIndex : outIdxs) {
                txInputs = ArrayUtils.add(txInputs, new TXInput(txId, outIndex, from));
            }
        }
        TXOutput[] txOutput = {};
        txOutput = ArrayUtils.add(txOutput, new TXOutput(amount, to));
        if (accumulated > amount) {
            txOutput = ArrayUtils.add(txOutput, new TXOutput((accumulated - amount), from));
        }
        Transaction newTx = new Transaction(null, txInputs, txOutput);
//        newTx.setTxId(Hex.decodeHex();
        return newTx;
    }

}