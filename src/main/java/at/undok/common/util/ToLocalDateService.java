package at.undok.common.util;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Service
public class ToLocalDateService {

    public LocalDate formatStringToLocalDate(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
        return localDate;
    }

    public LocalDateTime formatStringToLocalDateTime(String dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
        return localDateTime;
    }

    public String localDateToString(LocalDate localDate) {
        Locale locale = new Locale("de", "AT");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd. MMM yyyy", locale);
        return localDate.format(dateTimeFormatter);
    }

    public String localDateTimeToString(LocalDateTime localDateTime) {
        Locale locale = new Locale("de", "AT");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd. MMM yyyy HH:mm", locale);
        return localDateTime.format(dateTimeFormatter);
    }

}
