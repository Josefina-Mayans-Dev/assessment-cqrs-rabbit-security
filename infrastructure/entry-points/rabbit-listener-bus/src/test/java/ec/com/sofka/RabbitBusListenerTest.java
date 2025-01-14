package ec.com.sofka;

import ec.com.sofka.aggregate.events.AccountBalanceUpdated;
import ec.com.sofka.aggregate.events.AccountCreated;
import ec.com.sofka.aggregate.events.TransactionRegistered;
import ec.com.sofka.appservice.account.queries.usecases.AccountSavedViewUseCase;
import ec.com.sofka.appservice.transaction.queries.usecases.TransactionSavedViewUseCase;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.gateway.dto.UserDTO;
import ec.com.sofka.utils.enums.TransactionTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RabbitBusListenerTest {

    @Mock
    private AccountSavedViewUseCase accountSavedViewUseCase;

    @Mock
    private TransactionSavedViewUseCase transactionSavedViewUseCase;

    @InjectMocks
    private BusListener listener;

    @Test
    void shouldProcessAccountCreatedEvent() {
        AccountCreated event = new AccountCreated(
                "accountId",
                "123456789",
                BigDecimal.valueOf(1000.0),
                "John Doe",
                new ArrayList<>()
        );

        AccountDTO expectedDTO = new AccountDTO(
                "accountId",
                "123456789",
                BigDecimal.valueOf(1000.0),
                "John Doe"
        );

        listener.receiveAccountCreated(event);

        verify(accountSavedViewUseCase).accept(argThat(accountDTO ->
                accountDTO.getId().equals("accountId") &&
                        accountDTO.getAccountNumber().equals("123456789") &&
                        Objects.equals(accountDTO.getBalance(), BigDecimal.valueOf(1000.0)) &&
                        accountDTO.getAccountHolder().equals("John Doe")
        ));
    }


    @Test
    void shouldProcessTransactionCreatedEvent() {
        TransactionRegistered event = new TransactionRegistered(
                "transactionId",
                TransactionTypes.WITHDRAW_ATM,
                new BigDecimal("200.00"),
                new BigDecimal("5.00"),
                LocalDateTime.now(),
                "ATM withdrawal",
                "accountId"
        );

        listener.receiveTransactionRegistered(event);

        verify(transactionSavedViewUseCase).accept(argThat(transactionDTO ->
                transactionDTO.getId().equals("transactionId") &&
                        transactionDTO.getAmount().compareTo(new BigDecimal("200.00")) == 0 &&
                        transactionDTO.getFee().compareTo(new BigDecimal("5.00")) == 0 &&
                        transactionDTO.getTransactionTypes().equals(TransactionTypes.WITHDRAW_ATM) &&
                        transactionDTO.getAccountId().equals("accountId")
        ));
    }

    @Test
    void shouldProcessAccountUpdatedEvent() {
        AccountBalanceUpdated event = new AccountBalanceUpdated(
                "accountId",
                "123456789",
                new BigDecimal("1500.00"),
                "John Doe",
                new ArrayList<>()
        );

        listener.receiveAccountUpdated(event);

        verify(accountSavedViewUseCase).accept(argThat(accountDTO ->
                accountDTO.getId().equals("accountId") &&
                        accountDTO.getAccountNumber().equals("123456789") &&
                        accountDTO.getBalance().compareTo(new BigDecimal("1500.00")) == 0 &&
                        accountDTO.getAccountHolder().equals("John Doe")
        ));
    }
}
