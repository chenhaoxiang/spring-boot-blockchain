/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.pow;

import com.uifuture.springbootblockchain.block.Block;
import com.uifuture.springbootblockchain.util.ByteUtils;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;

/**
 * 工作量证明
 * @author chenhx
 * @version ProofOfWork.java, v 0.1 2018-10-13 下午 4:50
 */

@Data
public class ProofOfWork {
    /**
     * 难度最大值
     */
    private static final BigInteger TARGET_MAX = BigInteger.valueOf(1).shiftLeft((250));
    /**
     * 区块
     */
    private Block block;
    /**
     * 难度目标位  20
     * 固定一个时间点，多久调整一次
     * 暂定为2018个区块调整一次
     */
    public static int TARGET_BITS = 21;
    /**
     * 难度目标值
     */
    public static BigInteger TARGET = BigInteger.valueOf(1).shiftLeft((256 - TARGET_BITS));

    private ProofOfWork() {
    }

    private ProofOfWork(Block block) {
        this.block = block;
    }

    /**
     * 创建新的工作量证明，设定难度目标值
     * <p>
     * 对1进行移位运算，将1向左移动 (256 - TARGET_BITS) 位，得到我们的难度目标值
     * 目前为固定难度值
     * @param block
     * @return
     */
    public static ProofOfWork newProofOfWork(Block block) {
        return new ProofOfWork(block);
    }

    /**
     * 运行工作量证明，开始挖矿，找到小于难度目标值的Hash
     * @return
     */
    public PowResult run() {
        BigInteger nonce = new BigInteger("0");
        String shaHex = "";
        long startTime = System.currentTimeMillis();
        while (nonce.compareTo(TARGET_MAX) < 0) {
            this.block.setNonce(nonce);
            byte[] data = ByteUtils.prepareData(this.block);
            shaHex = DigestUtils.sha256Hex(data);
            if (new BigInteger(shaHex, 16).compareTo(ProofOfWork.TARGET) < 0) {
                System.out.printf("开采包含的交易数据：%s \n", this.getBlock().getTransactions());
                System.out.printf("花费时间: %s 秒 \n", (float) (System.currentTimeMillis() - startTime) / 1000);
                System.out.printf("正确的Hash值: %s \n\n", shaHex);
                break;
            } else {
                nonce = nonce.add(new BigInteger("1"));
            }
        }
        return new PowResult(nonce, shaHex);
    }

    /**
     * 验证当前的区块是否有效
     * @return
     */
    public boolean validate() {
        byte[] data = ByteUtils.prepareData(this.block);
        return new BigInteger(DigestUtils.sha256Hex(data), 16).compareTo(ProofOfWork.TARGET) == -1;
    }



}