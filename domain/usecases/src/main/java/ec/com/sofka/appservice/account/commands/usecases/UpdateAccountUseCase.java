package ec.com.sofka.appservice.account.commands.usecases;

import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.appservice.account.commands.UpdateAccountCommand;
import ec.com.sofka.appservice.account.queries.responses.AccountResponse;
import ec.com.sofka.gateway.AccountRepository;
import ec.com.sofka.gateway.BusEvent;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseExecute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UpdateAccountUseCase implements IUseCaseExecute<UpdateAccountCommand, AccountResponse> {
    private final AccountRepository accountRepository;
    private final IEventStore eventRepository;
    private final BusEvent busEvent;

    public UpdateAccountUseCase(AccountRepository accountRepository, IEventStore eventRepository, BusEvent busEvent) {
        this.accountRepository = accountRepository;
        this.eventRepository = eventRepository;
        this.busEvent = busEvent;
    }

/*    @Override
    public Mono<AccountResponse> execute(UpdateAccountCommand request) {
        return eventRepository.findAggregate(request.getAggregateId(), "customer")
                .collectList()
                .flatMap(events -> {
                    Mono<Customer> customerMono = Customer.from(request.getAggregateId(), Flux.fromIterable(events));

                    return customerMono.flatMap(customer -> {
                        customer.updateAccount(
                                customer.getAccount().getId().getValue(),
                                request.getBalance(),
                                request.getAccountNumber(),
                                request.getAccountHolder()
                        );

                        Mono<AccountDTO> updateResultMono = accountRepository.update(
                                new AccountDTO(customer.getAccount().getId().getValue(),
                                        request.getAccountNumber(),
                                        customer.getAccount().getBalance().getValue(),
                                        request.getAccountHolder()
                                ));

                        return updateResultMono.flatMap(result -> {
                            Flux.fromIterable(customer.getUncommittedEvents())
                                    .doOnNext(eventRepository::save)
                             //       .map(busEvent::sendEventAccountUpdated)
                                    .subscribe(); // No queremos bloquear la ejecución principal, así que usamos `subscribe`


                            customer.markEventsAsCommitted();

                            return Mono.just(new AccountResponse(
                                    request.getAggregateId(),
                                    result.getId(),
                                    result.getBalance(),
                                    result.getAccountNumber(),
                                    result.getAccountHolder()
                            ));
                        });
                    });
                });

    }*/

   /* @Override
    public Mono<AccountResponse> execute(UpdateAccountCommand request) {
        return eventRepository.findAggregate(request.getAggregateId(), "customer")
                .collectList() // Obtener todos los eventos del cliente
                .flatMap(events -> {
                    Mono<Customer> customerMono = Customer.from(request.getAggregateId(), Flux.fromIterable(events));

                    return customerMono.flatMap(customer -> {
                        // Actualizar la cuenta con los datos de la solicitud
                        customer.updateAccount(
                                customer.getAccount().getId().getValue(),
                                request.getBalance(),
                                request.getAccountNumber(),
                                request.getAccountHolder()
                        );

                        // Actualizar la cuenta en el repositorio
                        Mono<AccountDTO> updateResultMono = accountRepository.update(
                                new AccountDTO(
                                        customer.getAccount().getId().getValue(),
                                        request.getAccountNumber(),
                                        customer.getAccount().getBalance().getValue(),
                                        request.getAccountHolder()
                                )
                        );

                        return updateResultMono.flatMap(result -> {
                            // Guardar y enviar los eventos de manera reactiva
                            return Flux.fromIterable(customer.getUncommittedEvents())
                                    .flatMap(event ->
                                        // Guardar el evento y enviar el mensaje de evento de manera reactiva
                                        eventRepository.save(event)  // Guardar el evento
                                                .then(Mono.defer(() -> {
                                                    busEvent.sendEventAccountUpdated(Mono.just(event));
                                                    return Mono.empty();
                                                }))  // Enviar el evento actualizado
                                    )
                                    .then(  // Esperar hasta que todos los eventos sean guardados y enviados
                                            Mono.defer(() -> {
                                                // Realizar el commit de los eventos después de que se haya procesado todo
                                                customer.markEventsAsCommitted();  // Confirmar los eventos procesados
                                                return Mono.just(new AccountResponse(
                                                        request.getAggregateId(),
                                                        result.getId(),
                                                        result.getBalance(),
                                                        result.getAccountNumber(),
                                                        result.getAccountHolder()
                                                ));
                                            })
                                    );
                        });
                    });
                });

    }*/

    @Override
    public Mono<AccountResponse> execute(UpdateAccountCommand request) {
        return eventRepository.findAggregateforUpdate(request.getAggregateId()) // Obtener eventos como Flux<DomainEvent>
                .collectList() // Agrupar los eventos en una lista
                .flatMap(events -> {
                    Flux<DomainEvent> eventFlux = Flux.fromIterable(events);

                    return Customer.from(request.getAggregateId(), eventFlux)
                            .flatMap(customer -> {
                                // Actualizar la cuenta en el agregado
                                customer.updateAccount(
                                        customer.getAccount().getId().getValue(),
                                        request.getBalance(),
                                        request.getAccountNumber(),
                                        request.getAccountHolder()
                                );

                                // Guardar los eventos no comprometidos de forma reactiva
                                return Flux.fromIterable(customer.getUncommittedEvents())
                                        .flatMap(eventRepository::save) // Guardar cada evento en el EventStore
                                        .doOnNext(updateEvent -> busEvent.sendEventAccountUpdated(Mono.just(updateEvent))) // Enviar eventos a través de BusEvent
                                        .then(Mono.defer(() -> {
                                            // Marcar los eventos como comprometidos
                                            customer.markEventsAsCommitted();

                                            // Crear y devolver la respuesta
                                            return Mono.just(new AccountResponse(
                                                    request.getAggregateId(),
                                                    customer.getAccount().getId().getValue(),
                                                    customer.getAccount().getBalance().getValue(),
                                                    customer.getAccount().getAccountNumber().getValue(),
                                                    customer.getAccount().getAccountHolder().getValue()
                                            ));
                                        }));
                            });
                });
    }


}
