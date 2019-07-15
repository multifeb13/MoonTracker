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
  int    center_x_;      //���S(X)
  int    center_y_;      //���S(Y)

  int    icon_num_;
  Icon[] icon_;

  //�O��------------------------------------------
  int      wheel_width_;   //��
  int      wheel_color_;   //�F
  int      wheel_radius_;  //���a

  double[] wheel_arc_azimuth_begin_ = new double[2];
  double[] wheel_arc_azimuth_sweep_ = new double[2];

  //�Q�[�W----------------------------------------
  int     gauge_width_;   //��
  
//  Bitmap  center_icon_;    //�Q�[�W�̒��S�A�C�R��

  Bitmap  gauge_icon_left_;
  Bitmap  gauge_icon_center_;
  Bitmap  gauge_icon_right_;

  //�@�萔-----------------------------------------
  private final double kGAUGE_HEIGHT_MAX = 65.0;  //�Q�[�W�̏��(�p�x)
  private final double kGAUGE_HEIGHT_MIN =  0.0;  //�Q�[�W�̉���(�p�x)

  //�F
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
    center_x_ = center_x;  //���a(X)
    center_y_ = center_y;  //���a(Y)

    icon_num_ = icon_num;
    icon_     = new Icon[icon_num];

    for( int icon_cnt = 0; icon_cnt < icon_num; icon_cnt++ )
    {
      icon_[icon_cnt] = new Icon();
    }

    //�O��------------------------------------------
    wheel_width_  = 0;  //��
    wheel_color_  = 0;  //�F
    wheel_radius_ = 0;  //���a

    for( int index = 0; index < wheel_arc_azimuth_begin_.length; index++ )
    {
      wheel_arc_azimuth_begin_[index] = 0;
    }
    for( int index = 0; index < wheel_arc_azimuth_sweep_.length; index++ )
    {
      wheel_arc_azimuth_sweep_[index] = 0;
    }

    //�Q�[�W----------------------------------------
    gauge_width_  = 0;  //��
  }

  //�O��----------------------------------------------------------
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
    wheel_width_ = wheel_width;  //�O���̕�
  }
  public void SetWheelColor( int wheel_color )
  {
    wheel_color_ = wheel_color;  //�O���̐F
  }

  public void SetWheelIconAzimuth( int index, double azimuth )
  {
    icon_[index].wheel_azimuth_ = ( ( toDegreeMath( azimuth ) + 360 ) % 360 );

    //�A�C�R������]
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

  //�Q�[�W--------------------------------------------------------
  public void SetGaugeWidth( int gauge_width )
  {
    gauge_width_ = gauge_width;  //�Q�[�W�̕�
  }
  public void SetGaugeHeight ( int index, double height )
  {
    icon_[index].gauge_height_ = height;  //���x�p
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

    //���S�̉~��`��
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

//    //��
    gauge_x = gauge_x_left;
    gauge_y = gauge_y_bottom - gauge_icon_left_.getHeight();
    canvas.drawBitmap( gauge_icon_left_,
                       gauge_x, gauge_y, null );

    //��
    gauge_x += gauge_icon_left_.getWidth();
    do
    {
      canvas.drawBitmap( gauge_icon_center_,
                         gauge_x, gauge_y, null );
      gauge_x += gauge_icon_center_.getWidth();
    } while( gauge_x < ( gauge_x_right - gauge_icon_right_.getWidth() ) );
    
    //�E
    gauge_x = gauge_x_right - gauge_icon_right_.getWidth();
    canvas.drawBitmap( gauge_icon_right_, gauge_x, gauge_y, null );
    
    //---------------------------------------------

//    //���S�̃A�C�R����u��
//    canvas.drawBitmap( center_icon_,
//                       gauge_x_right  - center_icon_.getWidth(),
//                       gauge_y_bottom - center_icon_.getHeight(),
//                       null );

    //---------------------------------------------

    //�A�C�R����u��
    for( icon_cnt = 0; icon_cnt < icon_num_; icon_cnt++ )
    {
      //�A�C�R��
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
    //���ʊp�𐔊w�̊p�x�ɕϊ�
    //-- ���ʊp : ���v��0�����N�_�ɁA���v���
    //-- ���w�@  : ���v��3�����N�_�ɁA�����v���
    return 90 + ( 360 - degree_azimuth ) % 360;
  }
  public double toDegreeArc( double degree_azimuth )
  {
    //���ʊp���~�ʂ̊p�x�ɕϊ�
    //-- ���ʊp : ���v��0�����N�_�ɁA���v���
    //-- �~�ʁ@  : ���v��3�����N�_�ɁA���v���
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
