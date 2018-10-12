/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.pow;

import lombok.Data;

/**
 * 工作量计算结果
 * @author chenhx
 * @version PowResult.java, v 0.1 2018-10-11 下午 9:18
 */

@Data
public class PowResult {
    /**
     * 计数器
     */
    private long nonce;
    /**
     * hash值
     */
    private String hash;

    public PowResult(long nonce, String hash) {
        this.nonce = nonce;
        this.hash = hash;
    }
}