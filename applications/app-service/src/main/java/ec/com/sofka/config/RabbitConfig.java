package ec.com.sofka.config;

import ec.com.sofka.RabbitEnvProps;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    private  final RabbitEnvProps rabbitEnvProps;

    public RabbitConfig(RabbitEnvProps rabbitEnvProps){
        this.rabbitEnvProps = rabbitEnvProps;
    }

    @Bean
    public TopicExchange accountCreatedExchange() {
        return new TopicExchange(rabbitEnvProps.getAccountExchange());
    }

    @Bean
    public Queue accountCreatedQueue() {
        return new Queue(rabbitEnvProps.getAccountQueue(), true);
    }

    @Bean
    public Binding accountCreatedBinding() {
        return BindingBuilder.bind(accountCreatedQueue())
                .to(accountCreatedExchange())
                .with(rabbitEnvProps.getAccountRoutingKey());
    }

    @Bean
    public TopicExchange accountUpdatedExchange() {
        return new TopicExchange(rabbitEnvProps.getAccountUpdatedExchange());
    }

    @Bean
    public Queue accountUpdatedQueue() {
        return new Queue(rabbitEnvProps.getAccountUpdatedQueue(), true);
    }

    @Bean
    public Binding accountUpdatedBinding() {
        return BindingBuilder.bind(accountUpdatedQueue())
                .to(accountUpdatedExchange())
                .with(rabbitEnvProps.getAccountUpdatedRoutingKey());
    }


    @Bean
    public TopicExchange transactionRegisteredExchange() {
        return new TopicExchange(rabbitEnvProps.getTransactionExchange());
    }

    @Bean
    public Queue transactionRegisteredQueue() {
        return new Queue(rabbitEnvProps.getTransactionQueue(), true);
    }

    @Bean
    public Binding transactionRegisteredBinding() {
        return BindingBuilder.bind(transactionRegisteredQueue())
                .to(transactionRegisteredExchange())
                .with(rabbitEnvProps.getTransactionRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate rabbitTemplateBean(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> initializeBeans(AmqpAdmin amqpAdmin) {
        return event -> {
            amqpAdmin.declareExchange(accountCreatedExchange());
            amqpAdmin.declareQueue(accountCreatedQueue());
            amqpAdmin.declareBinding(accountCreatedBinding());

            amqpAdmin.declareExchange(transactionRegisteredExchange());
            amqpAdmin.declareQueue(transactionRegisteredQueue());
            amqpAdmin.declareBinding(transactionRegisteredBinding());

            amqpAdmin.declareExchange(accountUpdatedExchange());
            amqpAdmin.declareQueue(accountUpdatedQueue());
            amqpAdmin.declareBinding(accountUpdatedBinding());
        };
    }

}
