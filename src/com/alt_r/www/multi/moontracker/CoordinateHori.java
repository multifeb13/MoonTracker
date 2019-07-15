package com.alt_r.www.multi.moontracker;

//’n•½À•WŒn
public class CoordinateHori
{
  private double d_;        //Œo‰ßŠÔ(24ŠÔ = 1.0)
  private double Azimuth_;  //•ûˆÊŠp
  private double Height_;   //‚“xŠp

  static String[] AzimuthString
    = {"–k", "–k–k“Œ", "–k“Œ", "“Œ–k“Œ",
       "“Œ", "“Œ“ì“Œ", "“ì“Œ", "“ì“ì“Œ",
       "“ì", "“ì“ì¼", "“ì¼", "¼“ì¼",
       "¼", "¼–k¼", "–k¼", "–k–k¼"};

  //-------------------------------------------
  public double GetD()
  {
    return d_;
  }
  public void SetD( double d )
  {
    d_ = d;
  }
  public void AddD( double delta_d )
  {
    d_ += delta_d;
  }

  public double GetAzimuth()
  {
    return Azimuth_;
  }
  public void SetAzimuth( double azimuth )
  {
    Azimuth_ = azimuth;
  }

  public double GetHeight()
  {
    return Height_;
  }
  public void SetHeight( double height )
  {
    Height_ = height;
  }

  public void SetGeometry( double azimuth, double height )
  {
    SetAzimuth( azimuth );
    SetHeight ( height  );
  }

  public static String toAzimuthName( double azimuth )
  {
    return toAzimuthName( azimuth, 8 );
  }
  public static String toAzimuthName( double azimuth, int div )
  {
    //•ûˆÊŠp‚Ì–¼‘Oi•ûˆÊj‚ğ•Ô‚·
    int    index = 16  / div;
    double step  = 360 / div;

    double azimuth_min = 0;
    double azimuth_max = 0;
    
    for( int cnt = 1; cnt < div; cnt++ )
    {
      azimuth_min = cnt * step - ( step / 2 );
      azimuth_max = cnt * step + ( step / 2 );
      
      if( ( azimuth_min <= azimuth     ) && 
          ( azimuth     <  azimuth_max )    )
      {
        return AzimuthString[cnt * index];
      }
    }
    return AzimuthString[0];
  }
  
  public static String toAngleFormat( double angle )
  {
    //Šp“x‚ğ³‹K‰»‚µ‚½•¶š—ñ‚ğ•Ô‚·
    return String.format( "%06.2f", angle );
  }
}
