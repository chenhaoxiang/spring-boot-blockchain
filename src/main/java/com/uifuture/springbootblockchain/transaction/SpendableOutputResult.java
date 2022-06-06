/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 查询结果
 * @author chenhx
 * @version SpendableOutputResult.java, v 0.1 2018-10-15 下午 6:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpendableOutputResult {

    /**
     * 位置 输出的
     */
    private int value;
    /**
     * 未花费的藏品
     */
    private TXOutput unspentOuts;

    /**
     * 交易的id，这个输出的
     */
    private String txId;
}
