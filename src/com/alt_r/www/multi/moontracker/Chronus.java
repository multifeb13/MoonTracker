package com.alt_r.www.multi.moontracker;

import java.util.Calendar;
import java.util.TimeZone;

public class Chronus
{
 private
  int year_; 
  int month_;
  int day_;

  int hour_;
  int minute_;
  int second_;

  String   tz_id_;
  Calendar cal_;

  public Chronus()
  {
    year_   = 0;
    month_  = 0;
    day_    = 0;
    
    hour_   = 0;
    minute_ = 0;
    second_ = 0;
  }
  public Chronus(String tz_id)
  {
    tz_id_ = new String(tz_id);
  }

  public void UpdateTime()
  {
    if( (tz_id_ == null) || (tz_id_.length() < 1))
    {
      cal_ = Calendar.getInstance();
    }
    else
    {
      TimeZone tz = TimeZone.getTimeZone(tz_id_);
      cal_ = Calendar.getInstance(tz);
    }

    SetTime();
  }

  public void AddDate( double date )
  {
    double date_tmp = 0;

    int day    = 0;
    
    int hour   = 0;
    int minute = 0;
    int second = 0;
    
    day    = (int)date; date_tmp = date - day;

    hour   = (int)(   date_tmp * 24 );
    minute = (int)( ( date_tmp * 1440  ) % 60 );
    second = (int)( ( date_tmp * 86400 ) % 60 );
    
    Add( Calendar.DAY_OF_MONTH, day    );

    Add( Calendar.HOUR_OF_DAY,  hour   );
    Add( Calendar.MINUTE,       minute );
    Add( Calendar.SECOND,       second );
  }

  public void AddDay ( int value )   { Add( Calendar.DAY_OF_MONTH, value ); }
  public void AddHour( int value )   { Add( Calendar.HOUR,         value ); }
  public void AddMinute( int value ) { Add( Calendar.MINUTE,       value ); }

  private void Add( int field, int days )
  {
    cal_.add( field, days );

    //更新
    SetTime();
  }

  public void SetTime()
  {
    SetTime( cal_.get( Calendar.YEAR         ),
             cal_.get( Calendar.MONTH        ) + 1,
             cal_.get( Calendar.DAY_OF_MONTH ),

             cal_.get( Calendar.HOUR_OF_DAY  ),
             cal_.get( Calendar.MINUTE       ),
             cal_.get( Calendar.SECOND       ) );
  }
  public void SetTime(int year, int month,  int day,
                      int hour, int minute, int second)
  {
    year_   = year;   cal_.set( Calendar.YEAR,         year       );
    month_  = month;  cal_.set( Calendar.MONTH,        month - 1  );  //(0 to 11)
    day_    = day;    cal_.set( Calendar.DAY_OF_MONTH, day        );

    hour_   = hour;   cal_.set( Calendar.HOUR_OF_DAY,  hour       );
    minute_ = minute; cal_.set( Calendar.MINUTE,       minute     );
    second_ = second; cal_.set( Calendar.SECOND,       second     );
  }
  public void SetTime( int hour, int minute, int second )
  {
                      cal_.set( Calendar.YEAR,         year_      );
                      cal_.set( Calendar.MONTH,        month_ - 1 );  //(0 to 11)
                      cal_.set( Calendar.DAY_OF_MONTH, day_       );

    hour_   = hour;   cal_.set( Calendar.HOUR_OF_DAY,  hour       );
    minute_ = minute; cal_.set( Calendar.MINUTE,       minute     );
    second_ = second; cal_.set( Calendar.SECOND,       second     );
  }

  public String GetDate()
  {
    return String.format( "%04d", year_   ) + "/" +
           String.format( "%02d", month_  ) + "/" +
           String.format( "%02d", day_    );
  }
  public String GetTime()
  {
    return String.format( "%02d", hour_   ) + ":" +
           String.format( "%02d", minute_ ) + ":" +
           String.format( "%02d", second_ );
  }

  public Double GetJD()
  {
    return GetJD(hour_, minute_, second_);
  }

  public Double GetJD(int jd_hour,
                      int jd_minute,
                      int jd_second)
  {
    int jd_year  = 0;
    int jd_month = 0;

    if( (month_ == (Calendar.JANUARY  + 1)) ||
        (month_ == (Calendar.FEBRUARY + 1)) )
    {
      jd_year  = year_  - 1;
      jd_month = month_ + 12;
    }
    else
    {
      jd_year  = year_;
      jd_month = month_;
    }

    return    (int)( jd_year  * (double)365.25)
            + (int)( jd_year  / 400)
            - (int)( jd_year  / 100)
            + (int)((jd_month - 2) * (double)30.59)
            + day_
            + (double)1721088.5
            + (double)(jd_hour   / 24.0)
            + (double)(jd_minute / 1440.0)
            + (double)(jd_second / 86400.0);
  }

  public double GetGST( double jd)
  {
    double gst = 0;

    gst = 24 * (    0.671262
                 + (1.0027379094 * (jd - 2440000.5)) );

    return gst - ( (int)( gst / 24 ) * 24 );
  }

  public static String toElapsedTime( double d )
  {
    //経過時間(d)を、時刻の文字列に変換
    int hour   = 0;
    int minute = 0;
    int second = 0;
    
    String ret = "";

    if( d < 0 )
    {
      d += 1.0;  //dが負 = 前日

      ret = "--:--";
    }
    else if( d > 1 )
    {
      d -= 1.0;  //dが１より大きい = 翌日

      ret = "--:--";
    }
    else
    {
      //30秒以上は1分に繰り上げ
      second = (int)( (d * 86400) % 60 );
      if( second >= 30 )
      {
        //1分を追加
        d += ( 1 / (double)( 24 * 60 ) );
      }

      //秒の繰り上げをした後に、時間と分を計算
      hour   = (int)(  d * 24          );
      minute = (int)( (d * 1440 ) % 60 );
      
      ret = String.format("%02d", hour  ) + ":" +
            String.format("%02d", minute);

//      ret = String.format("%02d", hour  ) + ":" +
//            String.format("%02d", minute) + ":" +
//            String.format("%02d", second);
    }

    return ret;
  }

  // ----------------------------------------------------------

  private int CalcDeltaT( int year )
  {
    //ΔTを計算
    //参照式 : JPL ( http://eclipse.gsfc.nasa.gov/SEcat5/deltatpoly.html )
    int t       = 0;
    int delta_t = 0;

    if( (year >= 1900) && (year < 1920) )
    {
      t = year - 1900;
      
      delta_t
        = (int)( -2.79
                 + ( 1.494119  * t              )
                 - ( 0.0598939 * Math.pow(t, 2) )
                 + ( 0.0061966 * Math.pow(t, 3) )
                 - ( 0.000197  * Math.pow(t, 4) ) );
    }
    else if( (year >= 1920) && (year < 1941) )
    {
      t = year - 1920;
      
      delta_t
        = (int)( 21.20
                 + ( 0.84493   * t              )
                 - ( 0.076100  * Math.pow(t, 2) )
                 + ( 0.0020936 * Math.pow(t, 3) ) );
    }
    else if( (year >= 1941) && (year < 1961) )
    {
      t = year - 1950;

      delta_t
        = (int)( 29.07
                 + ( 0.407 * t                     )
                 - ( Math.pow(t, 2) / (double)233  )
                 + ( Math.pow(t, 3) / (double)2547 ) );
    }
    else if( (year >= 1961) && (year < 1986) )
    {
      t = year - 1975;
      
      delta_t
        = (int)( 45.45
                 + ( 1.067 * t                    )
                 - ( Math.pow(t, 2) / (double)260 )
                 - ( Math.pow(t, 3) / (double)718 ) );
    }
    else if( (year >= 1986) && (year < 2005) )
    {
      t = year - 2000;

      delta_t
        = (int)( 63.86
                 + ( 0.3345        * t              )
                 - ( 0.060374      * Math.pow(t, 2) )
                 + ( 0.0017275     * Math.pow(t, 3) )
                 + ( 0.000651814   * Math.pow(t, 4) )
                 + ( 0.00002373599 * Math.pow(t, 5) ) );
    }
    else if( (year <= 2005) && (year < 2050) )
    {
      t = year - 2000;
      
      delta_t
        = (int)( 62.92
                 + ( 0.32217  * t              )
                 + ( 0.005589 * Math.pow(t, 2) ) );
    }
    else if( (year <= 2050) && (year < 2150) )
    {
      delta_t
        = (int)( -20
                 + ( 32     * Math.pow((year - 1820) / (double)100, 2) )
                 - ( 0.5628 * (2150 - year)                            ) );
    }
    else
    {
      delta_t = 64;  //1999年のΔT
    }
    
    return delta_t;
  }
  
  public double CalcT()
  {
    TimeZone tz   = TimeZone.getDefault();
    int tz_offset = tz.getRawOffset() / 1000 / ( 60 * 60 );

    return CalcT( year_, month_, day_, hour_, tz_offset );
  }
  public double CalcT( int hour )
  {
    TimeZone tz   = TimeZone.getDefault();
    int tz_offset = tz.getRawOffset() / 1000 / ( 60 * 60 );

    return CalcT( year_, month_, day_, hour, tz_offset );
  }
  public double CalcT( int hour, int tz_offset )
  {
    return CalcT( year_, month_, day_, hour, tz_offset );
  }
  private double CalcT( int year, int month, int day, int hour, int tz_offset )
  {
    //時刻変数(T)を計算
    //  J2000.0(2000年1月1日力学時正午)からの経過日数をKとしたとき
    //  T = K / 365.25
    //    365.25 => ユリウス年
    int jd_year  = 0;
    int jd_month = 0;

    double k        = 0;
    double k_dash   = 0;
    double adjust_k = 0;
    double adjust_t = 0;

    if( (month == (Calendar.JANUARY  + 1)) ||
        (month == (Calendar.FEBRUARY + 1))    )
    {
      //1月=>13月、2月=>14月
      jd_year  = (year - 2000) - 1;
      jd_month =  month + 12;
    }
    else
    {
      //変更なし
      jd_year  = (year - 2000);
      jd_month =  month;
    }

    k_dash =   ( 365 * jd_year  )
             + (  30 * jd_month )
             + day
             - 33.5
             - ( tz_offset / (double)24 )
             + Math.floor( 3 * (jd_month + 1) / (double)5 )
             + Math.floor( jd_year / (double)4 );

    adjust_k = hour             / (double)24;
    adjust_t = CalcDeltaT(year) / (double)86400;

    k = k_dash + adjust_k + adjust_t;

    return (k / 365.25);
  }
  // ----------------------------------------------------------
  public int  GetYear () { return year_; }
  public void SetYear ( int year  )
  {
    year_   = year;
    cal_.set( Calendar.YEAR, year_ );
  }
  public int  GetMonth() { return month_; }
  public void SetMonth( int month )
  {
    month_  = month;
    cal_.set( Calendar.MONTH, month - 1 );  //(0 to 11)
  }

  public int  GetMonthOfYear() { return month_ - 1; }
  public void SetMonthOfYear( int month_of_year )
  {
    month_ = month_of_year + 1;
    cal_.set( Calendar.MONTH, month_of_year );
  }

  public int  GetDay() { return day_; }
  public void SetDay( int day )
  {
    day_    = day;
    cal_.set( Calendar.DAY_OF_MONTH, day );
  }

  public int  GetHour() { return hour_; }
  public void SetHour( int hour )
  {
    hour_   = hour;
    cal_.set( Calendar.HOUR_OF_DAY, hour );
  }
  public int  GetMinute() { return minute_; }
  public void SetMinute( int minute )
  {
    minute_ = minute;
    cal_.set( Calendar.MINUTE, minute );
  }
  public int  GetSecond() { return second_; }
  public void SetSecond( int second )
  {
    second_ = second;
    cal_.set( Calendar.SECOND, second );
  }

  public int GetDateSerial()  { return   (year_ * 10000) + (month_ * 100) +  day_; }
  public int GetTimeMinute()  { return   (hour_ * 60)    +  minute_; }
  
  public double GetElapsedTime()
  {
    return   (double)( ( hour_ * 60 ) + minute_ )
           / (double)(   24    * 60 );
  }
}
