import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Topic;

@Stateless
public class EnterpriseDataSender {
    private static final Logger LOG = LoggerFactory.getLogger(EnterpriseDataSender.class);

    @Resource(name = "java:/jms/topic/EnterpriseDataBus")
    private Topic esb;

    @Inject
    @JMSConnectionFactory("java:/jms/DataProducerConnectionFactory")
    private JMSContext ctx;

    @PostConstruct
    private void setupListeners() {
        ctx.setExceptionListener(e -> {
            LOG.error("Error Code: {}", e.getErrorCode());
            Exception linkedException = e.getLinkedException();
            if (linkedException != null) {
                LOG.error(linkedException.getLocalizedMessage(), linkedException);
            }
            LOG.error(e.getLocalizedMessage(), e);
        });
    }

    public void sendEventInvoiceCancelled(String txt) {
        sendEvent("invoice.job.report", txt);
    }

    private void sendEvent(String event, String txt) {
        ctx.createProducer()
                .setProperty("JMSType", event) // message selector
                .send(esb, txt);
    }
}
