package gr.hua.dit.mycitygov.core.port;

import java.time.LocalDate;

public interface HolidayProviderPort {
    boolean isHoliday(LocalDate date);
}