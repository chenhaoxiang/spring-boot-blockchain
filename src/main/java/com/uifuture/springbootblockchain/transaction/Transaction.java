/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.uifuture.springbootblockchain.transaction;

import com.uifuture.springbootblockchain.block.Block;
import com.uifuture.springbootblockchain.block.Blockchain;
import com.uifuture.springbootblockchain.util.BtcAddressUtils;
import com.uifuture.springbootblockchain.util.SerializeUtils;
import com.uifuture.springbootblockchain.wallet.Wallet;
import com.uifuture.springbootblockchain.wallet.WalletUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/**
 * 交易
 *
 * @author chenhx
 * @version Transaction.java, v 0.1 2018-10-15 下午 6:22
 */
@Data
@Slf4j
public class Transaction {
    /**
     * 本交易的Hash
     */
    private String hash;
    /**
     * 输出人的地址
     */
    private String from;
    /**
     * 公钥hash
     */
    private String fromPubHash;
    /**
     * 接收人的地址 - 公钥
     */
    private String to;
    /**
     * 藏品的hash
     */
    private String data;
    /**
     * 交易的时间
     */
    private long createTime;
    /**
     * 签名 - 由输出的人进行签名，需要私钥 才能进行验证
     */
    private String signature;

    public Transaction() {
    }

    public Transaction(String hash, String from, String to,String data, long createTime) {
        this.hash = hash;
        this.from = from;
        this.to = to;
        this.data = data;
        this.createTime = createTime;
    }

    /**
     * TODO 创建 交易 购买-使用金钱购买
     *
     * @param to 收账的钱包地址
     * @return
     */
    public static Transaction newRewardTX(String from,String to,String data) throws DecoderException {
        // 创建交易
        Transaction tx = new Transaction(null, from,to,data, System.currentTimeMillis());
        // 设置交易ID
        tx.setHash(tx.hash());
        return tx;
    }

    /**
     * 从 from 向  to 支付一定的 amount 的金额
     *
     * @param from       支付钱包地址
     * @param to         收款钱包地址
     * @param data     交易藏品
     * @param blockchain 区块链
     * @return
     */
    public static Transaction newUTXOTransaction(String from, String to, String data, Blockchain blockchain) throws Exception {
        // 获取钱包
        Wallet senderWallet = WalletUtils.getInstance().getWallet(from);
        byte[] pubKey = senderWallet.getPublicKey();
        byte[] pubKeyHash = BtcAddressUtils.ripeMD160Hash(pubKey);

        //TODO 遍历区块链，校验这个from是否是hash的最后拥有者


        Transaction newTx = new Transaction(null, from, to,data, System.currentTimeMillis());
        newTx.setHash(newTx.hash());

        // 进行交易签名
        blockchain.signTransaction(newTx, senderWallet.getPrivateKey());
        return newTx;
    }

    /**
     * 计算交易信息的Hash值
     *
     * @return
     */
    public String hash() {
        // 使用序列化的方式对Transaction对象进行深度复制
        byte[] serializeBytes = SerializeUtils.serialize(this);
        Transaction copyTx = (Transaction) SerializeUtils.deserialize(serializeBytes);
        copyTx.setHash("");
        return new String(DigestUtils.sha256(SerializeUtils.serialize(copyTx)), StandardCharsets.UTF_8);
    }

    /**
     * TODO 是否为 购买的 交易
     * @return
     */
    public boolean isCoinbase() {
        return true;
    }

    /**
     * 创建用于签名的交易数据副本，交易输入的 signature 和 pubKey 需要设置为null
     *
     * @return
     */
    public Transaction trimmedCopy() {
        return new Transaction(this.getTxId(), tmpTXInputs, tmpTXOutputs, this.getCreateTime());
    }


    /**
     * 签名
     *
     * @param privateKey 私钥
     * @param prevTxMap  前面多笔交易集合
     */
    public void sign(BCECPrivateKey privateKey, Map<String, Transaction> prevTxMap) throws Exception {
        // coinbase 交易信息不需要签名，因为它不存在交易输入信息
        if (this.isCoinbase()) {
            return;
        }
        // 再次验证一下交易信息中的交易输入是否正确，也就是能否查找对应的交易数据
        for (TXInput txInput : this.getInputs()) {
            if (prevTxMap.get(Hex.encodeHexString(txInput.getTxId())) == null) {
                throw new RuntimeException("ERROR: Previous transaction is not correct");
            }
        }

        // 创建用于签名的交易信息的副本
        Transaction txCopy = this.trimmedCopy();

        Security.addProvider(new BouncyCastleProvider());
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider.PROVIDER_NAME);
        ecdsaSign.initSign(privateKey);

        for (int i = 0; i < txCopy.getInputs().length; i++) {
            TXInput txInputCopy = txCopy.getInputs()[i];
            // 获取交易输入TxID对应的交易数据
            Transaction prevTx = prevTxMap.get(Hex.encodeHexString(txInputCopy.getTxId()));
            // 获取交易输入所对应的上一笔交易中的交易输出
            TXOutput prevTxOutput = prevTx.getOutputs()[txInputCopy.getTxOutputIndex()];
            txInputCopy.setPubKey(prevTxOutput.getPubKeyHash());
            txInputCopy.setSignature(null);
            // 得到要签名的数据，即交易ID
            txCopy.setTxId(txCopy.hash());
            txInputCopy.setPubKey(null);

            // 对整个交易信息仅进行签名，即对交易ID进行签名
            ecdsaSign.update(txCopy.getTxId());
            byte[] signature = ecdsaSign.sign();

            // 将整个交易数据的签名赋值给交易输入，因为交易输入需要包含整个交易信息的签名
            // 注意是将得到的签名赋值给原交易信息中的交易输入
            this.getInputs()[i].setSignature(signature);
        }
    }


    /**
     * 验证当前的交易信息
     * @param 历史的区块
     * @return
     */
    public boolean verify(List<Block> blockList) throws Exception {
        // coinbase 交易信息不需要签名，也就无需验证
        if (this.isCoinbase()) {
            return true;
        }

        // 再次验证一下交易信息中的交易输入是否正确，也就是能否查找对应的交易数据
        if (prevTxMap.get(Hex.encodeHexString(this.getTxId())) == null) {
            throw new RuntimeException("ERROR: Previous transaction is not correct");
        }

        // 创建用于签名验证的交易信息的副本
        Transaction txCopy = this.trimmedCopy();

        Security.addProvider(new BouncyCastleProvider());
        ECParameterSpec ecParameters = ECNamedCurveTable.getParameterSpec("secp256k1");
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider.PROVIDER_NAME);

        for (int i = 0; i < this.getInputs().length; i++) {
            TXInput txInput = this.getInputs()[i];
            // 获取交易输入TxID对应的交易数据
            Transaction prevTx = prevTxMap.get(Hex.encodeHexString(this.getTxId()));
            // 获取交易输入所对应的上一笔交易中的交易输出
            TXOutput prevTxOutput = prevTx.getOutputs()[txInput.getTxOutputIndex()];

            TXInput txInputCopy = txCopy.getInputs()[i];
            txInputCopy.setSignature(null);
            txInputCopy.setPubKey(prevTxOutput.getPubKeyHash());
            // 得到要签名的数据，即交易ID
            txCopy.setTxId(txCopy.hash());
            txInputCopy.setPubKey(null);

            // 使用椭圆曲线 x,y 点去生成公钥Key
            BigInteger x = new BigInteger(1, Arrays.copyOfRange(txInput.getPubKey(), 1, 33));
            BigInteger y = new BigInteger(1, Arrays.copyOfRange(txInput.getPubKey(), 33, 65));
            ECPoint ecPoint = ecParameters.getCurve().createPoint(x, y);

            ECPublicKeySpec keySpec = new ECPublicKeySpec(ecPoint, ecParameters);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(txCopy.getTxId());
            if (!ecdsaVerify.verify(txInput.getSignature())) {
                return false;
            }
        }
        return true;
    }
}
