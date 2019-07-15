package com.alt_r.www.multi.moontracker;

public class Moon extends Astronomy
{
  private final double ANGLE_PER_CYCLE = 347.8;
  
  Moon()
  {
    init( ANGLE_PER_CYCLE );
  }
  
  @Override
  public double CalcLongitudeYellow( double T )
  {
    //時刻定数(T)による黄経(λ)の近似式
    double A     = 0;
    double ramda = 0;

    A =
      0.0040 * Math.sin( Math.toRadians( 119.5   +     1.33   * T ) ) +
      0.0020 * Math.sin( Math.toRadians(  55.0   +    19.34   * T ) ) +
      0.0006 * Math.sin( Math.toRadians(  71.0   +     0.2    * T ) ) +
      0.0006 * Math.sin( Math.toRadians(  54.0   +    19.3    * T ) );

    //黄経
    ramda += 218.3161 + 4812.67881 * T +
      6.2887 * Math.sin( Math.toRadians( 134.961 +  4771.9886 * T + A )) +
      1.2740 * Math.sin( Math.toRadians( 100.738 +  4133.3536 * T ) ) +
      0.6583 * Math.sin( Math.toRadians( 235.700 +  8905.3422 * T ) ) +
      0.2136 * Math.sin( Math.toRadians( 269.926 +  9543.9773 * T ) ) +
      0.1856 * Math.sin( Math.toRadians( 177.525 +   359.9905 * T ) ) +
      0.1143 * Math.sin( Math.toRadians(   6.546 +  9664.0404 * T ) ) +
      0.0588 * Math.sin( Math.toRadians( 214.22  +   638.635  * T ) ) +
      0.0572 * Math.sin( Math.toRadians( 103.21  +  3773.363  * T ) ) +
      0.0533 * Math.sin( Math.toRadians(  10.66  + 13677.331  * T ) ) +
      0.0459 * Math.sin( Math.toRadians( 238.18  +  8545.352  * T ) ) +
      0.0410 * Math.sin( Math.toRadians( 137.43  +  4411.998  * T ) ) +
      0.0348 * Math.sin( Math.toRadians( 117.84  +  4452.671  * T ) ) +
      0.0305 * Math.sin( Math.toRadians( 312.49  +  5131.979  * T ) ) +
      0.0153 * Math.sin( Math.toRadians( 130.84  +   758.698  * T ) ) +
      0.0125 * Math.sin( Math.toRadians( 141.51  + 14436.029  * T ) ) +
      0.0110 * Math.sin( Math.toRadians( 231.59  +  4892.052  * T ) ) +
      0.0107 * Math.sin( Math.toRadians( 336.44  + 13038.696  * T ) ) +
      0.0100 * Math.sin( Math.toRadians(  44.89  + 14315.966  * T ) ) +
      0.0085 * Math.sin( Math.toRadians( 201.5   +  8266.71   * T ) ) +
      0.0079 * Math.sin( Math.toRadians( 278.2   +  4493.34   * T ) ) +
      0.0068 * Math.sin( Math.toRadians(  53.2   +  9265.33   * T ) ) +
      0.0052 * Math.sin( Math.toRadians( 197.2   +   319.32   * T ) ) +
      0.0050 * Math.sin( Math.toRadians( 295.4   +  4812.66   * T ) ) +
      0.0048 * Math.sin( Math.toRadians( 235.0   +    19.34   * T ) ) +
      0.0040 * Math.sin( Math.toRadians(  13.2   + 13317.34   * T ) ) +
      0.0040 * Math.sin( Math.toRadians( 145.6   + 18449.32   * T ) ) +
      0.0040 * Math.sin( Math.toRadians( 119.5   +     1.33   * T ) ) +
      0.0039 * Math.sin( Math.toRadians( 111.3   + 17810.68   * T ) ) +
      0.0037 * Math.sin( Math.toRadians( 349.1   +  5410.62   * T ) ) +
      0.0027 * Math.sin( Math.toRadians( 272.5   +  9183.99   * T ) ) +
      0.0026 * Math.sin( Math.toRadians( 107.2   + 13797.39   * T ) ) +
      0.0024 * Math.sin( Math.toRadians( 211.9   +   988.63   * T ) ) +
      0.0024 * Math.sin( Math.toRadians( 252.8   +  9224.66   * T ) ) +
      0.0022 * Math.sin( Math.toRadians( 240.6   +  8185.36   * T ) ) +
      0.0021 * Math.sin( Math.toRadians(  87.5   +  9903.97   * T ) ) +
      0.0021 * Math.sin( Math.toRadians( 175.1   +   719.98   * T ) ) +
      0.0021 * Math.sin( Math.toRadians( 105.6   +  3413.37   * T ) ) +
      0.0020 * Math.sin( Math.toRadians(  55.0   +    19.34   * T ) ) +
      0.0018 * Math.sin( Math.toRadians(   4.1   +  4013.29   * T ) ) +
      0.0016 * Math.sin( Math.toRadians( 242.2   + 18569.38   * T ) ) +
      0.0012 * Math.sin( Math.toRadians( 339.0   + 12678.71   * T ) ) +
      0.0011 * Math.sin( Math.toRadians( 276.5   + 19208.02   * T ) ) +
      0.0009 * Math.sin( Math.toRadians( 218.0   +  8586.0    * T ) ) +
      0.0008 * Math.sin( Math.toRadians( 188.0   + 14037.3    * T ) ) +
      0.0008 * Math.sin( Math.toRadians( 204.0   +  7906.7    * T ) ) +
      0.0007 * Math.sin( Math.toRadians( 140.0   +  4052.0    * T ) ) +
      0.0007 * Math.sin( Math.toRadians( 275.0   +  4853.3    * T ) ) +
      0.0007 * Math.sin( Math.toRadians( 216.0   +   278.6    * T ) ) +
      0.0006 * Math.sin( Math.toRadians( 128.0   +  1118.7    * T ) ) +
      0.0005 * Math.sin( Math.toRadians( 247.0   + 22582.7    * T ) ) +
      0.0005 * Math.sin( Math.toRadians( 181.0   + 19088.0    * T ) ) +
      0.0005 * Math.sin( Math.toRadians( 114.0   + 17450.7    * T ) ) +
      0.0005 * Math.sin( Math.toRadians( 332.0   +  5091.3    * T ) ) +
      0.0004 * Math.sin( Math.toRadians( 313.0   +   398.7    * T ) ) +
      0.0004 * Math.sin( Math.toRadians( 278.0   +   120.1    * T ) ) +
      0.0004 * Math.sin( Math.toRadians(  71.0   +  9584.7    * T ) ) +
      0.0004 * Math.sin( Math.toRadians(  20.0   +   720.0    * T ) ) +
      0.0003 * Math.sin( Math.toRadians(  83.0   +  3814.0    * T ) ) +
      0.0003 * Math.sin( Math.toRadians(  66.0   +  3494.7    * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 147.0   + 18089.3    * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 311.0   +  5492.0    * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 161.0   +    40.7    * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 280.0   + 23221.3    * T ) );

    //0～360°の範囲にする処理
    return AdjustDegree( ramda );
  }
//  @Override
//  public double CalcLongitudeYellowExactly( double T )
//  {
//    //時刻定数(T)による黄経(λ)の近似式
//    float  item = 0;
//
//    float A     = 0;
//    float ramda = 0;
//
//    item = (float) round( 0.0040 * Math.sin( Math.toRadians( 119.5   +     1.33   * T ) ), 5 ); A += item;
//    item = (float) round( 0.0020 * Math.sin( Math.toRadians(  55.0   +    19.34   * T ) ), 5 ); A += item;
//    item = (float) round( 0.0006 * Math.sin( Math.toRadians(  71.0   +     0.2    * T ) ), 5 ); A += item;
//    item = (float) round( 0.0006 * Math.sin( Math.toRadians(  54.0   +    19.3    * T ) ), 5 ); A += item;
//
//    //黄経
//    item = (float) ( 218.3161 + 4812.67881 * T); ramda += item;
//    item = (float) ( 6.2887 * Math.sin( Math.toRadians( 134.961 +  4771.9886 * T + A ) ) ); ramda += item;
//
//    item = (float) ( 1.2740 * Math.sin( Math.toRadians( 100.738 +  4133.3536 * T ) ) ); ramda += item;
//    item = (float) ( 0.6583 * Math.sin( Math.toRadians( 235.700 +  8905.3422 * T ) ) ); ramda += item;
//    item = (float) ( 0.2136 * Math.sin( Math.toRadians( 269.926 +  9543.9773 * T ) ) ); ramda += item;
//    item = (float) ( 0.1856 * Math.sin( Math.toRadians( 177.525 +   359.9905 * T ) ) ); ramda += item;
//    item = (float) ( 0.1143 * Math.sin( Math.toRadians(   6.546 +  9664.0404 * T ) ) ); ramda += item;
//    item = (float) ( 0.0588 * Math.sin( Math.toRadians( 214.22  +   638.635  * T ) ) ); ramda += item;
//    item = (float) ( 0.0572 * Math.sin( Math.toRadians( 103.21  +  3773.363  * T ) ) ); ramda += item;
//    item = (float) ( 0.0533 * Math.sin( Math.toRadians(  10.66  + 13677.331  * T ) ) ); ramda += item;
//    item = (float) ( 0.0459 * Math.sin( Math.toRadians( 238.18  +  8545.352  * T ) ) ); ramda += item;
//    item = (float) ( 0.0410 * Math.sin( Math.toRadians( 137.43  +  4411.998  * T ) ) ); ramda += item;
//    item = (float) ( 0.0348 * Math.sin( Math.toRadians( 117.84  +  4452.671  * T ) ) ); ramda += item;
//    item = (float) ( 0.0305 * Math.sin( Math.toRadians( 312.49  +  5131.979  * T ) ) ); ramda += item;
//    item = (float) ( 0.0153 * Math.sin( Math.toRadians( 130.84  +   758.698  * T ) ) ); ramda += item;
//    item = (float) ( 0.0125 * Math.sin( Math.toRadians( 141.51  + 14436.029  * T ) ) ); ramda += item;
//    item = (float) ( 0.0110 * Math.sin( Math.toRadians( 231.59  +  4892.052  * T ) ) ); ramda += item;
//    item = (float) ( 0.0107 * Math.sin( Math.toRadians( 336.44  + 13038.696  * T ) ) ); ramda += item;
//    item = (float) ( 0.0100 * Math.sin( Math.toRadians(  44.89  + 14315.966  * T ) ) ); ramda += item;
//    item = (float) ( 0.0085 * Math.sin( Math.toRadians( 201.5   +  8266.71   * T ) ) ); ramda += item;
//    item = (float) ( 0.0079 * Math.sin( Math.toRadians( 278.2   +  4493.34   * T ) ) ); ramda += item;
//    item = (float) ( 0.0068 * Math.sin( Math.toRadians(  53.2   +  9265.33   * T ) ) ); ramda += item;
//    item = (float) ( 0.0052 * Math.sin( Math.toRadians( 197.2   +   319.32   * T ) ) ); ramda += item;
//    item = (float) ( 0.0050 * Math.sin( Math.toRadians( 295.4   +  4812.66   * T ) ) ); ramda += item;
//    item = (float) ( 0.0048 * Math.sin( Math.toRadians( 235.0   +    19.34   * T ) ) ); ramda += item;
//    item = (float) ( 0.0040 * Math.sin( Math.toRadians(  13.2   + 13317.34   * T ) ) ); ramda += item;
//    item = (float) ( 0.0040 * Math.sin( Math.toRadians( 145.6   + 18449.32   * T ) ) ); ramda += item;
//    item = (float) ( 0.0040 * Math.sin( Math.toRadians( 119.5   +     1.33   * T ) ) ); ramda += item;
//    item = (float) ( 0.0039 * Math.sin( Math.toRadians( 111.3   + 17810.68   * T ) ) ); ramda += item;
//    item = (float) ( 0.0037 * Math.sin( Math.toRadians( 349.1   +  5410.62   * T ) ) ); ramda += item;
//    item = (float) ( 0.0027 * Math.sin( Math.toRadians( 272.5   +  9183.99   * T ) ) ); ramda += item;
//    item = (float) ( 0.0026 * Math.sin( Math.toRadians( 107.2   + 13797.39   * T ) ) ); ramda += item;
//    item = (float) ( 0.0024 * Math.sin( Math.toRadians( 211.9   +   988.63   * T ) ) ); ramda += item;
//    item = (float) ( 0.0024 * Math.sin( Math.toRadians( 252.8   +  9224.66   * T ) ) ); ramda += item;
//    item = (float) ( 0.0022 * Math.sin( Math.toRadians( 240.6   +  8185.36   * T ) ) ); ramda += item;
//    item = (float) ( 0.0021 * Math.sin( Math.toRadians(  87.5   +  9903.97   * T ) ) ); ramda += item;
//    item = (float) ( 0.0021 * Math.sin( Math.toRadians( 175.1   +   719.98   * T ) ) ); ramda += item;
//    item = (float) ( 0.0021 * Math.sin( Math.toRadians( 105.6   +  3413.37   * T ) ) ); ramda += item;
//    item = (float) ( 0.0020 * Math.sin( Math.toRadians(  55.0   +    19.34   * T ) ) ); ramda += item;
//    item = (float) ( 0.0018 * Math.sin( Math.toRadians(   4.1   +  4013.29   * T ) ) ); ramda += item;
//    item = (float) ( 0.0016 * Math.sin( Math.toRadians( 242.2   + 18569.38   * T ) ) ); ramda += item;
//    item = (float) ( 0.0012 * Math.sin( Math.toRadians( 339.0   + 12678.71   * T ) ) ); ramda += item;
//    item = (float) ( 0.0011 * Math.sin( Math.toRadians( 276.5   + 19208.02   * T ) ) ); ramda += item;
//    item = (float) ( 0.0009 * Math.sin( Math.toRadians( 218.0   +  8586.0    * T ) ) ); ramda += item;
//    item = (float) ( 0.0008 * Math.sin( Math.toRadians( 188.0   + 14037.3    * T ) ) ); ramda += item;
//    item = (float) ( 0.0008 * Math.sin( Math.toRadians( 204.0   +  7906.7    * T ) ) ); ramda += item;
//    item = (float) ( 0.0007 * Math.sin( Math.toRadians( 140.0   +  4052.0    * T ) ) ); ramda += item;
//    item = (float) ( 0.0007 * Math.sin( Math.toRadians( 275.0   +  4853.3    * T ) ) ); ramda += item;
//    item = (float) ( 0.0007 * Math.sin( Math.toRadians( 216.0   +   278.6    * T ) ) ); ramda += item;
//    item = (float) ( 0.0006 * Math.sin( Math.toRadians( 128.0   +  1118.7    * T ) ) ); ramda += item;
//    item = (float) ( 0.0005 * Math.sin( Math.toRadians( 247.0   + 22582.7    * T ) ) ); ramda += item;
//    item = (float) ( 0.0005 * Math.sin( Math.toRadians( 181.0   + 19088.0    * T ) ) ); ramda += item;
//    item = (float) ( 0.0005 * Math.sin( Math.toRadians( 114.0   + 17450.7    * T ) ) ); ramda += item;
//    item = (float) ( 0.0005 * Math.sin( Math.toRadians( 332.0   +  5091.3    * T ) ) ); ramda += item;
//    item = (float) ( 0.0004 * Math.sin( Math.toRadians( 313.0   +   398.7    * T ) ) ); ramda += item;
//    item = (float) ( 0.0004 * Math.sin( Math.toRadians( 278.0   +   120.1    * T ) ) ); ramda += item;
//    item = (float) ( 0.0004 * Math.sin( Math.toRadians(  71.0   +  9584.7    * T ) ) ); ramda += item;
//    item = (float) ( 0.0004 * Math.sin( Math.toRadians(  20.0   +   720.0    * T ) ) ); ramda += item;
//    item = (float) ( 0.0003 * Math.sin( Math.toRadians(  83.0   +  3814.0    * T ) ) ); ramda += item;
//    item = (float) ( 0.0003 * Math.sin( Math.toRadians(  66.0   +  3494.7    * T ) ) ); ramda += item;
//    item = (float) ( 0.0003 * Math.sin( Math.toRadians( 147.0   + 18089.3    * T ) ) ); ramda += item;
//    item = (float) ( 0.0003 * Math.sin( Math.toRadians( 311.0   +  5492.0    * T ) ) ); ramda += item;
//    item = (float) ( 0.0003 * Math.sin( Math.toRadians( 161.0   +    40.7    * T ) ) ); ramda += item;
//    item = (float) ( 0.0003 * Math.sin( Math.toRadians( 280.0   + 23221.3    * T ) ) ); ramda += item;
//
//    //0～360°の範囲にする処理
//    return (float) AdjustDegree( (double)ramda );
//  }
//  @Override
//  public double CalcLongitudeYellowExactly( double T )
//  {
//    //時刻定数(T)による黄経(λ)の近似式
//    int item = 0;
//
//    int  A     = 0;
//    long ramda = 0;
//
//    item = (int) (1000000 * 0.0040 * Math.sin( Math.toRadians( 119.5   +     1.33   * T ) )); A += item;
//    item = (int) (1000000 * 0.0020 * Math.sin( Math.toRadians(  55.0   +    19.34   * T ) )); A += item;
//    item = (int) (1000000 * 0.0006 * Math.sin( Math.toRadians(  71.0   +     0.2    * T ) )); A += item;
//    item = (int) (1000000 * 0.0006 * Math.sin( Math.toRadians(  54.0   +    19.3    * T ) )); A += item;
//
//    //黄経
//    item = (int) (1000000 * (218.3161 + 4812.67881 * T)); ramda += item;
//    item = (int) (1000000 * 6.2887 * Math.sin( Math.toRadians( 134.961 +  4771.9886 * T + (double)( A / 1000000 ) ) ) ); ramda += item;
//
//    item = (int) (1000000 * 1.2740 * Math.sin( Math.toRadians( 100.738 +  4133.3536 * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.6583 * Math.sin( Math.toRadians( 235.700 +  8905.3422 * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.2136 * Math.sin( Math.toRadians( 269.926 +  9543.9773 * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.1856 * Math.sin( Math.toRadians( 177.525 +   359.9905 * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.1143 * Math.sin( Math.toRadians(   6.546 +  9664.0404 * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0588 * Math.sin( Math.toRadians( 214.22  +   638.635  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0572 * Math.sin( Math.toRadians( 103.21  +  3773.363  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0533 * Math.sin( Math.toRadians(  10.66  + 13677.331  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0459 * Math.sin( Math.toRadians( 238.18  +  8545.352  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0410 * Math.sin( Math.toRadians( 137.43  +  4411.998  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0348 * Math.sin( Math.toRadians( 117.84  +  4452.671  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0305 * Math.sin( Math.toRadians( 312.49  +  5131.979  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0153 * Math.sin( Math.toRadians( 130.84  +   758.698  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0125 * Math.sin( Math.toRadians( 141.51  + 14436.029  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0110 * Math.sin( Math.toRadians( 231.59  +  4892.052  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0107 * Math.sin( Math.toRadians( 336.44  + 13038.696  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0100 * Math.sin( Math.toRadians(  44.89  + 14315.966  * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0085 * Math.sin( Math.toRadians( 201.5   +  8266.71   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0079 * Math.sin( Math.toRadians( 278.2   +  4493.34   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0068 * Math.sin( Math.toRadians(  53.2   +  9265.33   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0052 * Math.sin( Math.toRadians( 197.2   +   319.32   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0050 * Math.sin( Math.toRadians( 295.4   +  4812.66   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0048 * Math.sin( Math.toRadians( 235.0   +    19.34   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0040 * Math.sin( Math.toRadians(  13.2   + 13317.34   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0040 * Math.sin( Math.toRadians( 145.6   + 18449.32   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0040 * Math.sin( Math.toRadians( 119.5   +     1.33   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0039 * Math.sin( Math.toRadians( 111.3   + 17810.68   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0037 * Math.sin( Math.toRadians( 349.1   +  5410.62   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0027 * Math.sin( Math.toRadians( 272.5   +  9183.99   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0026 * Math.sin( Math.toRadians( 107.2   + 13797.39   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0024 * Math.sin( Math.toRadians( 211.9   +   988.63   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0024 * Math.sin( Math.toRadians( 252.8   +  9224.66   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0022 * Math.sin( Math.toRadians( 240.6   +  8185.36   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0021 * Math.sin( Math.toRadians(  87.5   +  9903.97   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0021 * Math.sin( Math.toRadians( 175.1   +   719.98   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0021 * Math.sin( Math.toRadians( 105.6   +  3413.37   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0020 * Math.sin( Math.toRadians(  55.0   +    19.34   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0018 * Math.sin( Math.toRadians(   4.1   +  4013.29   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0016 * Math.sin( Math.toRadians( 242.2   + 18569.38   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0012 * Math.sin( Math.toRadians( 339.0   + 12678.71   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0011 * Math.sin( Math.toRadians( 276.5   + 19208.02   * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0009 * Math.sin( Math.toRadians( 218.0   +  8586.0    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0008 * Math.sin( Math.toRadians( 188.0   + 14037.3    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0008 * Math.sin( Math.toRadians( 204.0   +  7906.7    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0007 * Math.sin( Math.toRadians( 140.0   +  4052.0    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0007 * Math.sin( Math.toRadians( 275.0   +  4853.3    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0007 * Math.sin( Math.toRadians( 216.0   +   278.6    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0006 * Math.sin( Math.toRadians( 128.0   +  1118.7    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0005 * Math.sin( Math.toRadians( 247.0   + 22582.7    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0005 * Math.sin( Math.toRadians( 181.0   + 19088.0    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0005 * Math.sin( Math.toRadians( 114.0   + 17450.7    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0005 * Math.sin( Math.toRadians( 332.0   +  5091.3    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0004 * Math.sin( Math.toRadians( 313.0   +   398.7    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0004 * Math.sin( Math.toRadians( 278.0   +   120.1    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0004 * Math.sin( Math.toRadians(  71.0   +  9584.7    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0004 * Math.sin( Math.toRadians(  20.0   +   720.0    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0003 * Math.sin( Math.toRadians(  83.0   +  3814.0    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0003 * Math.sin( Math.toRadians(  66.0   +  3494.7    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0003 * Math.sin( Math.toRadians( 147.0   + 18089.3    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0003 * Math.sin( Math.toRadians( 311.0   +  5492.0    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0003 * Math.sin( Math.toRadians( 161.0   +    40.7    * T ) ) ); ramda += item;
//    item = (int) (1000000 * 0.0003 * Math.sin( Math.toRadians( 280.0   + 23221.3    * T ) ) ); ramda += item;
//
//    //0～360°の範囲にする処理
//    return AdjustDegree( (double)( ramda / 1000000 ) );
//  }
//  @Override
//  public double CalcLongitudeYellowExactly( double T )
//  {
//    //時刻定数(T)による黄経(λ)の近似式
//    BigDecimal A     = new BigDecimal( "0.0" );
//    BigDecimal ramda = new BigDecimal( "0.0" );
//
//    //黄経
//    ramda = ramda.add( CalcItem( Double.toString( 218.3161 + 4812.67881 * T ), 5 ) );
//    ramda = ramda.add( CalcItem( "6.2887", "134.961", "4771.9886", T, 5 ) );
//    ramda = ramda.add( CalcItem( A.toString(), 5 ) );
//
//    ramda = ramda.add( CalcItem( "1.2740", "100.738", "4133.3536", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.6583", "235.700", "8905.3422", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.2136", "269.926", "9543.9773", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.1856", "177.525", " 359.9905", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.1143", "  6.546", "9664.0404", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0588", "214.22 ", " 638.635 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0572", "103.21 ", "3773.363 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0533", " 10.66", "13677.331 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0459", "238.18", " 8545.352 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0410", "137.43", " 4411.998 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0348", "117.84", " 4452.671 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0305", "312.49", " 5131.979 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0153", "130.84", "  758.698 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0125", "141.51", "14436.029 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0110", "231.59", " 4892.052 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0107", "336.44", "13038.696 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0100", " 44.89", "14315.966 ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0085", "201.5 ", " 8266.71  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0079", "278.2 ", " 4493.34  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0068", " 53.2 ", " 9265.33  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0052", "197.2 ", "  319.32  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0050", "295.4 ", " 4812.66  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0048", "235.0 ", "   19.34  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0040", " 13.2 ", "13317.34  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0040", "145.6 ", "18449.32  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0040", "119.5 ", "    1.33  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0039", "111.3 ", "17810.68  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0037", "349.1 ", " 5410.62  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0027", "272.5 ", " 9183.99  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0026", "107.2 ", "13797.39  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0024", "211.9 ", "  988.63  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0024", "252.8 ", " 9224.66  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0022", "240.6 ", " 8185.36  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0021", " 87.5 ", " 9903.97  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0021", "175.1 ", "  719.98  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0021", "105.6 ", " 3413.37  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0020", " 55.0 ", "   19.34  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0018", "  4.1 ", " 4013.29  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0016", "242.2 ", "18569.38  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0012", "339.0 ", "12678.71  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0011", "276.5 ", "19208.02  ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0009", "218.0 ", " 8586.0   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0008", "188.0 ", "14037.3   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0008", "204.0 ", " 7906.7   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0007", "140.0 ", " 4052.0   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0007", "275.0 ", " 4853.3   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0007", "216.0 ", "  278.6   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0006", "128.0 ", " 1118.7   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0005", "247.0 ", "22582.7   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0005", "181.0 ", "19088.0   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0005", "114.0 ", "17450.7   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0005", "332.0 ", " 5091.3   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0004", "313.0 ", "  398.7   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0004", "278.0 ", "  120.1   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0004", " 71.0 ", " 9584.7   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0004", " 20.0 ", "  720.0   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0003", " 83.0 ", " 3814.0   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0003", " 66.0 ", " 3494.7   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0003", "147.0 ", "18089.3   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0003", "311.0 ", " 5492.0   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0003", "161.0 ", "   40.7   ", T, 5 ) );
//    ramda = ramda.add( CalcItem( "0.0003", "280.0 ", "23221.3   ", T, 5 ) );
//
//    //0～360°の範囲にする処理
//    return AdjustDegree( ramda.doubleValue() );
//  }

  @Override
  public double CalcLatitudeYellow( double T )
  {
    //時刻定数(T)による黄緯(β)の近似式
    double B     = 0;
    double beta  = 0;

    B =
      0.0267 * Math.sin( Math.toRadians( 234.95  +    19.341  * T ) ) +
      0.0043 * Math.sin( Math.toRadians( 322.1   +    19.34   * T ) ) +
      0.0040 * Math.sin( Math.toRadians( 119.5   +     1.33   * T ) ) +
      0.0026 * Math.sin( Math.toRadians(  55.0   +    19.34   * T ) ) +
      0.0005 * Math.sin( Math.toRadians( 307.0   +    19.4    * T ) );

    //黄緯
    beta =
      5.1282 * Math.sin( Math.toRadians(  93.273 +  4832.0202 * T + B )) +
      0.2806 * Math.sin( Math.toRadians( 228.235 +  9604.0088 * T ) ) +
      0.2777 * Math.sin( Math.toRadians( 138.311 +    60.0316 * T ) ) +
      0.1732 * Math.sin( Math.toRadians( 142.427 +  4073.3220 * T ) ) +
      0.0554 * Math.sin( Math.toRadians( 194.01  +  8965.374  * T ) ) +
      0.0463 * Math.sin( Math.toRadians( 172.55  +   698.667  * T ) ) +
      0.0326 * Math.sin( Math.toRadians( 328.96  + 13737.362  * T ) ) +
      0.0172 * Math.sin( Math.toRadians(   3.18  + 14375.997  * T ) ) +
      0.0093 * Math.sin( Math.toRadians( 277.4   +  8845.31   * T ) ) +
      0.0088 * Math.sin( Math.toRadians( 176.7   +  4711.96   * T ) ) +
      0.0082 * Math.sin( Math.toRadians( 144.9   +  3713.33   * T ) ) +
      0.0043 * Math.sin( Math.toRadians( 307.6   +  5670.66   * T ) ) +
      0.0042 * Math.sin( Math.toRadians( 103.9   + 18509.35   * T ) ) +
      0.0034 * Math.sin( Math.toRadians( 319.9   +  4433.31   * T ) ) +
      0.0025 * Math.sin( Math.toRadians( 196.5   +  8605.38   * T ) ) +
      0.0022 * Math.sin( Math.toRadians( 331.4   + 13377.37   * T ) ) +
      0.0021 * Math.sin( Math.toRadians( 170.1   +  1058.66   * T ) ) +
      0.0019 * Math.sin( Math.toRadians( 230.7   +  9244.02   * T ) ) +
      0.0018 * Math.sin( Math.toRadians( 243.3   +  8206.68   * T ) ) +
      0.0018 * Math.sin( Math.toRadians( 270.8   +  5192.01   * T ) ) +
      0.0017 * Math.sin( Math.toRadians(  99.8   + 14496.06   * T ) ) +
      0.0016 * Math.sin( Math.toRadians( 135.7   +   420.02   * T ) ) +
      0.0015 * Math.sin( Math.toRadians( 211.1   +  9284.69   * T ) ) +
      0.0015 * Math.sin( Math.toRadians(  45.8   +  9964.00   * T ) ) +
      0.0014 * Math.sin( Math.toRadians( 219.2   +   299.96   * T ) ) +
      0.0013 * Math.sin( Math.toRadians(  95.8   +  4472.03   * T ) ) +
      0.0013 * Math.sin( Math.toRadians( 155.4   +   379.35   * T ) ) +
      0.0012 * Math.sin( Math.toRadians(  38.4   +  4812.68   * T ) ) +
      0.0012 * Math.sin( Math.toRadians( 148.2   +  4851.36   * T ) ) +
      0.0011 * Math.sin( Math.toRadians( 138.3   + 19147.99   * T ) ) +
      0.0010 * Math.sin( Math.toRadians(  18.0   + 12978.66   * T ) ) +
      0.0008 * Math.sin( Math.toRadians(  70.0   + 17870.7    * T ) ) +
      0.0008 * Math.sin( Math.toRadians( 326.0   +  9724.1    * T ) ) +
      0.0007 * Math.sin( Math.toRadians( 294.0   + 13098.7    * T ) ) +
      0.0006 * Math.sin( Math.toRadians( 224.0   +  5590.7    * T ) ) +
      0.0006 * Math.sin( Math.toRadians(  52.0   + 13617.3    * T ) ) +
      0.0005 * Math.sin( Math.toRadians( 280.0   +  8485.3    * T ) ) +
      0.0005 * Math.sin( Math.toRadians( 239.0   +  4193.4    * T ) ) +
      0.0004 * Math.sin( Math.toRadians( 311.0   +  9483.9    * T ) ) +
      0.0004 * Math.sin( Math.toRadians( 238.0   + 23281.3    * T ) ) +
      0.0004 * Math.sin( Math.toRadians(  81.0   + 10242.6    * T ) ) +
      0.0004 * Math.sin( Math.toRadians(  13.0   +  9325.4    * T ) ) +
      0.0004 * Math.sin( Math.toRadians( 147.0   + 14097.4    * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 205.0   + 22642.7    * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 107.0   + 18149.4    * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 146.0   +  3353.3    * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 234.0   + 19268.0    * T ) );

    //0～360°の範囲にする処理
    return AdjustDegree( beta );
  }

  public double CalcParallax( double T )
  {
    //時刻定数(T)による視差の近似式
    double pi = 0;

    pi=
      0.9507 * Math.sin( Math.toRadians( 90 ))                        +
      0.0518 * Math.sin( Math.toRadians( 224.98  +  4771.989  * T ) ) +
      0.0095 * Math.sin( Math.toRadians( 190.7   +  4133.35   * T ) ) +
      0.0078 * Math.sin( Math.toRadians( 325.7   +  8905.34   * T ) ) +
      0.0028 * Math.sin( Math.toRadians(   0.0   +  9543.98   * T ) ) +
      0.0009 * Math.sin( Math.toRadians( 100.0   + 13677.3    * T ) ) +
      0.0005 * Math.sin( Math.toRadians( 329.0   +  8545.4    * T ) ) +
      0.0004 * Math.sin( Math.toRadians( 194.0   +  3773.4    * T ) ) +
      0.0003 * Math.sin( Math.toRadians( 227.0   +  4412.0    * T ) );
    
    return pi;
  }

  @Override
  public double CalcDistance( double T )
  {
    //時間定数(T)による距離の近似式
    double r = 0;

    return r;
  }

  @Override
  public void toRed( Location loc_yellow, double T, Location loc_red )
  {
    //黄経、黄緯から赤経、赤緯度に変換
    double epsilon     = 23.439291 - 0.000130042 * T;  //黄道傾角度ε
    
    double sin_ramda   = Math.sin(Math.toRadians( loc_yellow.GetLongitude() ) );
    double sin_beta    = Math.sin(Math.toRadians( loc_yellow.GetLatitude()  ) );
    double sin_epsilon = Math.sin(Math.toRadians( epsilon ));

    double cos_ramda   = Math.cos(Math.toRadians( loc_yellow.GetLongitude() ) );
    double cos_beta    = Math.cos(Math.toRadians( loc_yellow.GetLatitude()  ) );
    double cos_epsilon = Math.cos(Math.toRadians( epsilon ));

    double U =  cos_beta * cos_ramda;
    double V = ( -sin_beta * sin_epsilon ) + ( cos_beta * sin_ramda * cos_epsilon );
    double W = (  sin_beta * cos_epsilon ) + ( cos_beta * sin_ramda * sin_epsilon );

    double longitude   = 0;
    double latitude    = 0;
    
    //赤経
    longitude = Math.atan( V / U );
    // ---- 象限補正 ----
    if( U >= 0 )
    {
      //補正なし
    }
    else if( U < 0 )
    {
      //90° < α < 270 -> 第2 or 第3象限
      longitude += Math.toRadians(180);  //180°を加算
    }
    //負の角度を360°表記に変換
    longitude  = AdjustRadian( longitude );

    //赤緯
    latitude
      =  Math.atan( W /
                    Math.sqrt( Math.pow( U, 2 ) + Math.pow( V, 2 ) ) );

    loc_red.SetLocation( latitude, longitude );
  }

  public void CalcLocation( double T, Location locRed )
  {
    //時刻定数(T)による黄経、黄緯の近似式から
    //赤経、赤緯を計算する
    locYellow_.SetLocation( CalcLatitudeYellow ( T ),
        CalcLongitudeYellow( T ) );
    //黄経、黄緯を赤経、赤緯に変換
    toRed( locYellow_, T, locRed );

    //視差を計算
    locRed.SetParallax( CalcParallax( T ) );
  }
}
