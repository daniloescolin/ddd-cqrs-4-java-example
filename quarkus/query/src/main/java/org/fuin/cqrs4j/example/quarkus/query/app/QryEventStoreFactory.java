package org.fuin.cqrs4j.example.quarkus.query.app;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.fuin.esc.api.EventStore;
import org.fuin.esc.eshttp.ESEnvelopeType;
import org.fuin.esc.eshttp.ESHttpEventStore;
import org.fuin.esc.eshttp.IESHttpEventStore;
import org.fuin.esc.spi.SerDeserializerRegistry;

/**
 * CDI factory that creates an event store connection and repositories.
 */
@ApplicationScoped
public class QryEventStoreFactory {

    /**
     * Creates an event store.<br>
     * <br>
     * CAUTION: The returned event store instance is NOT thread safe.
     * 
     * @param config
     *            Configuration.
     * @param registry
     *            Serialization registry.
     * 
     * @return Dependent scope event store.
     */
    @Produces
    @Dependent
    public IESHttpEventStore createEventStore(final QryConfig config, final SerDeserializerRegistry registry) {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(config.getEventStoreUser(),
                config.getEventStorePassword());
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        final ThreadFactory threadFactory = Executors.defaultThreadFactory();
        final IESHttpEventStore eventStore = new ESHttpEventStore(threadFactory, config.getEventStoreURL(), ESEnvelopeType.JSON, registry,
                registry, credentialsProvider);
        eventStore.open();
        return eventStore;

    }

    /**
     * Closes the event store when the context is disposed.
     * 
     * @param es
     *            Event store to close.
     */
    public void closeEventStore(@Disposes final EventStore es) {
        es.close();
    }

}