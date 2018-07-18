import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;

import java.util.Calendar;

import static javax.xml.bind.DatatypeConverter.parseDateTime;

/**
 * See the notices in
 * {@link <a href="https://docs.oracle.com/cd/E19798-01/821-1841/bncer/index.html">The Java EE 6 Tutorial</a>}
 * <br>
 * Message selectors assign the work of filtering messages to the JMS provider rather than to the application. <br>
 * SQL92 conditional expression syntax: JMSType = 'invoice.verification.trigger.failed' <br>
 * <br>
 * The Artemis Broker is able to have selectors in the queue in <em>broker.xml</em> and therefore we think
 * the <em>Message Selector</em> is performed in the broker of JMS Provider and not in the MDB engine, see the article:
 * {@link <a href="https://activemq.apache.org/artemis/docs/1.5.0/queue-attributes.html">Apache Artemis Broker.</a>
 * <br>
 * AMQ sets property <tt>alwaysSessionAsync</tt> to <tt>true</tt> by default on the ActiveMQ ConnectionFactory. You can increase
 * throughput by passing messages straight through the Session to the Consumer by setting <tt>alwaysSessionAsync</tt>
 * to <tt>false</tt> which means that a thread pool would not be used and messages would not be received concurrently and the
 * thread pool would not be exhausted in very high load, see the article
 * {@link <a href="https://activemq.apache.org/performance-tuning.html">Performance Tuning</a>}.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "JMSType = 'invoice.verification.trigger.failed'"),
        @ActivationConfigProperty(propertyName = "connectionFactoryLookup", propertyValue = "java:/jms/EventsConsumerConnectionFactory"),
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/topic/EnterpriseEventBus"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "invoice"),
        @ActivationConfigProperty(propertyName = "alwaysSessionAsync", propertyValue = "false")
})
@SuppressWarnings("unused")
public class EnterpriseEventReceiverMDB
        implements MessageListener {

    public void onMessage(Message message) {
        try {
            MapMessage event = message.getBody(MapMessage.class);

            Calendar invoiceFrom = parseDateTime(event.getString("invoice.from.iso-8601-utc")); // e.g. 2010-01-01T12:00:00Z
            Calendar invoiceTo = parseDateTime(event.getString("invoice.to.iso-8601-utc"));
            long contractId = event.getLong("job.id");
            long carparkId = event.getLong("costcenter.id");

            // Call pre-invoice here !!!
        } catch (JMSException e) {
            throw new JMSRuntimeException(e.getLocalizedMessage(), e.getErrorCode(), e);
        }
    }

}
