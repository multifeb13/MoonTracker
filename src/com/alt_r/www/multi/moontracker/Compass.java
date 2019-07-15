package com.alt_r.www.multi.moontracker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

class Icon
{
  double wheel_azimuth_;
  double gauge_height_;

  Bitmap  icon_wheel_;
  Bitmap  icon_gauge_;
}

public class Compass
{
  int    center_x_;      //中心(X)
  int    center_y_;      //中心(Y)

  int    icon_num_;
  Icon[] icon_;

  //外周------------------------------------------
  int      wheel_width_;   //幅
  int      wheel_color_;   //色
  int      wheel_radius_;  //半径

  double[] wheel_arc_azimuth_begin_ = new double[2];
  double[] wheel_arc_azimuth_sweep_ = new double[2];

  //ゲージ----------------------------------------
  int     gauge_width_;   //幅
  
//  Bitmap  center_icon_;    //ゲージの中心アイコン

  Bitmap  gauge_icon_left_;
  Bitmap  gauge_icon_center_;
  Bitmap  gauge_icon_right_;

  //　定数-----------------------------------------
  private final double kGAUGE_HEIGHT_MAX = 65.0;  //ゲージの上限(角度)
  private final double kGAUGE_HEIGHT_MIN =  0.0;  //ゲージの下限(角度)

  //色
  private final int kColorWheelCircle = 0xff2f2f2f; //Color.DKGRAY;
  
  //----------------------------------------------
  public Compass( int icon_num )
  {
    init( 0, 0, icon_num );
  }
  public Compass( int center_x, int center_y, int icon_num )
  {
    init( center_x, center_y, icon_num );
  }

  //--------------------------------------------------------------
  private int to_canvas_x( Bitmap icon, int icon_x )
  {
    return center_x_ + icon_x - ( icon.getWidth()  / 2 );
  }
  private int to_canvas_y( Bitmap icon, int icon_y )
  {
    return center_y_ - icon_y - ( icon.getHeight() / 2 );
  }
 
  private int to_canvas_x( Bitmap icon, int radius, double degree )
  {
    return to_canvas_x( icon, (int)(radius * Math.cos( Math.toRadians( degree ) ) ) );
  }
  private int to_canvas_y( Bitmap icon, int radius, double degree )
  {
    return to_canvas_y( icon, (int)(radius * Math.sin( Math.toRadians( degree ) ) ) );
  }

  //--------------------------------------------------------------
  public void init( int center_x, int center_y, int icon_num )
  {
    center_x_ = center_x;  //半径(X)
    center_y_ = center_y;  //半径(Y)

    icon_num_ = icon_num;
    icon_     = new Icon[icon_num];

    for( int icon_cnt = 0; icon_cnt < icon_num; icon_cnt++ )
    {
      icon_[icon_cnt] = new Icon();
    }

    //外周------------------------------------------
    wheel_width_  = 0;  //幅
    wheel_color_  = 0;  //色
    wheel_radius_ = 0;  //半径

    for( int index = 0; index < wheel_arc_azimuth_begin_.length; index++ )
    {
      wheel_arc_azimuth_begin_[index] = 0;
    }
    for( int index = 0; index < wheel_arc_azimuth_sweep_.length; index++ )
    {
      wheel_arc_azimuth_sweep_[index] = 0;
    }

    //ゲージ----------------------------------------
    gauge_width_  = 0;  //幅
  }

  //外周----------------------------------------------------------
  public void SetWheelRadius( int radius )
  {
    wheel_radius_ = radius;
  }
  public int GetWheelRadius()
  {
    return wheel_radius_;
  }
  public void SetWheelWidth( int wheel_width )
  {
    wheel_width_ = wheel_width;  //外周の幅
  }
  public void SetWheelColor( int wheel_color )
  {
    wheel_color_ = wheel_color;  //外周の色
  }

  public void SetWheelIconAzimuth( int index, double azimuth )
  {
    icon_[index].wheel_azimuth_ = ( ( toDegreeMath( azimuth ) + 360 ) % 360 );

    //アイコンを回転
//    icon_[index].rotate_  = (float)((( 90 - (azimuth - icon_[index].rotate_)) + 360) % 360);
  }
  public void SetWheelIcon( int index, Bitmap icon )
  {
    icon_[index].gauge_height_  = 0;
//    icon_[index].rotate_      = 0;

    icon_[index].icon_wheel_    = icon;
//    icon_[index].rotate_matrix_ = new Matrix();
  }

  public void SetWheelArcAzimuth( int index,
                                   double azimuth_begin, double azimuth_sweep )
  {
    wheel_arc_azimuth_begin_[index] = toDegreeArc( azimuth_begin );
    wheel_arc_azimuth_sweep_[index] = azimuth_sweep;
  }

  //ゲージ--------------------------------------------------------
  public void SetGaugeWidth( int gauge_width )
  {
    gauge_width_ = gauge_width;  //ゲージの幅
  }
  public void SetGaugeHeight ( int index, double height )
  {
    icon_[index].gauge_height_ = height;  //高度角
  }
  public void SetGaugeIcon( int index, Bitmap icon )
  {
    icon_[index].icon_gauge_   = icon;
  }

//  public void SetGaugeCenterIcon( Bitmap icon )
//  {
//    center_icon_ = icon;
//  }
  public void SetGaugeBaseIcon( Bitmap icon_left, Bitmap icon_center, Bitmap icon_right )
  {
    gauge_icon_left_   = icon_left;
    gauge_icon_center_ = icon_center;
    gauge_icon_right_  = icon_right;
  }
  //--------------------------------------------------------------
  private void DrawCircle( Canvas canvas,
                             int stroke_width, int stroke_color,
                             int radius )
  {
    Paint paint = new Paint();

    paint.setStrokeWidth( stroke_width );
    paint.setColor      ( stroke_color );
    paint.setStyle      ( Paint.Style.STROKE );

    canvas.drawCircle( (float)center_x_, (float)center_y_,
                       (float)radius, paint );
  }
  private void DrawArc( Canvas canvas,
                          int stroke_width, int stroke_color,
                          double azimuth_begin, double azimuth_sweep )
  {
    RectF rect  = new RectF( center_x_ - wheel_radius_, center_y_ - wheel_radius_,
                             center_x_ + wheel_radius_, center_y_ + wheel_radius_ );
    Paint paint = new Paint();
    
    paint.setStrokeWidth( stroke_width );
    paint.setColor      ( stroke_color );
    paint.setStyle      ( Paint.Style.STROKE );

    canvas.drawArc( rect,
                    (float)azimuth_begin, (float)azimuth_sweep,
                    false, paint );
  }
  
//  private void DrawLine( Canvas canvas,
//                           int stroke_width, int stroke_color,
//                           int start_x, int start_y,
//                           int end_x,   int end_y )
//  {
//    Paint paint = new Paint();
//
//    paint.setStrokeWidth( stroke_width );
//    paint.setColor      ( stroke_color );
//    paint.setStyle      ( Paint.Style.STROKE );
//
//    canvas.drawLine( (float)start_x, (float)start_y,
//                     (float)end_x,   (float)end_y,
//                     paint );
//  }
//  private void DrawText( Canvas canvas,
//                           int x,
//                           int y,
//                           String str)
//  {
//    Paint paint = new Paint();
// 
//    paint.setColor( Color.GRAY );
//    paint.setTextSize( 30 );
//
//    canvas.drawText( str, x, y, paint );
//  }
  
  public void Update( Canvas canvas )
  {
    int icon_cnt = 0;
 
    int canvas_x = 0;
    int canvas_y = 0;
    
    int gauge_radius         = 0;
    int kGAUGE_RADIUS_MERGIN = 10;
    int gauge_base_width     = 0;

    int gauge_x = 0;
    int gauge_y = 0;
 
    int gauge_x_left   = 0;
    int gauge_x_right  = 0;
    int gauge_y_top    = 0;
    int gauge_y_bottom = 0;

    double gauge_height = 0;

    canvas.drawColor( R.color.kColorBlack );

    //中心の円を描く
    DrawCircle( canvas,
                wheel_width_,
                kColorWheelCircle,
                wheel_radius_ );

    DrawArc(    canvas,
                wheel_width_,
                wheel_color_,
                wheel_arc_azimuth_begin_[0],
                wheel_arc_azimuth_sweep_[0] );
    
    gauge_radius
      = wheel_radius_ -  ( icon_[0].icon_wheel_.getHeight() / 2 ) - kGAUGE_RADIUS_MERGIN;
    gauge_base_width
      = (int) (gauge_radius * Math.sqrt(2));
//    gauge_base_height = gauge_base_width;

    gauge_x_left   = center_x_ - ( gauge_base_width / 2 );
    gauge_x_right  = center_x_ + ( gauge_base_width / 2 );
    gauge_y_top    = center_y_ - ( wheel_radius_ - icon_[icon_cnt].icon_wheel_.getHeight() / 2 );
    gauge_y_bottom = center_y_ + ( gauge_base_width / 2 );

//    //左
    gauge_x = gauge_x_left;
    gauge_y = gauge_y_bottom - gauge_icon_left_.getHeight();
    canvas.drawBitmap( gauge_icon_left_,
                       gauge_x, gauge_y, null );

    //中
    gauge_x += gauge_icon_left_.getWidth();
    do
    {
      canvas.drawBitmap( gauge_icon_center_,
                         gauge_x, gauge_y, null );
      gauge_x += gauge_icon_center_.getWidth();
    } while( gauge_x < ( gauge_x_right - gauge_icon_right_.getWidth() ) );
    
    //右
    gauge_x = gauge_x_right - gauge_icon_right_.getWidth();
    canvas.drawBitmap( gauge_icon_right_, gauge_x, gauge_y, null );
    
    //---------------------------------------------

//    //中心のアイコンを置く
//    canvas.drawBitmap( center_icon_,
//                       gauge_x_right  - center_icon_.getWidth(),
//                       gauge_y_bottom - center_icon_.getHeight(),
//                       null );

    //---------------------------------------------

    //アイコンを置く
    for( icon_cnt = 0; icon_cnt < icon_num_; icon_cnt++ )
    {
      //アイコン
      canvas.drawBitmap( icon_[icon_cnt].icon_wheel_,
                         to_canvas_x( icon_[icon_cnt].icon_wheel_, wheel_radius_, icon_[icon_cnt].wheel_azimuth_ ),
                         to_canvas_y( icon_[icon_cnt].icon_wheel_, wheel_radius_, icon_[icon_cnt].wheel_azimuth_ ),
                         null );

      if( icon_[icon_cnt].gauge_height_ <= 0 )
      {
        continue;
      }
      gauge_height = constrain( icon_[icon_cnt].gauge_height_,
                                kGAUGE_HEIGHT_MAX,
                                kGAUGE_HEIGHT_MIN );

      if( icon_[icon_cnt].icon_gauge_ != null )
      {
        canvas_x = center_x_ + 0 - ( icon_[icon_cnt].icon_gauge_.getWidth() / 2 );
        canvas_y = gauge_y_bottom -
                   (int)( ( gauge_height / kGAUGE_HEIGHT_MAX ) * ( gauge_y_bottom - gauge_y_top ) );

        canvas.drawBitmap( icon_[icon_cnt].icon_gauge_, canvas_x, canvas_y, null );
      }
    }
  }
  //--------------------------------------------------------------
  public double toDegreeMath( double degree_azimuth )
  {
    //方位角を数学の角度に変換
    //-- 方位角 : 時計の0時を起点に、時計回り
    //-- 数学　  : 時計の3時を起点に、反時計回り
    return 90 + ( 360 - degree_azimuth ) % 360;
  }
  public double toDegreeArc( double degree_azimuth )
  {
    //方位角を円弧の角度に変換
    //-- 方位角 : 時計の0時を起点に、時計回り
    //-- 円弧　  : 時計の3時を起点に、時計回り
    return ( ( degree_azimuth - 90 ) + 360 ) % 360;
  }
  //--------------------------------------------------------------
  private double constrain( double value, double max, double min )
  {
    if( value > max )
    {
      return max;
    }
    else if( value < min )
    {
      return min;
    }
    else
    {
      return value;
    }
  }
  //--------------------------------------------------------------
}
