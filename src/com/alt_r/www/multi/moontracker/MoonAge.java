package com.alt_r.www.multi.moontracker;

//月齢計算

public class MoonAge
{
  Moon moon;
  Sun  sun;

  Chronus chAgeFinal;
  Chronus chAgeCurrent;

  final double kAge_Adjust = -0.5;

  double moon_age_;
  double latest_moon_age_;
  
  MoonAge()
  {
    moon = new Moon();
    sun  = new Sun();

    chAgeFinal   = new Chronus();
    chAgeCurrent = new Chronus();

    chAgeFinal.  UpdateTime();
    chAgeCurrent.UpdateTime();
  }

  // ==================================================================

  private double round( double value, int digit )
  {
    //指定桁数で四捨五入
    double value_to_int = Math.pow( 10.0, (double)digit );
    return Math.round( value * value_to_int ) / value_to_int;
  }

  // ==================================================================

  public void Update()
  {
    chAgeFinal.  UpdateTime();
    chAgeCurrent.UpdateTime();

    Update( chAgeCurrent.GetYear(), chAgeCurrent.GetMonth(), chAgeCurrent.GetDay() );
  }
  
  public void Update( int year, int month, int day )
  {
    //逐次近似法で、月齢を計算（当日の午前0時基準）

    //無限ループ回避のため、最大ループ回数を設定する
    int cnt     = 0;
    int cnt_end = 10;  //最大ループ回数

    float T = 0;

    float longitude_moon = 0;
    float longitude_sun  = 0;
  
    float delta_longitude = 0;

    double g  = 0;  //月齢
    float  gn = 0;  //第n近似の月齢

    //ループ中のdelta_longitudeの最小値
    float min_delta_longitude = 9999;

//    chAgeFinal.  UpdateTime();
//    chAgeCurrent.UpdateTime();
//
////    //debug
////    chAgeFinal.  SetTime  (1999, 10, 27, 12, 0, 0);
////    chAgeCurrent.SetTime(1999, 10, 27, 12, 0, 0);
//    //当日の午前0時基準
//    chAgeFinal.  SetTime( 0, 0, 0 );
//    chAgeCurrent.SetTime( 0, 0, 0 );
    chAgeFinal  .SetTime(year, month, day, 12, 0, 0);
    chAgeCurrent.SetTime(year, month, day, 12, 0, 0);
    
    T = (float) chAgeCurrent.CalcT();
  
    for( cnt = 0; cnt < cnt_end; cnt++ )
    {
      longitude_moon = (float) Astronomy.AdjustDegree( moon.CalcLongitudeYellow( T ) );
      longitude_sun  = (float) Astronomy.AdjustDegree(  sun.CalcLongitudeYellow( T ) );
//      longitude_moon = (float) Astronomy.AdjustDegree( moon.CalcLongitudeYellowExactly( T ) );
//      longitude_sun  = (float) Astronomy.AdjustDegree(  sun.CalcLongitudeYellowExactly( T ) );

      delta_longitude = (float) Astronomy.AdjustDegree( longitude_moon - longitude_sun );

      //ループ中のdelta_longitudeの最小値を更新した時だけ、gn(第n近似の月齢)とTを更新
      if( min_delta_longitude > delta_longitude )
      {
        min_delta_longitude = delta_longitude;

        gn = (float) (delta_longitude / 12.1908);

        chAgeCurrent.AddDate( gn * -1 );
        T = (float) chAgeCurrent.CalcT();
        
      }
      //終了条件
      if( min_delta_longitude < 0.05 )
      {
        break;
      }
    }

    chAgeFinal.AddDay   ( chAgeCurrent.GetDay()    * -1 );
    chAgeFinal.AddHour  ( chAgeCurrent.GetHour()   * -1 );
    chAgeFinal.AddMinute( chAgeCurrent.GetMinute() * -1 );

    g = chAgeFinal.GetDay() + chAgeFinal.GetElapsedTime();
    g += kAge_Adjust;

    //月齢を取得
    moon_age_ = round( g % 30.0, 1 );
  }

  //月齢のインデックスを返す
  public int GetIndex( double age )
  {
    double   kRANGE_OF_DAY = 0.5;

    for( int index = 0; index < 30; index++)
    {
      if( ( ( index - kRANGE_OF_DAY ) <= age ) &&
          ( ( index + kRANGE_OF_DAY ) >  age )    )
      {
        return index;
      }
    }

    return 0;
  }

  private String toName( double moon_age )
  {
//    String[] name_of_day =
//      { "新月", "三日月", "上弦の月", "十日夜", "十三夜月", "十四日月", "満月", "十六夜月", "立待月", "居待月", "寝待月", "更待月", "下弦の月", "有明月" };
//    double[] delimit_of_day =
//      { 0,      2,       6,        9,        12,        13,        14,     15,        16,      17,      18,      19,      22,        25};
    String[] name_of_day =
      { "新月",
        "",
        "三日月",
        "",
        "",
        "",
        "上弦の月",
        "",
        "",
        "十日夜",
        "",
        "",
        "十三夜月",
        "十四日月",
        "満月",
        "十六夜月",
        "立待月",
        "居待月",
        "寝待月",
        "更待月",
        "",
        "",
        "下弦の月",
        "",
        "",
        "有明月",
        "",
        "",
        "",
        "",
        "新月"
      };

    return name_of_day[GetIndex( moon_age ) ];
  }

  // ==================================================================

  public double GetAge( double d )
  {
    latest_moon_age_ = moon_age_ + d;
    return latest_moon_age_;
  }
  public double GetAge()
  {
    return GetAge( 0 );
  }
  public double GetLatestAge()
  {
    return latest_moon_age_;
  }

  public String GetName( double age )
  {
    return toName( age );
  }
  public String GetName()
  {
    return GetName( moon_age_ );
  }
  
  public String GetAgeName( double age )
  {
    String strAgeName = "";
    String strName    = "";

    //月齢の数値と名称
    strAgeName = String.format( "%4.1f", age );

    //月齢の名称
    strName    = GetName( age );
    //月齢の名称がある時のみ、名称を付加
    if( strName.equals("") == false )
    {
      strAgeName = strAgeName +
                   String.format( "(%s)", strName );
    }
    
    return strAgeName;
  }
}
