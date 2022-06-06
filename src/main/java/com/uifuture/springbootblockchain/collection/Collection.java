/*
 * chenhx
 * Copyright (C) 2013-2022 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.collection;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenhx
 * @version 0.0.1
 * @className Collection.java
 * @date 2022-06-02 16:31
 * @description
 */
@Data
public class Collection implements Serializable {
    private static final long serialVersionUID = -3014671271487318120L;
    /**
     * 藏品所属钱包信息，每一次购买藏品，都是一个区块
     */
    private String address;
    /**
     * 藏品的hash,Base58Check.bytesToBase58(steams)
     */
    private String hash;
}
