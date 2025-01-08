package ec.com.sofka.config;

import ec.com.sofka.RabbitEnvProps;
import org.springframework.amqp.core.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    private final RabbitEnvProps envProperties;

    public RabbitConfig(RabbitEnvProps envProperties) {
        this.envProperties = envProperties;
    }

    @Bean
    public TopicExchange accountExchange() {
        return new TopicExchange(envProperties.getAccountExchange(), true, false);
    }


    @Bean
    public Queue accountQueue() {
        return new Queue(envProperties.getAccountQueue(), true);
    }

    @Bean
    public Binding accountBinding() {
        return BindingBuilder.bind(accountQueue())
                .to(accountExchange())
                .with(envProperties.getAccountRoutingKey());
    }

    @Bean
    public TopicExchange transactionExchange() {
        return new TopicExchange(envProperties.getTransactionExchange(), true, false);
    }


    @Bean
    public Queue transactionQueue() {
        return new Queue(envProperties.getTransactionQueue(), true);
    }

    @Bean
    public Binding transactionBinding() {
        return BindingBuilder.bind(transactionQueue())
                .to(transactionExchange())
                .with(envProperties.getTransactionRoutingKey());
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> initializeBeans(AmqpAdmin amqpAdmin) {
        return event -> {


            amqpAdmin.declareExchange(accountExchange());
            amqpAdmin.declareQueue(accountQueue());
            amqpAdmin.declareBinding(accountBinding());

            amqpAdmin.declareExchange(transactionExchange());
            amqpAdmin.declareQueue(transactionQueue());
            amqpAdmin.declareBinding(transactionBinding());
        };
    }

}
