/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.util;

import com.uifuture.springbootblockchain.block.Block;
import com.uifuture.springbootblockchain.pow.ProofOfWork;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 字节数组工具类
 * @author chenhx
 * @version ByteUtils.java, v 0.1 2018-10-11 下午 9:17
 */
public class ByteUtils {
    public static final String GENESIS_ZERO_HASH = Hex.encodeHexString(new byte[32]);

    /**
     * 准备数据
     * <p>
     * 注意：在准备区块数据时，一定要从原始数据类型转化为byte[]，不能直接从字符串进行转换
     *
     * @param block
     * @return
     */
    public static byte[] prepareData(Block block) {
        return ByteUtils.merge(
                new BigInteger(block.getPrevBlockHash(), 16).toByteArray(),
                block.hashTransaction(),
                ByteUtils.toBytes(block.getTimeStamp()),
                ByteUtils.toBytes(ProofOfWork.TARGET_BITS),
                block.getNonce().toByteArray(),
                new BigInteger(block.getGenesisHash(), 16).toByteArray()
        );
    }

    /**
     * 将多个字节数组合并成一个字节数组
     *
     * @param bytes
     * @return
     */
    public static byte[] merge(byte[]... bytes) {
        Stream<Byte> stream = Stream.of();
        for (byte[] b : bytes) {
            stream = Stream.concat(stream, Arrays.stream(ArrayUtils.toObject(b)));
        }
        return ArrayUtils.toPrimitive(stream.toArray(Byte[]::new));
    }

    /**
     * long 类型转 byte[]
     *
     * @param val
     * @return
     */
    public static byte[] toBytes(long val) {
        return ByteBuffer.allocate(Long.BYTES).putLong(val).array();
    }


}