/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.pow;

import com.uifuture.springbootblockchain.block.Block;
import com.uifuture.springbootblockchain.util.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;

/**
 * 工作量证明
 * @author chenhx
 * @version ProofOfWork.java, v 0.1 2018-10-13 下午 4:50
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ProofOfWork {
    /**
     * 难度最大值
     */
    private static final BigInteger TARGET_MAX = BigInteger.valueOf(1).shiftLeft((250));

    /**
     * 难度目标位  20
     * 固定一个时间点，多久调整一次
     * 暂定为2018个区块调整一次
     */
    public static int TARGET_BITS = 15;
    /**
     * 难度目标值
     */
    public static BigInteger TARGET = BigInteger.valueOf(1).shiftLeft((256 - TARGET_BITS));

    /**
     * 区块
     */
    private Block block;
    /**
     * 难度目标值
     */
    private BigInteger target;

    /**
     * 创建新的工作量证明，设定难度目标值
     * <p>
     * 对1进行移位运算，将1向左移动 (256 - TARGET_BITS) 位，得到我们的难度目标值
     *
     * @param block
     * @return
     */
    public static ProofOfWork newProofOfWork(Block block) {
        BigInteger targetValue = BigInteger.valueOf(1).shiftLeft((256 - TARGET_BITS));
        return new ProofOfWork(block, targetValue);
    }

    /**
     * 运行工作量证明，开始挖矿，找到小于难度目标值的Hash
     *
     * @return
     */
    public PowResult run() {
        BigInteger nonce = new BigInteger("0");
        String shaHex = "";
        long startTime = System.currentTimeMillis();
        while (nonce.compareTo(TARGET_MAX) < 0) {
            this.block.setNonce(nonce);
            byte[] data = this.prepareData(nonce);
            shaHex = DigestUtils.sha256Hex(data);
            if (new BigInteger(shaHex, 16).compareTo(ProofOfWork.TARGET) < 0) {
                log.info("开采包含的交易数据：{}", this.getBlock().getTransactions());
                log.info("花费时间: {}秒", (float) (System.currentTimeMillis() - startTime) / 1000);
                log.info("正确的Hash值: {}", shaHex);
                break;
            } else {
                log.info("当前运算量: {}", nonce);
                nonce = nonce.add(new BigInteger("1"));
            }
        }
        return new PowResult(nonce, shaHex);
    }

    /**
     * 验证区块是否有效
     *
     * @return
     */
    public boolean validate() {
        byte[] data = this.prepareData(this.getBlock().getNonce());
        return new BigInteger(DigestUtils.sha256Hex(data), 16).compareTo(this.target) == -1;
    }

    /**
     * 准备数据
     * <p>
     * 注意：在准备区块数据时，一定要从原始数据类型转化为byte[]，不能直接从字符串进行转换
     *
     * @param nonce
     * @return
     */
    private byte[] prepareData(BigInteger nonce) {
        byte[] prevBlockHashBytes = {};
        if (StringUtils.isNoneBlank(this.getBlock().getPrevBlockHash())) {
            prevBlockHashBytes = new BigInteger(this.getBlock().getPrevBlockHash(), 16).toByteArray();
        }
        return ByteUtils.merge(
                prevBlockHashBytes,
                this.getBlock().hashTransaction(),
                ByteUtils.toBytes(this.getBlock().getTimeStamp()),
                ByteUtils.toBytes(TARGET_BITS),
                nonce.toByteArray()
        );
    }

}
