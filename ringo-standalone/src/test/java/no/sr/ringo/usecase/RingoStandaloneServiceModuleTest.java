package no.sr.ringo.usecase;

import com.google.inject.Guice;
import com.google.inject.Injector;
import no.difi.oxalis.outbound.OxalisOutboundComponent;
import no.difi.ringo.UnitTestConfigModule;
import no.sr.ringo.guice.OxalisOutboundModule;
import no.sr.ringo.guice.RingoServiceModule;
import no.sr.ringo.oxalis.OxalisDocumentSender;
import no.sr.ringo.oxalis.PeppolDocumentSender;
import no.sr.ringo.persistence.jdbc.RingoDataSourceModule;
import no.sr.ringo.persistence.jdbc.RingoRepositoryModule;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * User: Adam
 * Date: 5/29/13
 * Time: 12:59 PM
 */
public class RingoStandaloneServiceModuleTest {

    @Test
    public void testOxalisSenderInjected() {

        Injector injector = Guice.createInjector(
                new UnitTestConfigModule(),

                new OxalisOutboundModule(),
                new RingoDataSourceModule(),
                new RingoServiceModule(),    // Production
                new RingoRepositoryModule()
        );

        SendQueuedMessagesUseCase useCase = injector.getInstance(SendQueuedMessagesUseCase.class);
        PeppolDocumentSender documentSender = useCase.getDocumentSender();

        assertTrue(documentSender instanceof OxalisDocumentSender);
    }

    @Test
    public void instantiateOxalisOutbound() throws Exception {
        final OxalisOutboundComponent oxalisOutboundComponent = new OxalisOutboundComponent();
    }
}
