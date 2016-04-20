/* Created by steinar on 08.01.12 at 21:46 */
package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.AccountRepository;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.common.DatabaseHelper;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.PeppolMessageNotFoundException;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.message.TransferDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

/**
 * Integration test verifying that messages can be filtered by various params
 *
 * @author Adam Mscisz adam@sendregning.no
 */
@Guice(moduleFactory = TestModuleFactory.class)
public class InboxWithMessageWithoutUUIDTest {

    Logger logger = LoggerFactory.getLogger(InboxWithMessageWithoutUUIDTest.class);

    private final AccountRepository accountRepository;
    private final DatabaseHelper databaseHelper;
    private final PeppolMessageRepository peppolMessageRepository;

    private Integer messageNo;
    private Integer messageNo2;
    private Integer messageNo3;
    private String receiver1 = "9908:976098898";
    private RingoAccount ringoAccount;
    private ParticipantId sender;

    @Inject
    public InboxWithMessageWithoutUUIDTest(AccountRepository accountRepository, DatabaseHelper databaseHelper, PeppolMessageRepository peppolMessageRepository) {
        this.accountRepository = accountRepository;
        this.databaseHelper = databaseHelper;
        this.peppolMessageRepository = peppolMessageRepository;
    }

    /**
     * This test must be run as last one, because it creates new message which would impact other tests
     */
    @Test(groups = {"persistence"})
    public void testMessageIdWithNoUUID() throws PeppolMessageNotFoundException {

        //proper message
        messageNo = databaseHelper.createMessage(ringoAccount.getId().toInteger(), TransferDirection.IN, ObjectMother.getAdamsParticipantId().stringValue(), receiver1, UUID.randomUUID().toString(), null);
        //uuid = null
        messageNo2 = databaseHelper.createMessage(ringoAccount.getId().toInteger(), TransferDirection.IN, ObjectMother.getAdamsParticipantId().stringValue(), receiver1, null, null);
        //uuid = ''
        messageNo3 = databaseHelper.createMessage(ringoAccount.getId().toInteger(), TransferDirection.IN, ObjectMother.getAdamsParticipantId().stringValue(), receiver1, "", null);

        //we expect to have only one message
        Integer inboxCount = peppolMessageRepository.getInboxCount(ringoAccount.getId());
        assertEquals((Integer) 1, inboxCount);

        //we expect to have only one message
        List<MessageMetaData> undeliveredInboundMessagesByAccount = peppolMessageRepository.findUndeliveredInboundMessagesByAccount(ringoAccount.getId());
        assertEquals(1, undeliveredInboundMessagesByAccount.size());

    }

    @BeforeMethod
    public void setUp() throws Exception {
        ringoAccount = accountRepository.createAccount(ObjectMother.getAdamsAccount(), ObjectMother.getAdamsParticipantId());
        sender = ObjectMother.getAdamsParticipantId();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        databaseHelper.deleteAllMessagesForAccount(ringoAccount);
        accountRepository.deleteAccount(ringoAccount.getId());
    }

}