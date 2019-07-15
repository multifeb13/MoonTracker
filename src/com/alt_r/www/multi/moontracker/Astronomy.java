package com.alt_r.www.multi.moontracker;

public class Astronomy
{
private
  int      date_;

  Location locRed_;
  Location locYellow_;

  public  final static int DAYS_OF_CALENDAR  = 3;  //���ێ��������
  public  final static int TIMES_OF_RISE_SET = 3;  //�o�A�쒆�A����3�̏��

  //���ێ�����z��
  //�����ƁA���̑O��1������3���̗�K�v
  Location[] locCalendar_ = new Location[DAYS_OF_CALENDAR];

  public  final int kCAL_YESTERDAY    = 0;  //�O��
  public  final int kCAL_TODAY        = 1;  //����
  public  final int kCAL_TOMORROW     = 2;  //����
  
  double st_;      //�P����
  double k_;       //�o�v���x = -��C�� + ����
  double t_;       //���p
  double S_;       //�����a
  double E_;       //�n�����̕��p
  double r_;       //����
  double gst_;     //�O���j�W���P����

  //���ݒn�̕��ʊp�A���x�p
  CoordinateHori   codHere_;
  //�o�A�쒆�A���̕��ʊp�A����
  CoordinateHori[] colRiseSet_ = new CoordinateHori[TIMES_OF_RISE_SET];  

  public final int TIME_RISE      = 0;  //�o
  public final int TIME_MERIDIAN  = 1;  //�쒆
  public final int TIME_SET       = 2;  //��

  double angle_per_cycle_ = 0;

  private final double R          = 0.585556;  //��C��
  private final double DELTA_D_TH = 0.000005;

  private final int  GEOMETRY_AZIMUTH = 0;  //���ʊp
  private final int  GEOMETRY_HEIGHT  = 1;  //���x�p

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
    //�o�x : [0] < [1] < [2]�ɂȂ�悤�ɕ␳����
    //index = 0�̎��Aindex - 1�͑��݂��Ȃ��̂�
    //index > 0�̎��̂ݍs��
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

    CalcLocation    ( d );                 //�o�x�A�ܓx�A�������v�Z
    CalcSiderealTime( d, locHere, gst_ );  //�P�������v�Z

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
    //2�����
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
    //�Ԍo�A�Ԉ܂̈ʒu�v�Z
    double ret[] = new double[4];

    CalcLocation( d, ret );

    locRed_.SetLocation( ret[Location.LATITUDE ],
                         ret[Location.LONGITUDE],
                         ret[Location.PARALLAX ] );

    k_ = -S_ -E_ -R + locRed_.GetParallax();  //�o�v���x
  }
  private void CalcLocation( double d, double ret[] )
  {
    //�ʒu�v�Z
 
    //�ܓx
    ret[Location.LATITUDE]
      = Calc2ndinterpolation(
          d,
          locCalendar_[kCAL_YESTERDAY].GetLatitude(),
          locCalendar_[kCAL_TODAY    ].GetLatitude(),
          locCalendar_[kCAL_TOMORROW ].GetLatitude() );

    //�o�x
    ret[Location.LONGITUDE]
      = Calc2ndinterpolation(
          d,
          locCalendar_[kCAL_YESTERDAY].GetLongitude(),
          locCalendar_[kCAL_TODAY    ].GetLongitude(),
          locCalendar_[kCAL_TOMORROW ].GetLongitude());

    //����
    ret[Location.PARALLAX]
      = Calc2ndinterpolation(
          d,
          locCalendar_[kCAL_YESTERDAY].GetParallax(),
          locCalendar_[kCAL_TODAY    ].GetParallax(),
          locCalendar_[kCAL_TOMORROW ].GetParallax() );

    //�o�v���x
    ret[3]
      = -R + ret[Location.PARALLAX];
  }

  private void CalcSiderealTime( double d, Location locHere, double gst )
  {
    //�P����
    double ret[] = new double[1];

    CalcSiderealTime( d, locHere, gst, ret );

    st_ = ret[0];
  }
  private void CalcSiderealTime( double d, Location locHere, double gst, double ret[] )
  {
    //�P����
    //�� = ��g + �� + d x 360.9856474
    //  ��g => gst              : �O���j�b�W���P����
    //  ��  => loc.GetLongitude : ���o
    //  d   => d                : ���E��0��(���{��9��)����̌o�ߎ���
    //                            (gmt / 24) = ((jst - 9) / 24) : �}�C�i�X�ł��ǂ�)
    //
    double st    = 0;
    double d_gmt = d - ( 9 / (double)24 );

    st = gst + locHere.GetLongitude() + ( d_gmt * 360.9856474);

    //�P�������}�C�i�X�ɂȂ�����A360������(360�𑫂���360�̗]��ł��ǂ�)
    ret[0] = AdjustDegree( st );
  }

  public double CalcTimeAngle( Location locHere, double k, double latitude )
  {
    //�o�v���xk�ɑΉ����鎞�p(tk)
    //          sin(k) - sin(��)sin(��)
    //cos(tk) = -----------------------
    //            cos(��)cos(��)
    //  k => k      : �o�v���x
    //  �� => delta : �Ԉ�
    //  �� => phi   : �ϑ��n�_�̓��o
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
    //�P�����ɑΉ����鎞�p(t)
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

    double formula_mother = 0;  //���ʊp�̎��̕���A����яی��␳�Ɏg�p

    formula_mother = (cos_phi * sin_delta) - (sin_phi * cos_delta * cos_t);

    //���ʊp
    ret[GEOMETRY_AZIMUTH]
      = Math.atan( (-cos_delta * sin_t ) / formula_mother );

    //���x�p
    ret[GEOMETRY_HEIGHT]
      = Math.asin( (sin_phi * sin_delta) + (cos_phi * cos_delta * cos_t) );

    // ---- �ی��␳ ----
    if( formula_mother > 0 )
    {
      //�␳�Ȃ�
    }
    else if( formula_mother < 0 )
    {
      //���ʊp�̎��̕��ꂪ��
      ret[GEOMETRY_AZIMUTH] += Math.toRadians(180);  //180�������Z
    }
    else  //���ʊp�̎��̕��ꂪ0
    {
      //-> sin(tk)�ɂ��A�␳�l�𔻕�
      if( sin_t > 0 )
      {
        ret[GEOMETRY_AZIMUTH] = Math.toRadians(-90);  //-90��(�Œ�l)
      }
      else if( sin_t < 0 )
      {
        ret[GEOMETRY_AZIMUTH] = Math.toRadians(90);   //90�� (�Œ�l)
      }
      else  //sin_tk == 0
      {
        ret[GEOMETRY_AZIMUTH] = Math.toRadians(0);    //�s�� (0�ɂ��Ă���)
        ret[GEOMETRY_HEIGHT]  = Math.toRadians(90);   //90�� (�Œ�l)
      }
    }

    //���ʊp : ���̊p�x��360���\�L�ɕϊ�
    ret[GEOMETRY_AZIMUTH] = AdjustRadian( ret[GEOMETRY_AZIMUTH] );
  }
  
  public void CalcRiseSet( int index_time, Location locHere )
  {
    //�����ߎ��@�ŁA�o�̎����܂��͓���̎������v�Z
    //�������[�v����̂��߁A�ő僋�[�v�񐔂�ݒ肷��
    int cnt     = 0;
    int cnt_end = 10;  //�ő僋�[�v��

    double delta_d   = 0;
    double delta_t   = 0;

    double t         = 0;      //debug
    double tk        = 0;      //debug

    double ret_loc[] = new double[4];
    double ret_geo[] = new double[2];

    //���[�v����delta_d�̍ŏ��l�ƁA���̎���t,d�̒l��ێ�
    double min_delta_d = 9999;
    double min_t       = 9999;
    double min_d       = 9999;

    //�����l
    colRiseSet_[index_time].SetD( 0.5 );

    for( cnt = 0; cnt < cnt_end; cnt++ )
    {
      //�o�x�A�ܓx�A�������v�Z
      CalcLocation(     colRiseSet_[index_time].GetD(),          ret_loc );
      CalcSiderealTime( colRiseSet_[index_time].GetD(), locHere, gst_ );

      if( index_time == TIME_MERIDIAN )
      {
        tk = 0;  //�쒆
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

      //tk - t�̐�Βl��180�𒴂��Ȃ��悤�ɕ␳
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

      //���[�v����delta_d�̍ŏ��l�ƁA���̎���d��ێ�
      if( Math.abs(min_delta_d) > Math.abs(delta_d) )
      {
        min_delta_d = delta_d;
        min_t       = t;
        min_d       = colRiseSet_[index_time].GetD();
      }
      
      //�؂�グ����
      //delta_d ���������l�ȉ��ɂȂ�����v�Z���x�ɉe�����Ȃ��̂�
      //���[�v�̓r���Ő؂�グ��
      if( Math.abs( min_delta_d ) < DELTA_D_TH )
      {
        break;
      }
    }

    //���[�v���̍ŏ��l���̗p���A�Z�b�g����
    colRiseSet_[index_time].SetD( min_d );

    //���ʊp�A���x�p�̌v�Z
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
    //�l�̌ܓ����āA�w�肵�������Ɋۂ߂�
    double ans = 0.0;

    double multi = Math.pow( 10.0, (double)round_digit );
    ans = Math.round( unrounded_value * multi ) / multi;
    
    return ans;
  }
  
  // Override���郁�\�b�h ============================
  public double CalcLongitudeYellow( double T )
  {
    //�����萔(T)�ɂ�鉩�o(��)�̋ߎ���
    //�p�������N���X���Ōv�Z
    return 0;
  }
  public double CalcLatitudeYellow( double T )
  {
    //�����萔(T)�ɂ�鉩��(��)�̋ߎ���
    //�p�������N���X���Ōv�Z
    return 0;
  }
  public double CalcParallax( double T )
  {
    //�����萔(T)�ɂ�鎋���̋ߎ���
    //�p�������N���X���Ōv�Z
    return 0;
  }
  public double CalcDistance( double T )
  {
    //���Ԓ萔(T)�ɂ�鋗���̋ߎ���
    //�p�������N���X���Ōv�Z
    return 0;
  }

  public void toRed( Location loc_yellow, double T, Location loc_red )
  {
    //���o�A���܂���Ԍo�A�Ԉܓx�ɕϊ�
    //�p�������N���X���Ōv�Z
  }
  public void CalcLocation( double T, Location locRed )
  {
    //�����萔(T)�ɂ�鉩�o�A���܂̋ߎ�������
    //�Ԍo�A�Ԉ܂��v�Z����
    //�p�������N���X���Ōv�Z
  }
  // Override���郁�\�b�h ============================

  public void CalcGST( int hour, double jd )
  {
    //�O���j�b�W�P�������ߎ����Ōv�Z
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
    //���ʊp(���ɕϊ��j
    return Math.toDegrees( codHere_.GetAzimuth() );
  }
  public double GetHeight()
  {
    //���x�p(���ɕϊ��j
    return Math.toDegrees( codHere_.GetHeight() );
  }
  public double GetTimeAngle()
  {
    return t_;
  }

  //�o�v�֘A
  public double GetRiseSetTime( int index_time )
  {
    //�o�ߎ���
    return colRiseSet_[index_time].GetD();
  }
  public double GetRiseSetAzimuth( int index_time )
  {
    //���ʊp(���ɕϊ��j
    return Math.toDegrees( colRiseSet_[index_time].GetAzimuth() );
  }
  public double GetRiseSetHeight ( int index_time )
  {
    //���x�p(���ɕϊ��j
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
