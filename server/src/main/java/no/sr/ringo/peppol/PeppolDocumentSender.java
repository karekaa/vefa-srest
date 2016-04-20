package no.sr.ringo.peppol;

import no.sr.ringo.message.MessageMetaData;
import org.w3._2009._02.ws_tra.FaultMessage;

import java.util.Date;

/**
 * User: andy
 * Date: 2/1/12
 * Time: 12:45 PM
 */
public interface PeppolDocumentSender {

    /**
     * Sends the document referenced by the message
     *
     * @param message the Message to send
     * @param xmlMessage
     * @return a Recipient containing the UUID and the TimeStamp for the delivery
     */
    TransmissionReceipt sendDocument(MessageMetaData message, String xmlMessage) throws FaultMessage;


    /**
     * Value Object containing
     * the Transmission Timestamp and the UUID of the transfer.
     */
    public static final class TransmissionReceipt {

        private final String messageId;
        private final Date date;

        public TransmissionReceipt(String messageId, Date date) {
            this.messageId = messageId;
            this.date = date;
        }

        public String getMessageId() {
            return messageId;
        }

        public Date getDate() {
            return date;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TransmissionReceipt that = (TransmissionReceipt) o;

            if (date != null ? !date.equals(that.date) : that.date != null) return false;
            if (messageId != null ? !messageId.equals(that.messageId) : that.messageId != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = messageId != null ? messageId.hashCode() : 0;
            result = 31 * result + (date != null ? date.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("TransmissionReceipt");
            sb.append("{messageId=").append(messageId);
            sb.append(", date=").append(date);
            sb.append('}');
            return sb.toString();
        }
    }

}