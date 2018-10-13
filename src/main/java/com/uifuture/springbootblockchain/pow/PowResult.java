/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.pow;

import lombok.Data;

import java.math.BigInteger;

/**
 * 工作量计算结果
 * @author chenhx
 * @version PowResult.java, v 0.1 2018-10-13 下午 4:50
 */
@Data
public class PowResult {
    /**
     * 计数器
     */
    private BigInteger nonce;
    /**
     * hash值
     */
    private String hash;

    public PowResult(BigInteger nonce, String hash) {
        this.nonce = nonce;
        this.hash = hash;
    }
}