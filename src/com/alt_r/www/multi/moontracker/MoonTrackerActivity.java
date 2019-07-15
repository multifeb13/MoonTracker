package com.alt_r.www.multi.moontracker;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

class Geometry
{
  double azimuth;  //方位角
  double height;   //高度角
}
class AstroViewable
{
  //方位角
  double azimuth_begin;  //見え始め
  double azimuth_end;    //見え終わり
  double azimuth_sweep;  //見える期間

  //時刻
  double time_begin;     //見え始め
  double time_end;       //見え終わり
}

public class MoonTrackerActivity extends Activity
                                 implements SensorEventListener, LocationListener, OnClickListener
{
  //テキストビュー ==============================================================
  private TextView   txtCurrentDateTimeTitle;
  private TextView   txtCurrentDate;
  private TextView   txtCurrentTime;

//  private TextView   txtLocateHereBody;

  private TextView[] txtRiseSet = new TextView[3];  //月の出、南中、月の入
  
  private TextView   txtCoordinateMoon;   //月　の座標
  private TextView   txtCoordinateDevice;  //端末の座標
  
  private TextView   txtMoonAge;          //月齢

  //定数 ====================================================================
  //標準の観測位置
  private final double kLOCATE_HERE_LATITUDE  =  35.6581;  //35.6544   //緯度
  private final double kLOCATE_HERE_LONGITUDE = 139.7414;  //139.7447  //経度

  private final int    kREPEAT_INTERVAL = 500;

  //更新する項目
  private final int    kUPDATE_LOCATION_VISIBLE = 1;  //現在地の表示を更新
  private final int    kUPDATE_RISE_SET         = 2;  //現在地を更新
  private final int    kUPDATE_CALENDAR         = 4;  //日付変更

  private final int    kPAUSE_UPDATE_DATE_TIME           = 8;   //日付と時刻の更新を中断
  private final int    kINDICATE_PAUSE_UPDATE_DATE_TIME  = 16;  //日付と時刻の表示状態を変更（更新停止中）
  private final int    kINDICATE_RESUME_UPDATE_DATE_TIME = 32;  //日付と時刻の表示状態を再開

  //基準となる日
  private final int    kDAY_TODAY     = 0;
  private final int    kDAY_YESTERDAY = 1;
  private final int    kDAY_TOMORROW  = 2;

  private final int    kTIME_RISE     = 0;
  private final int    kTIME_MERIDIAN = 1;
  private final int    kTIME_SET      = 2;
  
  //本体が水平とする角度（高度角）
  private final int    kDEVICE_TILT_ANGLE = 10;

  //使用するクラス ===============================================================
  private SensorManager   sensorManager;
  private Thread          thread;

  //端末を水平にするようユーザに促すダイアログ
  private ProgressDialog  dlgTilt;

  //傾きセンサ
  private Sensor          sensorOrient;
  private Handler         handler = new Handler();
  private LocationManager lm;

  //変数 =====================================================================
  private boolean   isRepeat            = true;
  private boolean   flagLocationVisible = true;  
  private boolean   flag_ignition       = false;

  private int       update_status = 0;  //更新した状態

  private double    d_prev = 0;         //直前の時刻

  //自作クラス ==================================================================
  //位置情報
  final   Location       locate_here = new Location();

  //[今日の月]にコピーする情報
  private String         infoDate;  //日付
  private CoordinateHori infoRise  = new CoordinateHori(); //月の出(時刻、方位角)
  private CoordinateHori infoSet   = new CoordinateHori(); //月の入(時刻、方位角)

  //CompassViewから参照する座標(方位角、高度角)
  static Geometry        geoMoon   = new Geometry();
  static Geometry        geoSensor = new Geometry();

  //見え始め、見え終わり(方位角、時刻)
  static AstroViewable   viewable  = new AstroViewable();
  
  //月齢
  static MoonAge moonage = new MoonAge();

  final Chronus chToday = new Chronus();

  // =========================================================================

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
 
    //センサマネージャ取得
    sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    List<Sensor> list;

    //傾きセンサ
    list = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
    if( list.size() > 0 )
    {
      sensorOrient = list.get(0);
    }

    txtCurrentDateTimeTitle = (TextView)findViewById( R.id.txtCurrentDateTimeTitle );
    txtCurrentDate = (TextView)findViewById( R.id.txtCurrentDate );
    txtCurrentTime = (TextView)findViewById( R.id.txtCurrentTime );

//    //位置
//    txtLocateHereBody = (TextView)findViewById( R.id.txtLocateHereBody );

    //月の出、南中、月の入
    txtRiseSet[kTIME_RISE    ] = (TextView)findViewById( R.id.txtMoonRise     );
    txtRiseSet[kTIME_MERIDIAN] = (TextView)findViewById( R.id.txtMoonMeridian );
    txtRiseSet[kTIME_SET     ] = (TextView)findViewById( R.id.txtMoonSet      );

    //座標
    txtCoordinateMoon   = (TextView)findViewById( R.id.txtCoordinateMoon   );  //月　の座標
    txtCoordinateDevice = (TextView)findViewById( R.id.txtCoordinateDevice );  //端末の座標
    
    txtMoonAge          = (TextView)findViewById( R.id.txtMoonAge );          //月齢

    //端末を水平にするようユーザに促すダイアログを表示
    if( flag_ignition == false )
    {
      dlgTilt = new ProgressDialog(MoonTrackerActivity.this);

      dlgTilt.setTitle("MoonTracker");
      dlgTilt.setMessage("本体を水平にしてください");
      dlgTilt.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      dlgTilt.setCancelable(true);
      dlgTilt.show();
    }
  }

//  @Override
//  public void onWindowFocusChanged(boolean hasFocus)
//  {
//    // TODO Auto-generated method stub
//    super.onWindowFocusChanged(hasFocus);
//
//    // Viewのサイズを取得
//    TextView text = (TextView)findViewById(R.id.txtLocateHere);
//    Log.d("MoonTracker", "TextView width="+text.getWidth()+", height="+text.getHeight());
//  }
  
  //アクティビティ開始時に呼ばれる
  @Override
  public void onStart()
  {
    super.onStart();

    //ロケーションマネージャの設定
    lm = (LocationManager)getSystemService( Context.LOCATION_SERVICE );
    lm.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, this );
  }

  //アクティビティ再開時に呼ばれる
  @Override
  protected void onResume()
  {
    super.onResume();
    isRepeat = true;

    final Moon[]  moon    = new Moon[3];

    moon[kDAY_TODAY    ]  = new Moon();
    moon[kDAY_YESTERDAY]  = new Moon();
    moon[kDAY_TOMORROW ]  = new Moon();

    //傾きセンサ
    if( sensorOrient != null )
    {
      sensorManager.registerListener(
          this,
          sensorOrient,
          SensorManager.SENSOR_DELAY_NORMAL );
    }
    locate_here.SetLocation( kLOCATE_HERE_LATITUDE, kLOCATE_HERE_LONGITUDE );
//    ShowLocation( flagLocationVisible, txtLocateHereBody, locate_here );  //現在位置を表示

    Runnable looper = new Runnable()
    {
      @Override
      public void run()
      {
        while( isRepeat )
        {
          try 
          {
            Thread.sleep( kREPEAT_INTERVAL );
          }
          catch (InterruptedException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

          if( ( update_status & kPAUSE_UPDATE_DATE_TIME ) != 0 )
          {
            //日付と時刻の更新を中断
          }
          else
          {
            //日付と時刻を更新
            chToday.UpdateTime();
          }

          //現在の月の方位角、高度角を表示
          handler.post(new Runnable()
          {
            public void run()
            {
              double d = 0;

              //debugの時は、下記Updateをコメントアウト
//              chToday.UpdateTime();
              d = chToday.GetElapsedTime();

              if( ( update_status & kINDICATE_PAUSE_UPDATE_DATE_TIME ) != 0 )
              {
                //日付と時刻の表示状態を変更（更新停止中）
                txtCurrentDateTimeTitle.setBackgroundColor(
                  getResources().getColor( R.color.kColorGreenDroid  ) );
                txtCurrentDateTimeTitle.setTextColor(
                  getResources().getColor( R.color.kColorBlack ) );
                
                update_status &= ~kINDICATE_PAUSE_UPDATE_DATE_TIME;
                d = chToday.GetElapsedTime();
              }
              if( ( update_status & kINDICATE_RESUME_UPDATE_DATE_TIME ) != 0 )
              {
                //日付と時刻の表示状態を変更（更新再開）
                txtCurrentDateTimeTitle.setBackgroundColor(
                  getResources().getColor( R.color.kColorBlack ) );
                txtCurrentDateTimeTitle.setTextColor(
                    getResources().getColor( R.color.kColorDefaultFont ) );

                update_status &= ~kINDICATE_RESUME_UPDATE_DATE_TIME;
//                chToday.UpdateTime();
//                d = chToday.GetElapsedTime();
                d_prev = -1;
                return;
              }
              if( moon[kDAY_TODAY].GetDateSerial() != chToday.GetDateSerial() )
              {
                update_status |= ( kUPDATE_CALENDAR | kUPDATE_RISE_SET );  //日付が変更した
                
                //クリップボードにコピーする情報を格納
                infoDate
                  = String.format( "%04d/%02d/%02d",
                                   chToday.GetYear(),
                                   chToday.GetMonth(),
                                   chToday.GetDay()   );
              }

              //-----------------------------------------------------------
              //変更した項目により、状態を更新
              if( ( update_status & kUPDATE_LOCATION_VISIBLE ) != 0 )
              {
//                //現在地の表示状態を更新
//                ShowLocation( flagLocationVisible, txtLocateHereBody, locate_here );
//                update_status &= ~kUPDATE_LOCATION_VISIBLE;  //フラグを0にする
              }
              if( ( update_status & kUPDATE_CALENDAR         ) != 0 )
              {
                //暦を更新 : day_cnt = kDAY_TODAY, kDAY_YESTERDAY, kDAY_TOMORROW
                for( int day_cnt = 0; day_cnt < 3; day_cnt++ )
                {
//                  SetCalendar ( moon[day_cnt], day_cnt );
                  SetCalendar ( moon[day_cnt], day_cnt, chToday );
                }
                update_status &= ~kUPDATE_CALENDAR;          //フラグを0にする
              }
              if( ( update_status & kUPDATE_RISE_SET         ) != 0 )
              {
                //出没を更新 
                CalcRiseSet( moon,             locate_here );
                ShowRiseSet( moon[kDAY_TODAY], txtRiseSet  );  //出没を表示

                //月齢を更新
                moonage.Update( chToday.GetYear(), chToday.GetMonth(), chToday.GetDay() );
                txtMoonAge.setText( moonage.GetAgeName( moonage.GetAge( d ) ) );

                update_status &= ~kUPDATE_RISE_SET;          //フラグを0にする
              }

              //-----------------------------------------------------------

              txtCurrentDate.setText( chToday.GetDate() + " " );
              txtCurrentTime.setText( chToday.GetTime() );
              
              if( ( d_prev != d ) || (( update_status & kPAUSE_UPDATE_DATE_TIME ) != 0 ) )
              {
                moon[kDAY_TODAY].Update( d, locate_here );
                CalcAstroViewable( moon, d, viewable );

                //月齢
                txtMoonAge.setText( moonage.GetAgeName( moonage.GetAge( d ) ) );

//                // デバッグ情報 -------------------------------
//                Log.d( "MoonTracker", "--" + Chronus.toElapsedTime( d ) +"--------------------------------------");
//                Log.d( "MoonTracker", "赤緯" + String.format( "%8.4f", moon.GetLatitude()  ) );    //赤緯
//                Log.d( "MoonTracker", "赤経" + String.format( "%8.4f", moon.GetLongitude() ) );    //赤経
//                Log.d( "MoonTracker", "視差" + String.format( "%7.5f", moon.GetParallax()  ) );    //視差
//
//                Log.d( "MoonTracker", "恒星時"   + String.format( "%8.4f", moon.GetST() ) );        //恒星時
//                Log.d( "MoonTracker", "出没高度" + String.format( "%8.4f", moon.GetAltitude()   ) );//出没高度
//                Log.d( "MoonTracker", "時角"     + String.format( "%8.4f", moon.GetTimeAngle() ) ); //時角
//                Log.d( "MoonTracker", "----------------------------------------");
//                // --------------------------------------------

                //月＠
                geoMoon.azimuth = moon[kDAY_TODAY].GetAzimuth();  //方位角
                geoMoon.height  = moon[kDAY_TODAY].GetHeight();   //高度角
                ShowCoordinate( txtCoordinateMoon, geoMoon );

                d_prev = d;  //直前の時刻を保持
              }
            }
          });
        }
      }
    };
 
    thread = new Thread(looper);
    thread.start();
  }

  //アプリの停止
  @Override
  protected void onStop()
  {
    super.onStop();

    //センサ処理の停止
    sensorManager.unregisterListener(this);
    isRepeat = false;
    
    //ロケーションマネージャの停止
    lm.removeUpdates(this);
  }

  @Override
  public void onAccuracyChanged(Sensor arg0, int arg1)
  {
  }

  @Override
  public void onSensorChanged(SensorEvent event)
  {
    if( event.sensor == sensorOrient )
    {
      //センサ更新
      geoSensor.azimuth = event.values[0];      //方位角
      geoSensor.height  = event.values[1] * -1; //高度角

      if( flag_ignition == false )
      {
        if( Math.abs(geoSensor.height) < kDEVICE_TILT_ANGLE )
        {
          flag_ignition = true;
          //端末を水平にするようユーザに促すダイアログを閉じる
          dlgTilt.dismiss();
        }
      }

      //端末の座標
      ShowCoordinate( txtCoordinateDevice, geoSensor );
    }
  }

  @Override
  public void onLocationChanged(android.location.Location location)
  {
    //位置情報を更新
    locate_here.SetLocation( location.getLatitude(), location.getLongitude() );
    update_status |= ( kUPDATE_LOCATION_VISIBLE | kUPDATE_RISE_SET );
   }

  @Override
  public void onProviderDisabled(String arg0)
  {
  }
  @Override
  public void onProviderEnabled(String arg0)
  {
  }
  @Override
  public void onStatusChanged(String arg0, int arg1, Bundle arg2)
  {
  }

  // クリック関連--------------------------------------------------
  @Override
  public void onClick( View view )
  {
    if( view.getId() == R.id.imgResumeUpdateDateTime )
    {
      //日付と時刻の更新を再開
      update_status &= ~kPAUSE_UPDATE_DATE_TIME;
      update_status |= kINDICATE_RESUME_UPDATE_DATE_TIME | kUPDATE_CALENDAR | kUPDATE_RISE_SET;
    }
  }
  public void onClickMenu( View view )
  {
    String strAzimuthNameRise = "";
    String strAzimuthNameSet  = "";

    AlertDialog.Builder listDlg = new AlertDialog.Builder(this);
    
    if( view.getId() == R.id.imageMenuDate )
    {
      // リスト表示用のアラートダイアログ
      final CharSequence[] items = {"日付を変更する", "時刻を変更する", "元に戻す"};

      listDlg.setTitle("日付/時刻を変更");
      listDlg.setItems(
          items,
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              // リスト選択時の処理
              // which は、選択されたアイテムのインデックス
              switch( which )
              {
                case 0: //日付を選択
                  try{
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                        com.alt_r.www.multi.moontracker.MoonTrackerActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                          public void onDateSet( DatePicker view, int year, int monthOfYear, int dayOfMonth)
                          {
                            // 選択された日付を設定
                            chToday.SetYear( year );
                            chToday.SetMonthOfYear( monthOfYear );
                            chToday.SetDay( dayOfMonth );

                            update_status |= kPAUSE_UPDATE_DATE_TIME | kINDICATE_PAUSE_UPDATE_DATE_TIME | kUPDATE_CALENDAR | kUPDATE_RISE_SET;
                          }
                        },
                        chToday.GetYear(),
                        chToday.GetMonthOfYear(),
                        chToday.GetDay()
                    );
                    datePickerDialog.setTitle(getString(R.string.strDialogTitleSetDate));
                    datePickerDialog.show();
                  }
                  catch( Exception e )
                  {
                    Log.d( "MoonTracker", "DatePickerDialog Error" );
                  }
                  break;

                case 1: //時刻を選択
                  try
                  {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                        com.alt_r.www.multi.moontracker.MoonTrackerActivity.this,
                        new TimePickerDialog.OnTimeSetListener()
                        {
                          public void onTimeSet( TimePicker view, int hourOfDay, int minutes )
                          {
                            //選択された時刻を設定
                            chToday.SetHour  ( hourOfDay );
                            chToday.SetMinute( minutes   );
                            chToday.SetSecond( 0 );

                            update_status |= kPAUSE_UPDATE_DATE_TIME | kINDICATE_PAUSE_UPDATE_DATE_TIME | kUPDATE_CALENDAR | kUPDATE_RISE_SET;
                          }
                        },
                        chToday.GetHour(),
                        chToday.GetMinute(),
                        true
                    );
                    timePickerDialog.setTitle(getString(R.string.strDialogTitleSetTime));
                    timePickerDialog.show();                    
                  }
                  catch (Exception e)
                  {
                    Log.d( "MoonTracker", "TimePickerDialog Error" );
                  }
                  break;

                case 2:  //日付と時刻の更新を再開
                  update_status &= ~kPAUSE_UPDATE_DATE_TIME;
                  update_status |= kINDICATE_RESUME_UPDATE_DATE_TIME | kUPDATE_CALENDAR | kUPDATE_RISE_SET;

                default:
                  break;
              }
            }
          });
      // 表示
      listDlg.create().show();
    }
    else if( view.getId() == R.id.imageMenuCamera )
    {
      //カメラ（アプリ）を起動
      try
      {
        Intent intent = new Intent();
        intent.setAction( MediaStore.ACTION_IMAGE_CAPTURE );
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        startActivityForResult(intent, 0); //　ここの0の番号で呼び出し元と呼び出し先で対象かどうか判断する
      }
      catch (Exception e)
      {
        Log.d( "MoonTracker", "Intent MediaStore.ACTION_IMAGE_CAPTURE Error" );
      }
    }
    else if( view.getId() == R.id.imageMenuPlace )
    {
//      //「位置」 : 表示状態を変更
//      flagLocationVisible = !flagLocationVisible;
//      update_status |= kUPDATE_LOCATION_VISIBLE;

      // リスト表示用のアラートダイアログ
      final CharSequence[] items = {"地図で表示"};

      listDlg.setTitle("現在地");
      listDlg.setItems(
          items,
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              // リスト選択時の処理
              // which は、選択されたアイテムのインデックス
              switch( which )
              {
                case 0: //現在地を表示
                  try
                  {
                    Intent intent = new Intent();  
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                    intent.setData(Uri.parse( locate_here.GetMapUrl(true) ));

                    startActivity(intent);  
                  }
                  catch( Exception e )
                  {
                    Toast.makeText( getApplication(),
                        getString( R.string.strToastGoogleMapNotFound ),
                        Toast.LENGTH_SHORT).show();
                  }
              }
            }
          });
      // 表示
      listDlg.create().show();      
    }
    else if( view.getId() == R.id.imageMenuShare )
    {
      //「月の出」「南中」「月の入」 : [今日の月]をクリップボードにコピー
      strAzimuthNameRise
        = CoordinateHori.toAzimuthName( infoRise.GetAzimuth() );
      strAzimuthNameRise.replaceAll("[\\s　]", "");

      strAzimuthNameSet
        = CoordinateHori.toAzimuthName( infoSet.GetAzimuth()  );
      strAzimuthNameSet.replaceAll("[\\s　]", "");

      final String strinfo
        = String.format( "[今日の月]%s 月の出 %s(%sの空) - 月の入 %s(%sの空) 月齢 %s",
                         infoDate,
                         Chronus.toElapsedTime( infoRise.GetD() ),
                         strAzimuthNameRise,
                         Chronus.toElapsedTime( infoSet.GetD()  ),
                         strAzimuthNameSet,
                         moonage.GetAgeName( moonage.GetAge( 0.5 ) ) );

      // リスト表示用のアラートダイアログ
      final CharSequence[] items = {"他のアプリ", "クリップボード"};

      listDlg.setTitle("[今日の月]を共有");
      listDlg.setItems(
          items,
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              // リスト選択時の処理
              // which は、選択されたアイテムのインデックス
              switch( which )
              {
                case 0: //他のアプリ
                  try
                  {
                    Intent intent = new Intent();
                    intent.setAction( Intent.ACTION_SEND );
                    intent.setType( "text/plain" );
                    intent.putExtra( Intent.EXTRA_TEXT, strinfo );
                    startActivity(intent);
                  }
                  catch (Exception e)
                  {
                    Log.d( "MoonTracker", "Intent Intent.ACTION_SEND Error" );
                  }
                  break;
                case 1: //クリップボード
                  ClipboardManager clipboard
                    = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
                  clipboard.setText( strinfo );

                  Toast.makeText( getApplication(),
                                  getString(R.string.strToastCopyTodaysMoonToClipboard),
                                  Toast.LENGTH_SHORT).show();
                  break;

                default:
                  break;
                  
              }
            }
          });
      // 表示
      listDlg.create().show();
    }
  }

  //　Utility ------------------------------------------------
//  private void SetCalendar( Astronomy astro, int index_day )
//  {
//    Chronus  chDate = new Chronus();
//    Location locRed = new Location();
//
//    int index_day_begin = 0;
//
//    //当日(引数の指定日)から、計算開始日を選択
//    switch( index_day )
//    {
//      case kDAY_YESTERDAY: index_day_begin = -1; break;
//      case kDAY_TODAY:     index_day_begin =  0; break;
//      case kDAY_TOMORROW:  index_day_begin =  1; break;
//    }
//
//    //UpdateTime()を何度も呼ぶと、その間に日付が変わる可能性があるので
//    //1回しか呼ばないように処理する
//    chDate.UpdateTime();
//
//    chDate.AddDay( index_day_begin - 1 );  //前日(引数の指定日-1)から開始する
//    for( int day_cnt = 0; day_cnt < Astronomy.DAYS_OF_CALENDAR; day_cnt++ )
//    {
//      //赤経、赤緯を計算し、設定する
//      astro.CalcLocation( chDate.CalcT( 0, 0 ), locRed );
//      astro.SetCalendar ( day_cnt,              locRed );
//
//      if( day_cnt == ( Astronomy.DAYS_OF_CALENDAR / 2) )
//      {
//        //当日(引数の指定日)の処理
//        //--グリニジ視恒星時
//        astro.CalcGST( chDate.GetHour() - 9, chDate.GetJD( 0, 0, 0 ) );
//        astro.SetDateSerial( chDate.GetDateSerial() );
//      }
//      chDate.AddDay( 1 );  //計算する日を更新
//
//      //debug
//      //chYesterday.SetTime( 1999, 11, 13, 0, 0, 0 );
//      //chToday.SetTime    ( 1999, 11, 14, 0, 0, 0 );
//      //chTomorrow.SetTime ( 1999, 11, 15, 0, 0, 0 );
//    }
//  }
  private void SetCalendar( Astronomy astro, int index_day, Chronus chCurrentDate )
  {
    Chronus  chLocalDate = new Chronus();
    Location locRed = new Location();

    int index_day_begin = 0;

    //当日(引数の指定日)から、計算開始日を選択
    switch( index_day )
    {
      case kDAY_YESTERDAY: index_day_begin = -1; break;
      case kDAY_TODAY:     index_day_begin =  0; break;
      case kDAY_TOMORROW:  index_day_begin =  1; break;
    }

    //UpdateTime()を何度も呼ぶと、その間に日付が変わる可能性があるので
    //1回しか呼ばないように処理する
    chLocalDate.UpdateTime();
    chLocalDate.SetTime
      ( chCurrentDate.GetYear(), chCurrentDate.GetMonth(),  chCurrentDate.GetDay(),
        chCurrentDate.GetHour(), chCurrentDate.GetMinute(), chCurrentDate.GetSecond() );
    
    chLocalDate.AddDay( index_day_begin - 1 );  //前日(引数の指定日-1)から開始する
    for( int day_cnt = 0; day_cnt < Astronomy.DAYS_OF_CALENDAR; day_cnt++ )
    {
      //赤経、赤緯を計算し、設定する
      astro.CalcLocation( chLocalDate.CalcT( 0, 0 ), locRed );
      astro.SetCalendar ( day_cnt,              locRed );

      if( day_cnt == ( Astronomy.DAYS_OF_CALENDAR / 2) )
      {
        //当日(引数の指定日)の処理
        //--グリニジ視恒星時
        astro.CalcGST( chLocalDate.GetHour() - 9, chLocalDate.GetJD( 0, 0, 0 ) );
        astro.SetDateSerial( chLocalDate.GetDateSerial() );
      }
      chLocalDate.AddDay( 1 );  //計算する日を更新

      //debug
      //chYesterday.SetTime( 1999, 11, 13, 0, 0, 0 );
      //chToday.SetTime    ( 1999, 11, 14, 0, 0, 0 );
      //chTomorrow.SetTime ( 1999, 11, 15, 0, 0, 0 );
    }
  }
 
  public void CalcRiseSet( Astronomy[] astro, Location locate_here )
  {
    //出没の計算
    //day_cnt  : kDAY_TODAY, kDAY_YESTERDAY, kDAY_TOMORROW
    //time_cnt : kTIME_RISE, kTIME_MERIDIAN, kTIME_SET
    for( int day_cnt = 0; day_cnt < Astronomy.DAYS_OF_CALENDAR; day_cnt++ )
    {
      for( int time_cnt = 0; time_cnt < 3; time_cnt++ )
      {
        astro[day_cnt].CalcRiseSet( time_cnt, locate_here );
      }
    }
  }
  public void ShowRiseSet( Astronomy astro, TextView[] txtRiseSet )
  {
    //出没関連
    //[0] = 出 : [1] = 南中 : [2] = 入
    double d = 0;
    double A = 0;

    String strAngleFormat = "";  //方位角
    String strAzimuthName = "";  //方位角の名称

    for( int time_cnt = 0; time_cnt < 3; time_cnt++ )
    {
      d = astro.GetRiseSetTime   ( time_cnt );  //時刻を取得

      if( IsInRange( d, 1.0, 0.0 ) )
      {
        //対象の出、南中、入が今日中にある時は、通常の表示にする
        A = astro.GetRiseSetAzimuth( time_cnt );  //方位角を取得

        strAngleFormat = CoordinateHori.toAngleFormat( A );
        strAzimuthName = AlignSpace( CoordinateHori.toAzimuthName( A ), 3 );
      }
      else
      {
        //対象の出、南中、入が今日中に無い時は、無効の表示にする
        strAngleFormat = "000.00";
        strAzimuthName = "　　　";
      }

      txtRiseSet[time_cnt].setText(
          String.format( "方位角:%s(%s) / 時刻 %s",
                         strAngleFormat,
                         strAzimuthName,
                         Chronus.toElapsedTime( d ) ) );
    }

    //クリップボードにコピーする情報を格納
    //月の出
    infoRise.SetD      ( astro.GetRiseSetTime   ( astro.TIME_RISE ) );  //時刻
    infoRise.SetAzimuth( astro.GetRiseSetAzimuth( astro.TIME_RISE ) );  //方位角

    //月の入
    infoSet.SetD       ( astro.GetRiseSetTime   ( astro.TIME_SET  ) );  //時刻
    infoSet.SetAzimuth ( astro.GetRiseSetAzimuth( astro.TIME_SET  ) );  //方位角
  }
  private void CalcAstroViewable( Astronomy astro[], double d, AstroViewable viewable )
  {
    //対象の惑星を見ることが出来る方位角、時間の範囲を計算
    //astro[]
    //  [kDAY_TODAY]     : 今日
    //  [kDAY_YESTERDAY] : 昨日
    //  [DAY_TOMRROW]    : 明日
    double d_today_rise = astro[kDAY_TODAY].GetRiseSetTime( kTIME_RISE );
    double d_today_set  = astro[kDAY_TODAY].GetRiseSetTime( kTIME_SET  );

    int index_day_rise = 0;
    int index_day_set  = 0;

    //月の出、入がある日を調べる
    /*
        出 ; d_today_rise が 1.0 ～ 0.0 の範囲内 -> 今日
                                          範囲外 -> 昨日

        入 ; d_today_rise が 1.0 ～ 0.0 の範囲内 -> 今日
                                          範囲外 -> 明日
    */
    index_day_rise = IsInRange( d_today_rise, 1.0, 0.0 ) ? kDAY_TODAY : kDAY_YESTERDAY;
    index_day_set  = IsInRange( d_today_set,  1.0, 0.0 ) ? kDAY_TODAY : kDAY_TOMORROW;

    //月の入より出が遅い時の補正
    __timeline_is_rise_to_set:
    if( ( index_day_rise == kDAY_TODAY) && ( index_day_set == kDAY_TODAY ) )
    {
      //当日が、月の出 -> 月の入の順番の場合は例外処理をしない
      if( d_today_rise <= d_today_set ) break __timeline_is_rise_to_set;

      if( d_today_set > d )
      {
        index_day_rise = kDAY_YESTERDAY;
      }
      else
      {
        index_day_set  = kDAY_TOMORROW;
      }
    }

    viewable.azimuth_begin
      = astro[index_day_rise].GetRiseSetAzimuth( kTIME_RISE );
    viewable.azimuth_end
      = astro[index_day_set ].GetRiseSetAzimuth( kTIME_SET  );
    viewable.azimuth_sweep
      = Math.abs( viewable.azimuth_end - viewable.azimuth_begin );
  }
  private boolean IsInRange( double value, double max, double min )
  {
    return ( max >= value ) && ( value >= min );
  }
  
  public void ShowLocation( boolean flagLocationVisible,
                             TextView txtview,
                             Location locate_here )
  {
    //現在の位置
    String strLongitude = "";
    String strLatitude  = "";

    if( flagLocationVisible )
    {
      strLongitude
        = Location.toLocationFormat( locate_here.GetLongitude() );
      strLatitude
        = Location.toLocationFormat( locate_here.GetLatitude () );
    }
    else
    {
      strLongitude = "***.****";
      strLatitude  = "***.****";
    }

    //"東経:%s / 北緯:%s"
    txtview.setText(
        getString( R.string.strLayoutLocateHereBody,
                   strLongitude,
                   strLatitude ) );
  }
  
  private void ShowCoordinate( TextView txtview,
                               Geometry geometry )
  {
    txtview.setText(
        String.format( "方位角:%s(%s) / 高度角:%s",
                       CoordinateHori.toAngleFormat( geometry.azimuth    ),
                       AlignSpace( CoordinateHori.toAzimuthName( geometry.azimuth, 4 ), 3 ),
                       CoordinateHori.toAngleFormat( geometry.height     ) ) );
  }

  private String AlignSpace( String strOriginal,
                             int    lengthOfAll )
  {
    //指定された文字数(lengthOfAll)になるまで
    //入力文字(strOriginal)に全角スペースを付加した文字列を返す
    String strAligned = strOriginal;

    for( int index = strOriginal.length(); index < lengthOfAll; index++ )
    {
      strAligned = strAligned + "　";  //全角スペースを付加
    }
    
    return strAligned;
  }
}
