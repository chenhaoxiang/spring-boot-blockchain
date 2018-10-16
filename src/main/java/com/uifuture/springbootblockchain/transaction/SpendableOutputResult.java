/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author chenhx
 * @version SpendableOutputResult.java, v 0.1 2018-10-15 下午 6:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpendableOutputResult {
    /**
     * 交易时的支付金额
     */
    private int accumulated;
    /**
     * 未花费的交易
     */
    private Map<String, int[]> unspentOuts;
}