package com.alt_r.www.multi.moontracker;

public class Astronomy
{
private
  int      date_;

  Location locRed_;
  Location locYellow_;

  public  final static int DAYS_OF_CALENDAR  = 3;  //暦を保持する日数
  public  final static int TIMES_OF_RISE_SET = 3;  //出、南中、入の3つの状態

  //暦を保持する配列
  //当日と、その前後1日ずつで3日の暦が必要
  Location[] locCalendar_ = new Location[DAYS_OF_CALENDAR];

  public  final int kCAL_YESTERDAY    = 0;  //前日
  public  final int kCAL_TODAY        = 1;  //当日
  public  final int kCAL_TOMORROW     = 2;  //翌日
  
  double st_;      //恒星時
  double k_;       //出没高度 = -大気差 + 視差
  double t_;       //時角
  double S_;       //視半径
  double E_;       //地平線の伏角
  double r_;       //距離
  double gst_;     //グリニジ視恒星時

  //現在地の方位角、高度角
  CoordinateHori   codHere_;
  //出、南中、入の方位角、時刻
  CoordinateHori[] colRiseSet_ = new CoordinateHori[TIMES_OF_RISE_SET];  

  public final int TIME_RISE      = 0;  //出
  public final int TIME_MERIDIAN  = 1;  //南中
  public final int TIME_SET       = 2;  //入

  double angle_per_cycle_ = 0;

  private final double R          = 0.585556;  //大気差
  private final double DELTA_D_TH = 0.000005;

  private final int  GEOMETRY_AZIMUTH = 0;  //方位角
  private final int  GEOMETRY_HEIGHT  = 1;  //高度角

  Astronomy()
  {
    date_ = 0;

    t_    = 0;

    S_    = 0;
    r_    = 0;

    st_   = 0;
    k_    = 0;
    
    locRed_    = new Location();
    locYellow_ = new Location();

    for( int day_cnt = 0; day_cnt < DAYS_OF_CALENDAR; day_cnt++ )
    {
      locCalendar_[day_cnt] = new Location();
    }

    codHere_        = new CoordinateHori();
    for( int time_cnt = 0; time_cnt < 3; time_cnt++ )
    {
      colRiseSet_[time_cnt] = new CoordinateHori();
    }
  }

  protected void init( double cycle )
  {
    angle_per_cycle_ = cycle;
  }
  
  public void SetCalendar( int      index,
                             Location locCalendar )
  {
    SetCalendar( index,
                 Math.toDegrees( locCalendar.GetLatitude()  ),
                 Math.toDegrees( locCalendar.GetLongitude() ),
                 locCalendar.GetParallax() );
  }
  public void SetCalendar( int    index,
                            double latitude,
                            double longitude,
                            double parallax )
  {
    //経度 : [0] < [1] < [2]になるように補正する
    //index = 0の時、index - 1は存在しないので
    //index > 0の時のみ行う
    if( ( index > 0 ) &&
        ( longitude < locCalendar_[index - 1].GetLongitude() ) )
    {
      longitude += 360.0;
    }
    
    locCalendar_[index].SetLocation( latitude, longitude, parallax );
  }

  //==============================================================
  public void Update( double d, Location locHere )
  {
    codHere_.SetD( d );

    CalcLocation    ( d );                 //経度、緯度、視差を計算
    CalcSiderealTime( d, locHere, gst_ );  //恒星時を計算

    t_ = CalcTimeAngle( st_, locRed_.GetLongitude() );
    CalcGeometry(  locHere );
  }
  public void Update( int hour, int minute, Location locHere )
  {
    double d;
    
    d = ((hour * 60) + minute) / (double)(24 * 60);

    Update( d, locHere );
  }
  //==============================================================
  private double Calc2ndinterpolation( double d,
                                         double x1, double x2, double x3 )
  {
    //2次補間
    double a = 0;
    double b = 0;
    double c = 0;

    a = (        x1   - (   2 * x2 ) +        x3  ) / (double)2;
    b = ( ( -7 * x1 ) + (   6 * x2 ) +        x3  ) / (double)8;
    c = ( ( 33 * x1 ) + ( 110 * x2 ) - ( 15 * x3) ) / (double)128;

    return ( a * Math.pow( d, 2 ) ) + ( b * d ) + c;
  }

  private void CalcLocation( double d )
  {
    //赤経、赤緯の位置計算
    double ret[] = new double[4];

    CalcLocation( d, ret );

    locRed_.SetLocation( ret[Location.LATITUDE ],
                         ret[Location.LONGITUDE],
                         ret[Location.PARALLAX ] );

    k_ = -S_ -E_ -R + locRed_.GetParallax();  //出没高度
  }
  private void CalcLocation( double d, double ret[] )
  {
    //位置計算
 
    //緯度
    ret[Location.LATITUDE]
      = Calc2ndinterpolation(
          d,
          locCalendar_[kCAL_YESTERDAY].GetLatitude(),
          locCalendar_[kCAL_TODAY    ].GetLatitude(),
          locCalendar_[kCAL_TOMORROW ].GetLatitude() );

    //経度
    ret[Location.LONGITUDE]
      = Calc2ndinterpolation(
          d,
          locCalendar_[kCAL_YESTERDAY].GetLongitude(),
          locCalendar_[kCAL_TODAY    ].GetLongitude(),
          locCalendar_[kCAL_TOMORROW ].GetLongitude());

    //視差
    ret[Location.PARALLAX]
      = Calc2ndinterpolation(
          d,
          locCalendar_[kCAL_YESTERDAY].GetParallax(),
          locCalendar_[kCAL_TODAY    ].GetParallax(),
          locCalendar_[kCAL_TOMORROW ].GetParallax() );

    //出没高度
    ret[3]
      = -R + ret[Location.PARALLAX];
  }

  private void CalcSiderealTime( double d, Location locHere, double gst )
  {
    //恒星時
    double ret[] = new double[1];

    CalcSiderealTime( d, locHere, gst, ret );

    st_ = ret[0];
  }
  private void CalcSiderealTime( double d, Location locHere, double gst, double ret[] )
  {
    //恒星時
    //θ = θg + λ + d x 360.9856474
    //  θg => gst              : グリニッジ視恒星時
    //  λ  => loc.GetLongitude : 東経
    //  d   => d                : 世界時0時(日本時9時)からの経過時間
    //                            (gmt / 24) = ((jst - 9) / 24) : マイナスでも良い)
    //
    double st    = 0;
    double d_gmt = d - ( 9 / (double)24 );

    st = gst + locHere.GetLongitude() + ( d_gmt * 360.9856474);

    //恒星時がマイナスになったら、360°足す(360を足して360の余りでも良い)
    ret[0] = AdjustDegree( st );
  }

  public double CalcTimeAngle( Location locHere, double k, double latitude )
  {
    //出没高度kに対応する時角(tk)
    //          sin(k) - sin(δ)sin(φ)
    //cos(tk) = -----------------------
    //            cos(δ)cos(φ)
    //  k => k      : 出没高度
    //  δ => delta : 赤緯
    //  φ => phi   : 観測地点の東経
    double sin_k     = Math.sin(Math.toRadians(k));

    double sin_delta = Math.sin(Math.toRadians(latitude));
    double sin_phi   = Math.sin(Math.toRadians(locHere.GetLatitude()));

    double cos_delta = Math.cos(Math.toRadians(latitude));
    double cos_phi   = Math.cos(Math.toRadians(locHere.GetLatitude()));
    double cos_tk    = 0;

    double tk        = 0;
    
    cos_tk = ( ( sin_k - ( sin_delta * sin_phi ) ) /
                         ( cos_delta * cos_phi )
             );
    tk = Math.acos( cos_tk );
    
    return Math.toDegrees( tk );
  }
  public double CalcTimeAngle( double st, double longitude )
  {
    //恒星時に対応する時角(t)
    return st - longitude;
  }

  public void CalcGeometry( Location locHere )
  {
    double ret[] = new double[2];
    
    CalcGeometry( locHere, t_, locRed_.GetLatitude(), ret );
    codHere_.SetGeometry( ret[GEOMETRY_AZIMUTH],
                          ret[GEOMETRY_HEIGHT]  );
  }
  public void CalcGeometry( Location locHere, double t, double latitude,
                             double ret[] )
  {
    double sin_phi   = Math.sin(Math.toRadians( locHere.GetLatitude() ));
    double sin_delta = Math.sin(Math.toRadians( latitude ));
    double sin_t     = Math.sin(Math.toRadians( t ));

    double cos_phi   = Math.cos(Math.toRadians( locHere.GetLatitude() ));
    double cos_delta = Math.cos(Math.toRadians( latitude ));
    double cos_t     = Math.cos(Math.toRadians( t ));

    double formula_mother = 0;  //方位角の式の分母、および象限補正に使用

    formula_mother = (cos_phi * sin_delta) - (sin_phi * cos_delta * cos_t);

    //方位角
    ret[GEOMETRY_AZIMUTH]
      = Math.atan( (-cos_delta * sin_t ) / formula_mother );

    //高度角
    ret[GEOMETRY_HEIGHT]
      = Math.asin( (sin_phi * sin_delta) + (cos_phi * cos_delta * cos_t) );

    // ---- 象限補正 ----
    if( formula_mother > 0 )
    {
      //補正なし
    }
    else if( formula_mother < 0 )
    {
      //方位角の式の分母が負
      ret[GEOMETRY_AZIMUTH] += Math.toRadians(180);  //180°を加算
    }
    else  //方位角の式の分母が0
    {
      //-> sin(tk)により、補正値を判別
      if( sin_t > 0 )
      {
        ret[GEOMETRY_AZIMUTH] = Math.toRadians(-90);  //-90°(固定値)
      }
      else if( sin_t < 0 )
      {
        ret[GEOMETRY_AZIMUTH] = Math.toRadians(90);   //90° (固定値)
      }
      else  //sin_tk == 0
      {
        ret[GEOMETRY_AZIMUTH] = Math.toRadians(0);    //不定 (0にしておく)
        ret[GEOMETRY_HEIGHT]  = Math.toRadians(90);   //90° (固定値)
      }
    }

    //方位角 : 負の角度を360°表記に変換
    ret[GEOMETRY_AZIMUTH] = AdjustRadian( ret[GEOMETRY_AZIMUTH] );
  }
  
  public void CalcRiseSet( int index_time, Location locHere )
  {
    //逐次近似法で、出の時刻または入りの時刻を計算
    //無限ループ回避のため、最大ループ回数を設定する
    int cnt     = 0;
    int cnt_end = 10;  //最大ループ回数

    double delta_d   = 0;
    double delta_t   = 0;

    double t         = 0;      //debug
    double tk        = 0;      //debug

    double ret_loc[] = new double[4];
    double ret_geo[] = new double[2];

    //ループ中のdelta_dの最小値と、その時のt,dの値を保持
    double min_delta_d = 9999;
    double min_t       = 9999;
    double min_d       = 9999;

    //初期値
    colRiseSet_[index_time].SetD( 0.5 );

    for( cnt = 0; cnt < cnt_end; cnt++ )
    {
      //経度、緯度、視差を計算
      CalcLocation(     colRiseSet_[index_time].GetD(),          ret_loc );
      CalcSiderealTime( colRiseSet_[index_time].GetD(), locHere, gst_ );

      if( index_time == TIME_MERIDIAN )
      {
        tk = 0;  //南中
      }
      else
      {
        tk = CalcTimeAngle( locHere, ret_loc[3], ret_loc[Location.LATITUDE] );
        if( index_time == TIME_RISE )
        {
          tk *= -1;
        }
      }

      t  = CalcTimeAngle( st_, ret_loc[Location.LONGITUDE] );
      delta_t = tk - t;

      //tk - tの絶対値が180を超えないように補正
      if( delta_t > 180 )
      {
        delta_t = ( delta_t - 360 ) % 360;
      }
      else if( delta_t < -180 )
      {
        delta_t = ( delta_t + 360 ) % 360;
      }

      delta_d = (delta_t / (double)angle_per_cycle_) % 1;
      colRiseSet_[index_time].AddD( delta_d );

      //ループ中のdelta_dの最小値と、その時のdを保持
      if( Math.abs(min_delta_d) > Math.abs(delta_d) )
      {
        min_delta_d = delta_d;
        min_t       = t;
        min_d       = colRiseSet_[index_time].GetD();
      }
      
      //切り上げ条件
      //delta_d がしきい値以下になったら計算精度に影響がないので
      //ループの途中で切り上げる
      if( Math.abs( min_delta_d ) < DELTA_D_TH )
      {
        break;
      }
    }

    //ループ中の最小値を採用し、セットする
    colRiseSet_[index_time].SetD( min_d );

    //方位角、高度角の計算
    CalcGeometry( locHere, min_t, ret_loc[Location.LATITUDE], ret_geo );
    colRiseSet_[index_time].SetGeometry( ret_geo[GEOMETRY_AZIMUTH],
                                          ret_geo[GEOMETRY_HEIGHT] );
  }

//  protected BigDecimal CalcItem( String unscaled_value, int scale )
//  {
//    BigDecimal unscaled_value_ = new BigDecimal( unscaled_value.trim() );
//    BigDecimal scaled_value_   = unscaled_value_.setScale( scale, BigDecimal.ROUND_HALF_UP );
//
//    return scaled_value_;
//  }
//  
//  protected BigDecimal CalcItem( String a, String b, String c, double T, int scale )
//  {
//    BigDecimal a_ = new BigDecimal( a.trim() );
//    BigDecimal b_ = new BigDecimal( b.trim() );
//    BigDecimal c_ = new BigDecimal( c.trim() );
//    BigDecimal T_ = new BigDecimal( Double.toString( T ) );
//
//    BigDecimal b_cT_ = new BigDecimal( "0.0" );
//
//    double sin_b_cT_  = 0;
//    double asin_b_cT_ = 0;
//
//    b_cT_ = b_.add( c_.multiply( T_ ) );      //b + cT
//    BigDecimal b_cT_scaled_ = b_cT_.setScale( 3, BigDecimal.ROUND_HALF_UP );
//
//    sin_b_cT_  = Math.sin( Math.toRadians( b_cT_scaled_.doubleValue() ) );
//    BigDecimal sin_b_cT_scaled_  = new BigDecimal( Double.toString( sin_b_cT_ ) ).setScale( scale + 1, BigDecimal.ROUND_FLOOR );
//
//    asin_b_cT_ = a_.doubleValue() * sin_b_cT_scaled_.doubleValue();
//    BigDecimal asin_b_cT_scaled_ = new BigDecimal( Double.toString( asin_b_cT_) ).setScale( scale,     BigDecimal.ROUND_HALF_UP );
//
//    return asin_b_cT_scaled_;
//  }
  protected double round( double unrounded_value, int round_digit )
  {
    //四捨五入して、指定した桁数に丸める
    double ans = 0.0;

    double multi = Math.pow( 10.0, (double)round_digit );
    ans = Math.round( unrounded_value * multi ) / multi;
    
    return ans;
  }
  
  // Overrideするメソッド ============================
  public double CalcLongitudeYellow( double T )
  {
    //時刻定数(T)による黄経(λ)の近似式
    //継承したクラス内で計算
    return 0;
  }
  public double CalcLatitudeYellow( double T )
  {
    //時刻定数(T)による黄緯(β)の近似式
    //継承したクラス内で計算
    return 0;
  }
  public double CalcParallax( double T )
  {
    //時刻定数(T)による視差の近似式
    //継承したクラス内で計算
    return 0;
  }
  public double CalcDistance( double T )
  {
    //時間定数(T)による距離の近似式
    //継承したクラス内で計算
    return 0;
  }

  public void toRed( Location loc_yellow, double T, Location loc_red )
  {
    //黄経、黄緯から赤経、赤緯度に変換
    //継承したクラス内で計算
  }
  public void CalcLocation( double T, Location locRed )
  {
    //時刻定数(T)による黄経、黄緯の近似式から
    //赤経、赤緯を計算する
    //継承したクラス内で計算
  }
  // Overrideするメソッド ============================

  public void CalcGST( int hour, double jd )
  {
    //グリニッジ恒星時を近似式で計算
    double tjd = jd - 2440000.5;

    gst_ = ( 24 * ( 0.671262 + 1.0027379094 * tjd ) * 15 ) % 360;
  }
  
  protected static double AdjustDegree( double degree )
  {
    return ( degree + 360 ) % 360;
  }
  protected double AdjustRadian( double radian )
  {
    return ( radian + Math.toRadians(360) ) % Math.toRadians(360);
  }
  //==============================================================
  public void SetDateSerial( int date)
  {
    date_ = date;
  }
  public int GetDateSerial()
  {
    return date_;
  }
  public void SetGST( double gst )
  {
    gst_ = gst;
  }
  public double GetGST()
  {
    return gst_;
  }
  public double GetST()
  {
    return st_;
  }

  public double GetLatitude()
  {
    return locRed_.GetLatitude();
  }
  public double GetLongitude()
  {
    return locRed_.GetLongitude();
  }
  public double GetParallax()
  {
    return locRed_.GetParallax();
  }

  public double GetLongitudeYellow()
  {
    return locYellow_.GetLongitude();
  }
  
  public double GetAltitude()
  {
    return k_;
  }
  public double GetAzimuth()
  {
    //方位角(°に変換）
    return Math.toDegrees( codHere_.GetAzimuth() );
  }
  public double GetHeight()
  {
    //高度角(°に変換）
    return Math.toDegrees( codHere_.GetHeight() );
  }
  public double GetTimeAngle()
  {
    return t_;
  }

  //出没関連
  public double GetRiseSetTime( int index_time )
  {
    //経過時間
    return colRiseSet_[index_time].GetD();
  }
  public double GetRiseSetAzimuth( int index_time )
  {
    //方位角(°に変換）
    return Math.toDegrees( colRiseSet_[index_time].GetAzimuth() );
  }
  public double GetRiseSetHeight ( int index_time )
  {
    //高度角(°に変換）
    return Math.toDegrees( colRiseSet_[index_time].GetHeight() );
  }
  
  public double GetDistance()
  {
    return r_;
  }

  public double CalcLongitudeYellowExactly(double T)
  {
    // TODO Auto-generated method stub
    return 0;
  }
}
