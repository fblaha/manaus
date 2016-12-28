package cz.fb.manaus.reactor.traffic;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class BetTransactionLogger {
    private static final Logger log = Logger.getLogger(BetTransactionLogger.class.getSimpleName());
    private Date date = DateUtils.ceiling(new Date(), Calendar.HOUR);
    private long placeNumber = 0;
    private long updateNumber = 0;

    synchronized public void incrementBy(int inc, boolean place) {
        Date currHour = DateUtils.ceiling(new Date(), Calendar.HOUR);
        if (!currHour.equals(date)) {
            placeNumber = 0;
            updateNumber = 0;
            date = currHour;
        }
        if (place) {
            placeNumber += inc;
        } else {
            updateNumber += inc;
        }
        log.log(Level.INFO, "Transaction stats : total ''{0}'' place ''{1}'' update ''{2}''  in hour ''{3}''",
                new Object[]{placeNumber + updateNumber, placeNumber, updateNumber, currHour});
    }


}