package ec.com.sofka.mapper;

import ec.com.sofka.gateway.dto.TransactionDTO;
import ec.com.sofka.transaction.Transaction;
import ec.com.sofka.data.TransactionEntity;

public class TransactionMapper {

    public static TransactionDTO toTransaction(TransactionEntity transactionEntity){
        return new TransactionDTO(transactionEntity.getId(), transactionEntity.getAmount(), transactionEntity.getFee(),
                transactionEntity.getTransactionTypes(), transactionEntity.getDate(),
                transactionEntity.getDescription(), transactionEntity.getAccountId());
    }

    public static TransactionEntity toTransactionEntity(TransactionDTO transactionDTO){
        return new TransactionEntity(transactionDTO.getId(), transactionDTO.getTransactionTypes(),
                transactionDTO.getAmount(), transactionDTO.getFee(), transactionDTO.getDate(),
                transactionDTO.getDescription(), transactionDTO.getAccountId());
    }
}
