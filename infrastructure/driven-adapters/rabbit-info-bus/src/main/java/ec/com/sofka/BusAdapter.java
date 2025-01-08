package ec.com.sofka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.com.sofka.gateway.BusMessage;
import ec.com.sofka.log.Log;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class BusAdapter implements BusMessage {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitEnvProps envProperties;

    public BusAdapter(RabbitTemplate rabbitTemplate, RabbitEnvProps envProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.envProperties = envProperties;
    }

    @Override
    public void sendMsg(Log log) {
        String exchange = switch (log.getEntity()) {
            case "account" -> envProperties.getAccountExchange();
            case "transaction" -> envProperties.getTransactionExchange();
            default -> throw new IllegalArgumentException("Invalid entity type: " + log.getEntity());
        };

        String routingKey = switch (log.getEntity()) {
            case "account" -> envProperties.getAccountRoutingKey();
            case "transaction" -> envProperties.getTransactionRoutingKey();
            default -> throw new IllegalArgumentException("Invalid entity type: " + log.getEntity());
        };

        ObjectMapper mapper = new ObjectMapper();

        try {
            String payload = mapper.writeValueAsString(Map.of(
                    "entity", log.getEntity(),
                    "message", log.getMessage()
            ));
            rabbitTemplate.convertAndSend(exchange, routingKey, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating payload", e);
        }
    }

}
