package com.alt_r.www.multi.moontracker;

public class Sun extends Astronomy
{
  private final double ANGLE_PER_CYCLE = 360.0;  //1周期の角度

  Sun()
  {
    init( ANGLE_PER_CYCLE );
  }

  @Override
  public double CalcLongitudeYellow( double T )
  {
    //時刻定数(T)による黄経(λ)の近似式
    double ramda = 0;

    ramda =
      280.4603 + 360.00769 * T +
      (1.9146 - 0.00005 * T) * Math.sin( Math.toRadians( 357.538 + 359.991 * T )) +
      0.0200 * Math.sin( Math.toRadians( 355.05  +   719.981  * T ) ) +
      0.0048 * Math.sin( Math.toRadians( 234.95  +    19.341  * T ) ) +
      0.0020 * Math.sin( Math.toRadians( 247.1   +   329.64   * T ) ) +
      0.0018 * Math.sin( Math.toRadians( 297.8   +  4452.67   * T ) ) +
      0.0018 * Math.sin( Math.toRadians( 251.3   +     0.20   * T ) ) +
      0.0015 * Math.sin( Math.toRadians( 343.2   +   450.3    * T ) ) +
      0.0013 * Math.sin( Math.toRadians(  81.4   +   225.18   * T ) ) +
      0.0008 * Math.sin( Math.toRadians( 132.5   +   659.29   * T ) ) +
      0.0007 * Math.sin( Math.toRadians( 153.3   +    90.38   * T ) ) +
      0.0007 * Math.sin( Math.toRadians( 206.8   +    30.35   * T ) ) +
      0.0006 * Math.sin( Math.toRadians(  29.8   +   337.18   * T ) ) +
      0.0005 * Math.sin( Math.toRadians( 207.4   +     1.50   * T ) ) +
      0.0005 * Math.sin( Math.toRadians( 291.2   +    22.81   * T ) ) +
      0.0004 * Math.sin( Math.toRadians( 234.9   +   315.56   * T ) ) +
      0.0004 * Math.sin( Math.toRadians( 157.3   +   299.30   * T ) ) +
      0.0004 * Math.sin( Math.toRadians(  21.1   +   720.02   * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 352.5   +  1079.97   * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 329.7   +    44.43   * T ) );

    //0～360°の範囲にする処理
    return AdjustDegree( ramda );
  }
//  @Override
//  public double CalcLongitudeYellowExactly( double T )
//  {
//    //時刻定数(T)による黄経(λ)の近似式
//    BigDecimal ramda = new BigDecimal( "0.0" );
//
//    ramda
//      = ramda.add( CalcItem( Double.toString( 280.4603 + 360.00769 * T ), 5 ) );
//    ramda
//      = ramda.add( CalcItem( Double.toString( (1.9146 - 0.00005 * T) ), "357.538", "359.991", T, 5 ) );
//
//    ramda = ramda.add( CalcItem( "0.0200", "355.05", " 719.981", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0048", "234.95", "  19.341", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0020", "247.1",  " 329.64 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0018", "297.8",  "4452.67 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0018", "251.3",  "   0.20 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0015", "343.2",  " 450.3  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0013", " 81.4",  " 225.18 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0008", "132.5",  " 659.29 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0007", "153.3",  "  90.38 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0007", "206.8",  "  30.35 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0006", " 29.8",  " 337.18 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0005", "207.4",  "   1.50 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0005", "291.2",  "  22.8  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0004", "234.9",  " 315.56 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0004", "157.3",  " 299.30 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0004", " 21.1",  " 720.02 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0003", "352.5",  "1079.97 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0003", "329.7",  "  44.43 ", T, 5 ) );
//
//    //0～360°の範囲にする処理
//    return AdjustDegree( ramda.doubleValue() );
//  }

  public double CalcDistance( double T )
  {
    //時間定数(T)による距離(AU:天文距離)の近似式
    double q = 0;
    double r = 0;

    q =
      (0.007256 - 0.0000002 * T) * Math.sin( Math.toRadians( 267.56 + 359.991 * T )) +
      0.000091 * Math.sin( Math.toRadians( 265.1   +   719.981  * T ) ) +
      0.000030 * Math.sin( Math.toRadians(  90.0                    ) ) +
      0.000013 * Math.sin( Math.toRadians(  27.8   +  4452.67   * T ) ) +
      0.000007 * Math.sin( Math.toRadians( 254.0   +   450.4    * T ) ) +
      0.000007 * Math.sin( Math.toRadians( 156.0   +   329.6    * T ) );

    r = Math.pow( 10, q );

    return r;
  }
//  public double CalcDistanceExactly( double T )
//  {
//    //時間定数(T)による距離(AU:天文距離)の近似式
//    double r = 0;
// 
//    BigDecimal q = new BigDecimal( "0.0" );
//
////    q =
////      (0.007256 - 0.0000002 * T) * Math.sin( Math.toRadians( 267.56 + 359.991 * T )) +
////      0.000091 * Math.sin( Math.toRadians( 265.1   +   719.981  * T ) ) +
////      0.000030 * Math.sin( Math.toRadians(  90.0                    ) ) +
////      0.000013 * Math.sin( Math.toRadians(  27.8   +  4452.67   * T ) ) +
////      0.000007 * Math.sin( Math.toRadians( 254.0   +   450.4    * T ) ) +
////      0.000007 * Math.sin( Math.toRadians( 156.0   +   329.6    * T ) );
//    T = -0.13312597;
//
//    q.add( CalcItem( Double.toString( (0.007256 - 0.0000002 * T) ), "267.56", " 359.991", T, 7 ) );
//    q.add( CalcItem( "0.000091", "265.1",  " 719.981", T, 7 ) );
//    q.add( CalcItem( "0.000030", " 90.0",  "   0.0",   0, 7 ) );
//    q.add( CalcItem( "0.000013", " 27.8",  "4452.67",  T, 7 ) );
//    q.add( CalcItem( "0.000007", "254.0",  " 450.4 ",  T, 7 ) );
//    q.add( CalcItem( "0.000007", "156.0",  " 329.6 ",  T, 7 ) );
// 
//    r = Math.pow( 10, q.doubleValue() );
//
//    return r;
//  }

  @Override
  public void CalcLocation( double T, Location locRed )
  {
    //時刻定数(T)による黄経、黄緯の近似式から
    //赤経、赤緯を計算する
    locYellow_.SetLongitude( CalcLongitudeYellow( T ) );

    r_ = CalcDistance( T );
    S_ = 0.266994 / r_;
  }
}
