/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.blockchainspringboot;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author chenhx
 * @version ProofTest.java, v 0.1 2018-10-13 下午 4:04
 */
public class ProofTest {
    /**
     * Hashcash的工作量证明算法
     *
     * @param args
     */
    public static void main(String[] args) {
        int x = 5;
        int y = 0;
        long s = System.currentTimeMillis();
        String hash = DigestUtils.sha256Hex((x * y) + "");
        while (!hash.endsWith("0000")) {
            y++;
            hash = DigestUtils.sha256Hex((x * y) + "");
        }
        System.out.println("y=" + y + ",hash=" + hash);
        System.out.println("消耗时间:" + (System.currentTimeMillis() - s) + "ms");
    }

}