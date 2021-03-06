package org.tron.common.dispatch.creator.commonCase;

import com.google.protobuf.ByteString;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.tron.common.crypto.ECKey;
import org.tron.common.dispatch.AbstractTransactionCreator;
import org.tron.common.dispatch.BadCaseTransactionCreator;
import org.tron.common.dispatch.TransactionFactory;
import org.tron.common.dispatch.creator.CreatorCounter;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.TransactionUtils;
import org.tron.protos.Contract;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;

public class BadRefBlockRandomValueCreator extends AbstractTransactionCreator implements BadCaseTransactionCreator {
  private AtomicLong serialNum = new AtomicLong(0);

  private Random random = new Random(System.currentTimeMillis());

  @Override
  protected Protocol.Transaction create() {
    TransactionFactory.context.getBean(CreatorCounter.class).put(this.getClass().getName());
    Contract.TransferContract contract = Contract.TransferContract.newBuilder()
        .setOwnerAddress(ownerAddress)
        .setToAddress(toAddress)
        .setAmount(amount)
        .build();
    Protocol.Transaction transaction = TransactionUtils.createTransaction(contract, ContractType.TransferContract);
    byte[] data = new byte[64];
    random.nextBytes(data);
    transaction = transaction.toBuilder()
        .setRawData(
            transaction.getRawData().toBuilder()
                .setRefBlockBytes(ByteString.copyFrom(data))
                .setRefBlockHash(ByteString.copyFrom(data))
                .setRefBlockNum(random.nextLong())
                .build()
        )
        .build();
    transaction = TransactionUtils.signTransaction(transaction, ECKey.fromPrivate(ByteArray.fromHexString(privateKey)));
    return transaction;

  }
}
