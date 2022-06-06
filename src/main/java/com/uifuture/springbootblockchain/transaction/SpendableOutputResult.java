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
     * 是否拥有藏品
     */
    private Boolean have;

}
