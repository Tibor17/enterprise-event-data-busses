import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import static javax.xml.bind.DatatypeConverter.printDateTime;

public class Utils {

    private static void putDateRange(Calendar from, Calendar to, Map<String, Object> eventAttr) {
        // e.g. 2010-01-01T12:00:00Z
        eventAttr.put("invoice.from.iso-8601-utc", printDateTime(toUTC(from)));
        eventAttr.put("invoice.to.iso-8601-utc", printDateTime(toUTC(to)));
    }

    private static Calendar toUTC(Calendar c) {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.setTimeInMillis(c.getTimeInMillis());
        return utc;
    }
}
