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

  //�C���e���g�p�}�b�vURL��Ԃ�
  public String GetMapUrl(){ return GetMapUrl( false ); }
  public String GetMapUrl( boolean flag_show_pin )
  {
    //���ݒn��URL
    String location_url
      = toLocationFormat( GetLatitude()  ) + "," +
        toLocationFormat( GetLongitude() );
    String map_url;

    //�n�}��URL���쐬
    if ( flag_show_pin == true )
    {
      //���ݒn + �s���\��
      map_url = "geo:0,0?q=" + location_url;
    }
    else
    {
      //���ݒn
      map_url = "geo:" + location_url;
    }
    
    //�n�}��URL��Ԃ�
    return map_url;
  }
}
