package be.wegenenverkeer.atomium.japi.format;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * Created by Karel Maesen, Geovise BVBA on 22/08/16.
 */
public class AdaptersTest {

    @Test
    public void testSerializationOfDateTime(){
        DateTime dt = new DateTime(2015, 8, 26, 19, 41, 44, 0, DateTimeZone.forOffsetHours(2));
        String dtText = Adapters.jodaOutputFormatterWithSecondsAndOptionalTZ.print(dt);
        assertEquals("2015-08-26T19:41:44+02:00", dtText);
    }

    @Test
    public void testSerializationOfUTCDateTime(){
        DateTime dt = new DateTime(2016, 8, 22, 16, 20, 6, 0, DateTimeZone.forOffsetHours(0));
        String dtText = Adapters.jodaOutputFormatterWithSecondsAndOptionalTZ.print(dt);
        assertEquals("2016-08-22T16:20:06Z", dtText);
    }


    @Test
    public void testSerializationOfDateTimeJ8(){
        ZonedDateTime dt = ZonedDateTime.of(2016, 8, 22, 16, 20, 6, 0, TimeZone.getTimeZone("Europe/Brussels").toZoneId());
        String dtText = Adapters.formatter.format(dt);
        assertEquals("2016-08-22T16:20:06+02:00", dtText);
    }

    @Test
    public void testSerializationOfUTCDateTimeJ8(){
        OffsetDateTime dt = OffsetDateTime.of(2016, 8, 22, 16, 20, 6, 0, ZoneOffset.ofHours(0));
        String dtText = Adapters.formatter.format(dt);
        assertEquals("2016-08-22T16:20:06Z", dtText);
    }

    @Test
    public void testDeSerializationOfDateTimeJ8(){
        ZonedDateTime dt = ZonedDateTime.of(2016, 8, 22, 16, 20, 6, 0, TimeZone.getTimeZone("Europe/Brussels").toZoneId());
        OffsetDateTime parsed = OffsetDateTime.parse("2016-08-22T16:20:06+02:00", Adapters.datetimeParser);
        assertEquals(dt.toOffsetDateTime(), parsed);
    }

    @Test
    public void testDeSerializationOfUTCDateTimeJ8(){
        OffsetDateTime dt = OffsetDateTime.of(2016, 8, 22, 16, 20, 6, 0, ZoneOffset.ofHours(0));
        OffsetDateTime parsed = OffsetDateTime.parse("2016-08-22T16:20:06Z", Adapters.datetimeParser);
        assertEquals(dt, parsed);
    }

    @Test
    public void testDeserializatoinOfVariantDateTimeJ8(){
        OffsetDateTime dt = OffsetDateTime.of(2015, 8, 26, 19, 41, 44, 0, ZoneOffset.ofHours(2));
        OffsetDateTime parsed = OffsetDateTime.parse("2015-08-26T19:41:44+0200", Adapters.datetimeParser);
        assertEquals(dt, parsed);
    }



}
