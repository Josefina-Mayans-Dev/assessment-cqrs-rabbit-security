package ec.com.sofka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:rabbit-application.properties")
public class RabbitEnvProps {

    @Value("${account.created.exchange}")
    private String accountCreatedExchange;

    @Value("${account.created.queue}")
    private String accountCreatedQueue;

    @Value("${account.created.routingKey}")
    private String accountCreatedRoutingKey;

    @Value("${account.updated.exchange}")
    private String accountUpdatedExchange;

    @Value("${account.updated.queue}")
    private String accountUpdatedQueue;

    @Value("${account.updated.routingKey}")
    private String accountUpdatedRoutingKey;

    @Value("${transaction.registered.exchange}")
    private String transactionRegisteredExchange;

    @Value("${transaction.registered.queue}")
    private String transactionRegisteredQueue;

    @Value("${transaction.registered.routingKey}")
    private String transactionRegisteredRoutingKey;

    public String getAccountExchange() {
        return accountCreatedExchange;
    }

    public String getAccountQueue() {
        return accountCreatedQueue;
    }

    public String getAccountRoutingKey() {
        return accountCreatedRoutingKey;
    }

    public String getTransactionExchange() {
        return transactionRegisteredExchange;
    }

    public String getTransactionQueue() {
        return transactionRegisteredQueue;
    }

    public String getTransactionRoutingKey() {
        return transactionRegisteredRoutingKey;
    }

    public String getAccountUpdatedExchange() {
        return accountUpdatedExchange;
    }

    public String getAccountUpdatedQueue() {
        return accountUpdatedQueue;
    }

    public String getAccountUpdatedRoutingKey() {
        return accountUpdatedRoutingKey;
    }
}
