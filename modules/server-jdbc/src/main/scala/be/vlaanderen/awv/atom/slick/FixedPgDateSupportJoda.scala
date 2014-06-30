package be.vlaanderen.awv.atom.slick

import com.github.tminglei.slickpg._
import java.sql.{Timestamp, Time, Date}
import java.util.Calendar
import org.joda.time.{Period, LocalDateTime, LocalTime, LocalDate}
import org.postgresql.util.PGInterval
import scala.slick.driver.PostgresDriver
import scala.slick.lifted.Column

/**
 * De driver heeft een bug met month handling in versie 0.5.0-RC1, fix deze hier tot nieuwe versie uitkomt waarin dit gefixed is
 */
trait FixedPgDateSupportJoda extends date.PgDateExtensions with date.PgDateJavaTypes with utils.PgCommonJdbcTypes { driver: PostgresDriver =>

  type DATE   = LocalDate
  type TIME   = LocalTime
  type TIMESTAMP = LocalDateTime
  type INTERVAL  = Period

  trait DateTimeImplicits {
    implicit val jodaDateTypeMapper = new DateJdbcType(sqlDate2jodaDate, jodaDate2sqlDate)
    implicit val jodaTimeTypeMapper = new TimeJdbcType(sqlTime2jodaTime, jodaTime2sqlTime)
    implicit val jodaDateTimeTypeMapper = new TimestampJdbcType(sqlTimestamp2jodaDateTime, jodaDateTime2sqlTimestamp)
    implicit val jodaPeriodTypeMapper = new GenericJdbcType[Period]("interval", pgIntervalStr2jodaPeriod)

    ///
    implicit def dateColumnExtensionMethods(c: Column[LocalDate]) = new DateColumnExtensionMethods(c)
    implicit def dateOptColumnExtensionMethods(c: Column[Option[LocalDate]]) = new DateColumnExtensionMethods(c)

    implicit def timeColumnExtensionMethods(c: Column[LocalTime]) = new TimeColumnExtensionMethods(c)
    implicit def timeOptColumnExtensionMethods(c: Column[Option[LocalTime]]) = new TimeColumnExtensionMethods(c)

    implicit def timestampColumnExtensionMethods(c: Column[LocalDateTime]) = new TimestampColumnExtensionMethods(c)
    implicit def timestampOptColumnExtensionMethods(c: Column[Option[LocalDateTime]]) = new TimestampColumnExtensionMethods(c)

    implicit def intervalColumnExtensionMethods(c: Column[Period]) = new IntervalColumnExtensionMethods(c)
    implicit def intervalOptColumnExtensionMethods(c: Column[Option[Period]]) = new IntervalColumnExtensionMethods(c)
  }

  //--------------------------------------------------------------------

  /// sql.Date <-> joda LocalDate
  private def sqlDate2jodaDate(date: Date): LocalDate = {
    new LocalDate(date)
  }
  private def jodaDate2sqlDate(date: LocalDate): Date = {
    new java.sql.Date(date.toDate.getTime)
  }


  /// sql.Time <-> joda LocalTime
  private def sqlTime2jodaTime(time: Time): LocalTime = {
    val cal = Calendar.getInstance()
    cal.setTime(time)
    new LocalTime(
      cal.get(Calendar.HOUR_OF_DAY),
      cal.get(Calendar.MINUTE),
      cal.get(Calendar.SECOND),
      cal.get(Calendar.MILLISECOND) * 1000
    )
  }
  private def jodaTime2sqlTime(time: LocalTime): Time = {
    val cal = Calendar.getInstance()
    cal.set(0, 0, 0, time.getHourOfDay, time.getMinuteOfHour, time.getSecondOfMinute)
    cal.set(Calendar.MILLISECOND, time.getMillisOfSecond)
    new Time(cal.getTimeInMillis)
  }

  /// sql.Timestamp <-> joda LocalDateTime
  private def sqlTimestamp2jodaDateTime(ts: Timestamp): LocalDateTime = {
    val cal = Calendar.getInstance()
    cal.setTime(ts)
    new LocalDateTime(
      cal.get(Calendar.YEAR),
      cal.get(Calendar.MONTH) +1,
      cal.get(Calendar.DAY_OF_MONTH),
      cal.get(Calendar.HOUR_OF_DAY),
      cal.get(Calendar.MINUTE),
      cal.get(Calendar.SECOND),
      cal.get(Calendar.MILLISECOND)
    )
  }
  private def jodaDateTime2sqlTimestamp(dt: LocalDateTime): Timestamp = {
    val cal = Calendar.getInstance()
    cal.set(dt.getYear, dt.getMonthOfYear - 1, dt.getDayOfMonth, dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute)
    cal.set(Calendar.MILLISECOND, dt.getMillisOfSecond)
    new Timestamp(cal.getTimeInMillis)
  }

  /// pg interval string <-> joda Duration
  private def pgIntervalStr2jodaPeriod(intervalStr: String): Period = {
    val pgInterval = new PGInterval(intervalStr)
    val seconds = Math.floor(pgInterval.getSeconds) .asInstanceOf[Int]
    val millis  = ((pgInterval.getSeconds - seconds) * 1000) .asInstanceOf[Int]

    new Period(
      pgInterval.getYears,
      pgInterval.getMonths,
      0,  // weeks
      pgInterval.getDays,
      pgInterval.getHours,
      pgInterval.getMinutes,
      seconds, millis
    )
  }
}
