import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Topic;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

@Stateless
public class EnterpriseEventSender {
    private static final Logger LOG = LoggerFactory.getLogger(EnterpriseEventSender.class);

    @Resource(name = "java:/jms/topic/EnterpriseEventBus")
    private Topic esb;

    @Inject
    @JMSConnectionFactory("java:/jms/EventsProducerConnectionFactory")
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

    public void sendEventInvoiceCancelled(long jobId) {
        sendEvent("invoice.job.cancelled", singletonMap("job.id", jobId));
    }

    public void sendEventNewInvoice(long jobId, long... costcenterIds) {
        Map<String, Object> eventAttr = new HashMap<>();
        eventAttr.put("job.id", jobId);
        eventAttr.put("costcenter.ids", costcenterIds);

        sendEvent("invoice.verification.trigger.failed", eventAttr);
    }

    private void sendEvent(String event, Map<String, Object> eventAttr) {
        ctx.createProducer()
                .setProperty("JMSType", event) // message selector
                .send(esb, eventAttr);
    }
}
