package com.alt_r.www.multi.moontracker;

public class Location
{
 private
  double Latitude_;
  double Longitude_;
  double Parallax_;

  private static final int    kMINUTE_PER_HOUR = 60;
  private static final double kDEGREE_PER_HOUR = 15.0;

  public  static final int    LATITUDE  = 0;
  public  static final int    LONGITUDE = 1;
  public  static final int    PARALLAX  = 2;

  public Location()
  {
    Latitude_  = 0;
    Longitude_ = 0;
    Parallax_  = 0;
  }

  public static String toLocationFormat( double locate )
  {
    return String.format( "%8.4f", locate);
  }
  // --------------------------------------
  public void SetLocation( double latitude, double longitude, double parallax )
  {
    SetLatitude ( latitude  );
    SetLongitude( longitude );
    SetParallax ( parallax  );
  }
  public void SetLocation( double latitude, double longitude )
  {
    SetLatitude ( latitude  );
    SetLongitude( longitude );
  }
  // --------------------------------------
  public double toHour(double value)
  {
    return value / kDEGREE_PER_HOUR;
  }
  
  public double toMinute(double value)
  {
    return value / kDEGREE_PER_HOUR * kMINUTE_PER_HOUR;
  }
  // --------------------------------------
  public void SetLatitude(double value)
  {
    Latitude_  = value;
  }
  public double GetLatitude()
  {
    return Latitude_;
  }

  public void SetLongitude(double value)
  {
    Longitude_ = value;
  }
  public double GetLongitude()
  {
    return Longitude_;
  }

  public void SetParallax(double value)
  {
    Parallax_ = value;
  }
  public double GetParallax()
  {
    return Parallax_;
  }

  //インテント用マップURLを返す
  public String GetMapUrl(){ return GetMapUrl( false ); }
  public String GetMapUrl( boolean flag_show_pin )
  {
    //現在地のURL
    String location_url
      = toLocationFormat( GetLatitude()  ) + "," +
        toLocationFormat( GetLongitude() );
    String map_url;

    //地図のURLを作成
    if ( flag_show_pin == true )
    {
      //現在地 + ピン表示
      map_url = "geo:0,0?q=" + location_url;
    }
    else
    {
      //現在地
      map_url = "geo:" + location_url;
    }
    
    //地図のURLを返す
    return map_url;
  }
}
