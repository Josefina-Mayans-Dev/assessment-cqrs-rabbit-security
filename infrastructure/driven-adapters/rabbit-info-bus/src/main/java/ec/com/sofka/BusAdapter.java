package ec.com.sofka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.com.sofka.gateway.BusEvent;
import ec.com.sofka.generics.domain.DomainEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;


@Service
public class BusAdapter implements BusEvent {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitEnvProps rabbitEnvProps;

    public BusAdapter(RabbitTemplate rabbitTemplate, RabbitEnvProps rabbitEnvProps) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitEnvProps = rabbitEnvProps;
    }

    @Override
    public void sendEventAccountCreated(Mono<DomainEvent> event) {
        event.subscribe(accountCreated -> {
                    rabbitTemplate.convertAndSend(rabbitEnvProps.getAccountExchange(), rabbitEnvProps.getAccountRoutingKey(), accountCreated);
                }
        );
    }

    @Override
    public void sendEventTransactionRegistered(Mono<DomainEvent> event) {
        event.subscribe(transactionRegistered -> {
            rabbitTemplate.convertAndSend(rabbitEnvProps.getTransactionExchange(), rabbitEnvProps.getTransactionRoutingKey(), transactionRegistered);
        });
    }

    @Override
    public void sendEventAccountUpdated(Mono<DomainEvent> event) {
        event.subscribe(accountUpdated -> {
                    rabbitTemplate.convertAndSend(rabbitEnvProps.getAccountUpdatedExchange(), rabbitEnvProps.getAccountUpdatedRoutingKey(), accountUpdated);
                }
        );
    }

}
