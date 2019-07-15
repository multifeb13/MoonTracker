package com.alt_r.www.multi.moontracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
 
public class CompassView extends View
{
  Compass compass;

  //画面更新
  private boolean isAttached;
  private static final long delay_milisec = 100;

  private Bitmap icon_moon[] = new Bitmap[8]; //30
  private Bitmap icon_phone;
  
  private Bitmap icon_gauge_moon[] = new Bitmap[8];
  private Bitmap icon_gauge_phone;

  private Bitmap icon_gauge_left;
  private Bitmap icon_gauge_center;
  private Bitmap icon_gauge_right;
  
//  private Bitmap icon_center;

  int wndWidth_  = 0;
  int wndHeight_ = 0;
  int wndRadius_ = 0;

  int wndCenterX_ = 0;
  int wndCenterY_ = 0;

  static double prev_moon_age_ = 0;

  public CompassView(Context context, AttributeSet attrs)
  {
    super(context,attrs);
    setFocusable(true);
 
    //ADTバグ対応
    //カスタムビューを使用すると、レイアウト（main.xml)に下記のWarningが表示されるため
    //if ( isInEditMode() ) return; で回避
    //The following classes could not be instantiated:
    //- com.alt_r.www.multi.moontracker.CompassView (Open Class, Show Error Log)
    //See the Error Log (Window > Show View) for more details.
    //Tip: Use View.isInEditMode() in your custom views to skip code when shown in Eclipse
    if( isInEditMode() ) return;

    Resources r = context.getResources();

    icon_moon[ 0]      = BitmapFactory.decodeResource( r, R.drawable.ic_moon_00 );
    icon_moon[ 1]      = BitmapFactory.decodeResource( r, R.drawable.ic_moon_01 );
    icon_moon[ 2]      = BitmapFactory.decodeResource( r, R.drawable.ic_moon_02 );
    icon_moon[ 3]      = BitmapFactory.decodeResource( r, R.drawable.ic_moon_03 );
    icon_moon[ 4]      = BitmapFactory.decodeResource( r, R.drawable.ic_moon_04 );
    icon_moon[ 5]      = BitmapFactory.decodeResource( r, R.drawable.ic_moon_05 );
    icon_moon[ 6]      = BitmapFactory.decodeResource( r, R.drawable.ic_moon_06 );
    icon_moon[ 7]      = BitmapFactory.decodeResource( r, R.drawable.ic_moon_07 );

    icon_phone        = BitmapFactory.decodeResource( r, R.drawable.ic_phone        );

    //    icon_gauge_moon   = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_moon   );
    icon_gauge_moon[ 0] = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_moon_00 );
    icon_gauge_moon[ 1] = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_moon_01 );
    icon_gauge_moon[ 2] = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_moon_02 );
    icon_gauge_moon[ 3] = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_moon_03 );
    icon_gauge_moon[ 4] = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_moon_04 );
    icon_gauge_moon[ 5] = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_moon_05 );
    icon_gauge_moon[ 6] = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_moon_06 );
    icon_gauge_moon[ 7] = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_moon_07 );

    icon_gauge_phone  = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_phone  );

    icon_gauge_left   = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_left   );
    icon_gauge_center = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_center );
    icon_gauge_right  = BitmapFactory.decodeResource( r, R.drawable.ic_gauge_right  );

    //    icon_center       = BitmapFactory.decodeResource( r, R.drawable.ic_center       );
    //    
    prev_moon_age_    = MoonTrackerActivity.moonage.GetLatestAge();
  }

  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);

    double azimuth_diff = 0;
    double moon_age     = 0;
    int    moon_index   = 0;

    if( compass == null ) return;

    //コンパスの方位角を計算
    azimuth_diff = MoonTrackerActivity.geoMoon.azimuth -
                   MoonTrackerActivity.geoSensor.azimuth;

    //月齢
    moon_age     = MoonTrackerActivity.moonage.GetLatestAge();
    if( moon_age != prev_moon_age_ )
    {
      prev_moon_age_ = moon_age;

      moon_index = toIconIndex( moon_age );
      compass.SetWheelIcon( 0, icon_moon      [moon_index] );
      compass.SetGaugeIcon( 0, icon_gauge_moon[moon_index] );
    }

    compass.SetWheelIconAzimuth( 0, azimuth_diff );
    compass.SetGaugeHeight     ( 0, MoonTrackerActivity.geoMoon.height   );

    compass.SetWheelIconAzimuth( 1, 0            );
    compass.SetGaugeHeight     ( 1, MoonTrackerActivity.geoSensor.height );

    compass.SetWheelArcAzimuth ( 0,
                                 MoonTrackerActivity.viewable.azimuth_begin -
                                 MoonTrackerActivity.geoSensor.azimuth,
                                 MoonTrackerActivity.viewable.azimuth_sweep);

    //外周の色 -----------------------------------------------------------
    //  月と端末の方位角の差の絶対値が10より小さかったら
    //　　外周の色をミドリにする
    compass.SetWheelColor( Math.abs( azimuth_diff ) < 10 ?
                           getResources().getColor( R.color.kColorGreenDroid ) :
                           getResources().getColor( R.color.kColorLightWhite )  );

    compass.Update( canvas );
  }

  public void onWindowFocusChanged( boolean hasFocus )
  {
    super.onWindowFocusChanged( hasFocus );

    double moon_age   = MoonTrackerActivity.moonage.GetLatestAge();
    int    moon_index = 0;

    //画面サイズ
    wndWidth_   = getWidth();
    wndHeight_  = getHeight();
    wndRadius_  = SelectMin( wndWidth_, wndHeight_ ) / 2;

    wndCenterX_ = wndWidth_  / 2;
    wndCenterY_ = wndHeight_ / 2;

    compass = new Compass( wndCenterX_, wndCenterY_, 2 );

    moon_index = toIconIndex( moon_age );
    compass.SetWheelRadius( wndRadius_ - ( icon_moon[moon_index].getWidth() / 2 ) );

    compass.SetWheelWidth ( 10 );
    compass.SetWheelIcon  ( 0, icon_moon[toIconIndex( moon_age )] );
    compass.SetWheelIcon  ( 1, icon_phone );

    compass.SetGaugeWidth ( 6 );
    compass.SetGaugeIcon  ( 0, icon_gauge_moon[moon_index]  );
    compass.SetGaugeIcon  ( 1, icon_gauge_phone );

    compass.SetGaugeBaseIcon  ( icon_gauge_left, icon_gauge_center, icon_gauge_right );
//    compass.SetGaugeCenterIcon( icon_center );
  }

  //画面更新
  @SuppressLint("HandlerLeak")
  protected void onAttachedToWindow()
  {
    Handler handler = new Handler()
    {
      public void handleMessage( Message msg )
      {
        if( isAttached )
        {
          invalidate();
          sendEmptyMessageDelayed( 0, delay_milisec );
        }
      }
    };

    isAttached = true;
    handler.sendEmptyMessageDelayed( 0, delay_milisec );
    
    super.onAttachedToWindow();
  }

  protected void onDetachedFromWindow()
  {
    isAttached = false;
    super.onDetachedFromWindow();
  }
  
  //------------------------------------------------------------
  private int SelectMin( int value1, int value2 )
  {
    return ( value1 < value2 ) ?
             value1 :
             value2;
  }
  
  private int toIconIndex( double moon_age )
  {
    //月齢を、アイコンのインデックスに変換
    int moon_index = 0;
 
    switch( (int)moon_age )
    {
      case 0:  //新月
      case 1:
        moon_index = 0; break;

      case 2:  //三日月
      case 3:
      case 4:
      case 5:
        moon_index = 1; break;

      case 6:  //上弦の月
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
        moon_index = 2; break;

      case 12:  //十三夜月
      case 13:
        moon_index = 3; break;

      case 14:  //満月
      case 15:
        moon_index = 4; break;

      case 16:  //立待月
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
        moon_index = 5; break;

      case 22:  //下弦の月
      case 23:
      case 24:
      case 25:
      case 26:
        moon_index = 6; break;

      case 27:  //新月
      case 28:
      case 29:
      case 30:
        moon_index = 7; break;

      default:
        moon_index = 0; break;
    }
    
    return moon_index;
  }
}