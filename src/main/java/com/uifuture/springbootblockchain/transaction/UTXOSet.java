/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.transaction;

import com.google.common.collect.Maps;
import com.uifuture.springbootblockchain.bd.RocksDBUtils;
import com.uifuture.springbootblockchain.block.Block;
import com.uifuture.springbootblockchain.block.Blockchain;
import com.uifuture.springbootblockchain.util.SerializeUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * 未被花费的交易输出池
 *
 * @author chenhx
 * @version UTXOSet.java, v 0.1 2018-10-16 下午 6:07
 */

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class UTXOSet {

    private Blockchain blockchain;

    /**
     * 寻找是否拥有这个藏品
     *
     * @param pubKeyHash 钱包公钥Hash
     * @param value     交易的藏品
     */
    public SpendableOutputResult findSpendableOutputs(byte[] pubKeyHash, String value) {
        Map<String, byte[]> chainstateBucket = RocksDBUtils.getInstance().getChainstateBucket();
        //遍历区块
        long maxTime = 0;
        TXOutput max = null;
        int oId = -1;
        String txId="";
        for (Map.Entry<String, byte[]> entry : chainstateBucket.entrySet()) {
            TXOutput[] txOutputs = (TXOutput[]) SerializeUtils.deserialize(entry.getValue());
            for (int outId = 0; outId < txOutputs.length; outId++) {
                TXOutput txOutput = txOutputs[outId];
                if(txOutput.getValue().equals(value)){
                    //需要时间最晚的是自己这个公钥的
                    if(txOutput.getTimestamp() > maxTime){
                        txId = entry.getKey();
                        maxTime = txOutput.getTimestamp();
                        max = txOutput;
                        oId = outId;
                    }
                }
            }
        }
        if(max==null){
            return new SpendableOutputResult(oId, null,txId);
        }
        if (max.isLockedWithKey(pubKeyHash)) {
            //需要校验，最后交易的人是不是这个人的
            return new SpendableOutputResult(oId, max,txId);
        }
        return new SpendableOutputResult(oId, null,txId);
    }


    /**
     * 查找钱包地址对应的所有UTXO
     *
     * @param pubKeyHash 钱包公钥Hash
     * @return
     */
    public TXOutput[] findUTXOs(byte[] pubKeyHash) {
        TXOutput[] utxos = {};
        Map<String, byte[]> chainstateBucket = RocksDBUtils.getInstance().getChainstateBucket();
        if (chainstateBucket.isEmpty()) {
            return utxos;
        }
        for (byte[] value : chainstateBucket.values()) {
            TXOutput[] txOutputs = (TXOutput[]) SerializeUtils.deserialize(value);
            for (TXOutput txOutput : txOutputs) {
                if (txOutput.isLockedWithKey(pubKeyHash)) {
                    utxos = ArrayUtils.add(utxos, txOutput);
                }
            }
        }
        return utxos;
    }

    /**
     * 重建 UTXO 池索引
     */
    @Synchronized
    public void reIndex() {
        log.info("Start to reIndex UTXO set !");
        RocksDBUtils.getInstance().cleanChainStateBucket();
        Map<String, TXOutput[]> allUTXOs = blockchain.findAllUTXOs();
        Map<String, byte[]> allUTXOBytes = Maps.newHashMap();
        for (Map.Entry<String, TXOutput[]> entry : allUTXOs.entrySet()) {
            allUTXOBytes.put(entry.getKey(), SerializeUtils.serialize(entry.getValue()));
        }
        RocksDBUtils.getInstance().initAllUTXOs(allUTXOBytes);
        log.info("ReIndex UTXO set finished ! ");
    }

    /**
     * 更新UTXO池
     * <p>
     * 当一个新的区块产生时，需要去做两件事情：
     * 1）从UTXO池中移除花费掉了的交易输出；
     * 2）保存新的未花费交易输出；
     *
     * @param tipBlock 最新的区块
     */
    @Synchronized
    public void update(Block tipBlock) {
        if (tipBlock == null) {
            log.error("Fail to update UTXO set ! tipBlock is null !");
            throw new RuntimeException("Fail to update UTXO set ! ");
        }
        for (Transaction transaction : tipBlock.getTransactions()) {

            // 根据交易输入排查出剩余未被使用的交易输出
            if (!transaction.isCoinbase()) {
                for (TXInput txInput : transaction.getInputs()) {
                    // 余下未被使用的交易输出
                    TXOutput[] remainderUTXOs = {};
                    String txId = Hex.encodeHexString(txInput.getTxId());
                    TXOutput[] txOutputs = RocksDBUtils.getInstance().getUTXOs(txId);

                    if (txOutputs == null) {
                        continue;
                    }

                    for (int outIndex = 0; outIndex < txOutputs.length; outIndex++) {
                        if (outIndex != txInput.getTxOutputIndex()) {
                            remainderUTXOs = ArrayUtils.add(remainderUTXOs, txOutputs[outIndex]);
                        }
                    }

                    // 没有剩余则删除，否则更新
                    if (remainderUTXOs.length == 0) {
                        RocksDBUtils.getInstance().deleteUTXOs(txId);
                    } else {
                        RocksDBUtils.getInstance().putUTXOs(txId, remainderUTXOs);
                    }
                }
            }

            // 新的交易输出保存到DB中
            TXOutput[] txOutputs = transaction.getOutputs();
            String txId = Hex.encodeHexString(transaction.getTxId());
            RocksDBUtils.getInstance().putUTXOs(txId, txOutputs);
        }

    }


}
