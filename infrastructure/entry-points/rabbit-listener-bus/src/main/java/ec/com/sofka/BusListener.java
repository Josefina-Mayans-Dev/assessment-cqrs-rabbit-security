package ec.com.sofka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.com.sofka.applogs.PrintLogUseCase;
import ec.com.sofka.gateway.BusMessageListener;
import ec.com.sofka.log.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BusListener implements BusMessageListener {
    private final PrintLogUseCase printLogUseCase;
    private final RabbitEnvProps envProperties;

    public BusListener(PrintLogUseCase printLogUseCase, RabbitEnvProps envProperties) {
        this.printLogUseCase = printLogUseCase;
        this.envProperties = envProperties;
    }

    @Override
    @RabbitListener(queues = "#{@rabbitEnvProps.getAllQueues()}")
    public void receiveMsg(String payload) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> messageMap = mapper.readValue(payload, new TypeReference<>() {});
            String entity = messageMap.get("entity");
            String message = messageMap.get("message");

            Log log = new Log(message, entity, LocalDateTime.now());

            printLogUseCase.accept(log).subscribe();
        } catch (JsonProcessingException e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}