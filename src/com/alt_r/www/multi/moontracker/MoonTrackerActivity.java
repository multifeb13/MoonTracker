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
  double azimuth;  //���ʊp
  double height;   //���x�p
}
class AstroViewable
{
  //���ʊp
  double azimuth_begin;  //�����n��
  double azimuth_end;    //�����I���
  double azimuth_sweep;  //���������

  //����
  double time_begin;     //�����n��
  double time_end;       //�����I���
}

public class MoonTrackerActivity extends Activity
                                 implements SensorEventListener, LocationListener, OnClickListener
{
  //�e�L�X�g�r���[ ==============================================================
  private TextView   txtCurrentDateTimeTitle;
  private TextView   txtCurrentDate;
  private TextView   txtCurrentTime;

//  private TextView   txtLocateHereBody;

  private TextView[] txtRiseSet = new TextView[3];  //���̏o�A�쒆�A���̓�
  
  private TextView   txtCoordinateMoon;   //���@�̍��W
  private TextView   txtCoordinateDevice;  //�[���̍��W
  
  private TextView   txtMoonAge;          //����

  //�萔 ====================================================================
  //�W���̊ϑ��ʒu
  private final double kLOCATE_HERE_LATITUDE  =  35.6581;  //35.6544   //�ܓx
  private final double kLOCATE_HERE_LONGITUDE = 139.7414;  //139.7447  //�o�x

  private final int    kREPEAT_INTERVAL = 500;

  //�X�V���鍀��
  private final int    kUPDATE_LOCATION_VISIBLE = 1;  //���ݒn�̕\�����X�V
  private final int    kUPDATE_RISE_SET         = 2;  //���ݒn���X�V
  private final int    kUPDATE_CALENDAR         = 4;  //���t�ύX

  private final int    kPAUSE_UPDATE_DATE_TIME           = 8;   //���t�Ǝ����̍X�V�𒆒f
  private final int    kINDICATE_PAUSE_UPDATE_DATE_TIME  = 16;  //���t�Ǝ����̕\����Ԃ�ύX�i�X�V��~���j
  private final int    kINDICATE_RESUME_UPDATE_DATE_TIME = 32;  //���t�Ǝ����̕\����Ԃ��ĊJ

  //��ƂȂ��
  private final int    kDAY_TODAY     = 0;
  private final int    kDAY_YESTERDAY = 1;
  private final int    kDAY_TOMORROW  = 2;

  private final int    kTIME_RISE     = 0;
  private final int    kTIME_MERIDIAN = 1;
  private final int    kTIME_SET      = 2;
  
  //�{�̂������Ƃ���p�x�i���x�p�j
  private final int    kDEVICE_TILT_ANGLE = 10;

  //�g�p����N���X ===============================================================
  private SensorManager   sensorManager;
  private Thread          thread;

  //�[���𐅕��ɂ���悤���[�U�ɑ����_�C�A���O
  private ProgressDialog  dlgTilt;

  //�X���Z���T
  private Sensor          sensorOrient;
  private Handler         handler = new Handler();
  private LocationManager lm;

  //�ϐ� =====================================================================
  private boolean   isRepeat            = true;
  private boolean   flagLocationVisible = true;  
  private boolean   flag_ignition       = false;

  private int       update_status = 0;  //�X�V�������

  private double    d_prev = 0;         //���O�̎���

  //����N���X ==================================================================
  //�ʒu���
  final   Location       locate_here = new Location();

  //[�����̌�]�ɃR�s�[������
  private String         infoDate;  //���t
  private CoordinateHori infoRise  = new CoordinateHori(); //���̏o(�����A���ʊp)
  private CoordinateHori infoSet   = new CoordinateHori(); //���̓�(�����A���ʊp)

  //CompassView����Q�Ƃ�����W(���ʊp�A���x�p)
  static Geometry        geoMoon   = new Geometry();
  static Geometry        geoSensor = new Geometry();

  //�����n�߁A�����I���(���ʊp�A����)
  static AstroViewable   viewable  = new AstroViewable();
  
  //����
  static MoonAge moonage = new MoonAge();

  final Chronus chToday = new Chronus();

  // =========================================================================

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
 
    //�Z���T�}�l�[�W���擾
    sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    List<Sensor> list;

    //�X���Z���T
    list = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
    if( list.size() > 0 )
    {
      sensorOrient = list.get(0);
    }

    txtCurrentDateTimeTitle = (TextView)findViewById( R.id.txtCurrentDateTimeTitle );
    txtCurrentDate = (TextView)findViewById( R.id.txtCurrentDate );
    txtCurrentTime = (TextView)findViewById( R.id.txtCurrentTime );

//    //�ʒu
//    txtLocateHereBody = (TextView)findViewById( R.id.txtLocateHereBody );

    //���̏o�A�쒆�A���̓�
    txtRiseSet[kTIME_RISE    ] = (TextView)findViewById( R.id.txtMoonRise     );
    txtRiseSet[kTIME_MERIDIAN] = (TextView)findViewById( R.id.txtMoonMeridian );
    txtRiseSet[kTIME_SET     ] = (TextView)findViewById( R.id.txtMoonSet      );

    //���W
    txtCoordinateMoon   = (TextView)findViewById( R.id.txtCoordinateMoon   );  //���@�̍��W
    txtCoordinateDevice = (TextView)findViewById( R.id.txtCoordinateDevice );  //�[���̍��W
    
    txtMoonAge          = (TextView)findViewById( R.id.txtMoonAge );          //����

    //�[���𐅕��ɂ���悤���[�U�ɑ����_�C�A���O��\��
    if( flag_ignition == false )
    {
      dlgTilt = new ProgressDialog(MoonTrackerActivity.this);

      dlgTilt.setTitle("MoonTracker");
      dlgTilt.setMessage("�{�̂𐅕��ɂ��Ă�������");
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
//    // View�̃T�C�Y���擾
//    TextView text = (TextView)findViewById(R.id.txtLocateHere);
//    Log.d("MoonTracker", "TextView width="+text.getWidth()+", height="+text.getHeight());
//  }
  
  //�A�N�e�B�r�e�B�J�n���ɌĂ΂��
  @Override
  public void onStart()
  {
    super.onStart();

    //���P�[�V�����}�l�[�W���̐ݒ�
    lm = (LocationManager)getSystemService( Context.LOCATION_SERVICE );
    lm.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, this );
  }

  //�A�N�e�B�r�e�B�ĊJ���ɌĂ΂��
  @Override
  protected void onResume()
  {
    super.onResume();
    isRepeat = true;

    final Moon[]  moon    = new Moon[3];

    moon[kDAY_TODAY    ]  = new Moon();
    moon[kDAY_YESTERDAY]  = new Moon();
    moon[kDAY_TOMORROW ]  = new Moon();

    //�X���Z���T
    if( sensorOrient != null )
    {
      sensorManager.registerListener(
          this,
          sensorOrient,
          SensorManager.SENSOR_DELAY_NORMAL );
    }
    locate_here.SetLocation( kLOCATE_HERE_LATITUDE, kLOCATE_HERE_LONGITUDE );
//    ShowLocation( flagLocationVisible, txtLocateHereBody, locate_here );  //���݈ʒu��\��

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
            //���t�Ǝ����̍X�V�𒆒f
          }
          else
          {
            //���t�Ǝ������X�V
            chToday.UpdateTime();
          }

          //���݂̌��̕��ʊp�A���x�p��\��
          handler.post(new Runnable()
          {
            public void run()
            {
              double d = 0;

              //debug�̎��́A���LUpdate���R�����g�A�E�g
//              chToday.UpdateTime();
              d = chToday.GetElapsedTime();

              if( ( update_status & kINDICATE_PAUSE_UPDATE_DATE_TIME ) != 0 )
              {
                //���t�Ǝ����̕\����Ԃ�ύX�i�X�V��~���j
                txtCurrentDateTimeTitle.setBackgroundColor(
                  getResources().getColor( R.color.kColorGreenDroid  ) );
                txtCurrentDateTimeTitle.setTextColor(
                  getResources().getColor( R.color.kColorBlack ) );
                
                update_status &= ~kINDICATE_PAUSE_UPDATE_DATE_TIME;
                d = chToday.GetElapsedTime();
              }
              if( ( update_status & kINDICATE_RESUME_UPDATE_DATE_TIME ) != 0 )
              {
                //���t�Ǝ����̕\����Ԃ�ύX�i�X�V�ĊJ�j
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
                update_status |= ( kUPDATE_CALENDAR | kUPDATE_RISE_SET );  //���t���ύX����
                
                //�N���b�v�{�[�h�ɃR�s�[��������i�[
                infoDate
                  = String.format( "%04d/%02d/%02d",
                                   chToday.GetYear(),
                                   chToday.GetMonth(),
                                   chToday.GetDay()   );
              }

              //-----------------------------------------------------------
              //�ύX�������ڂɂ��A��Ԃ��X�V
              if( ( update_status & kUPDATE_LOCATION_VISIBLE ) != 0 )
              {
//                //���ݒn�̕\����Ԃ��X�V
//                ShowLocation( flagLocationVisible, txtLocateHereBody, locate_here );
//                update_status &= ~kUPDATE_LOCATION_VISIBLE;  //�t���O��0�ɂ���
              }
              if( ( update_status & kUPDATE_CALENDAR         ) != 0 )
              {
                //����X�V : day_cnt = kDAY_TODAY, kDAY_YESTERDAY, kDAY_TOMORROW
                for( int day_cnt = 0; day_cnt < 3; day_cnt++ )
                {
//                  SetCalendar ( moon[day_cnt], day_cnt );
                  SetCalendar ( moon[day_cnt], day_cnt, chToday );
                }
                update_status &= ~kUPDATE_CALENDAR;          //�t���O��0�ɂ���
              }
              if( ( update_status & kUPDATE_RISE_SET         ) != 0 )
              {
                //�o�v���X�V 
                CalcRiseSet( moon,             locate_here );
                ShowRiseSet( moon[kDAY_TODAY], txtRiseSet  );  //�o�v��\��

                //������X�V
                moonage.Update( chToday.GetYear(), chToday.GetMonth(), chToday.GetDay() );
                txtMoonAge.setText( moonage.GetAgeName( moonage.GetAge( d ) ) );

                update_status &= ~kUPDATE_RISE_SET;          //�t���O��0�ɂ���
              }

              //-----------------------------------------------------------

              txtCurrentDate.setText( chToday.GetDate() + " " );
              txtCurrentTime.setText( chToday.GetTime() );
              
              if( ( d_prev != d ) || (( update_status & kPAUSE_UPDATE_DATE_TIME ) != 0 ) )
              {
                moon[kDAY_TODAY].Update( d, locate_here );
                CalcAstroViewable( moon, d, viewable );

                //����
                txtMoonAge.setText( moonage.GetAgeName( moonage.GetAge( d ) ) );

//                // �f�o�b�O��� -------------------------------
//                Log.d( "MoonTracker", "--" + Chronus.toElapsedTime( d ) +"--------------------------------------");
//                Log.d( "MoonTracker", "�Ԉ�" + String.format( "%8.4f", moon.GetLatitude()  ) );    //�Ԉ�
//                Log.d( "MoonTracker", "�Ԍo" + String.format( "%8.4f", moon.GetLongitude() ) );    //�Ԍo
//                Log.d( "MoonTracker", "����" + String.format( "%7.5f", moon.GetParallax()  ) );    //����
//
//                Log.d( "MoonTracker", "�P����"   + String.format( "%8.4f", moon.GetST() ) );        //�P����
//                Log.d( "MoonTracker", "�o�v���x" + String.format( "%8.4f", moon.GetAltitude()   ) );//�o�v���x
//                Log.d( "MoonTracker", "���p"     + String.format( "%8.4f", moon.GetTimeAngle() ) ); //���p
//                Log.d( "MoonTracker", "----------------------------------------");
//                // --------------------------------------------

                //����
                geoMoon.azimuth = moon[kDAY_TODAY].GetAzimuth();  //���ʊp
                geoMoon.height  = moon[kDAY_TODAY].GetHeight();   //���x�p
                ShowCoordinate( txtCoordinateMoon, geoMoon );

                d_prev = d;  //���O�̎�����ێ�
              }
            }
          });
        }
      }
    };
 
    thread = new Thread(looper);
    thread.start();
  }

  //�A�v���̒�~
  @Override
  protected void onStop()
  {
    super.onStop();

    //�Z���T�����̒�~
    sensorManager.unregisterListener(this);
    isRepeat = false;
    
    //���P�[�V�����}�l�[�W���̒�~
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
      //�Z���T�X�V
      geoSensor.azimuth = event.values[0];      //���ʊp
      geoSensor.height  = event.values[1] * -1; //���x�p

      if( flag_ignition == false )
      {
        if( Math.abs(geoSensor.height) < kDEVICE_TILT_ANGLE )
        {
          flag_ignition = true;
          //�[���𐅕��ɂ���悤���[�U�ɑ����_�C�A���O�����
          dlgTilt.dismiss();
        }
      }

      //�[���̍��W
      ShowCoordinate( txtCoordinateDevice, geoSensor );
    }
  }

  @Override
  public void onLocationChanged(android.location.Location location)
  {
    //�ʒu�����X�V
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

  // �N���b�N�֘A--------------------------------------------------
  @Override
  public void onClick( View view )
  {
    if( view.getId() == R.id.imgResumeUpdateDateTime )
    {
      //���t�Ǝ����̍X�V���ĊJ
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
      // ���X�g�\���p�̃A���[�g�_�C�A���O
      final CharSequence[] items = {"���t��ύX����", "������ύX����", "���ɖ߂�"};

      listDlg.setTitle("���t/������ύX");
      listDlg.setItems(
          items,
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              // ���X�g�I�����̏���
              // which �́A�I�����ꂽ�A�C�e���̃C���f�b�N�X
              switch( which )
              {
                case 0: //���t��I��
                  try{
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                        com.alt_r.www.multi.moontracker.MoonTrackerActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                          public void onDateSet( DatePicker view, int year, int monthOfYear, int dayOfMonth)
                          {
                            // �I�����ꂽ���t��ݒ�
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

                case 1: //������I��
                  try
                  {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                        com.alt_r.www.multi.moontracker.MoonTrackerActivity.this,
                        new TimePickerDialog.OnTimeSetListener()
                        {
                          public void onTimeSet( TimePicker view, int hourOfDay, int minutes )
                          {
                            //�I�����ꂽ������ݒ�
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

                case 2:  //���t�Ǝ����̍X�V���ĊJ
                  update_status &= ~kPAUSE_UPDATE_DATE_TIME;
                  update_status |= kINDICATE_RESUME_UPDATE_DATE_TIME | kUPDATE_CALENDAR | kUPDATE_RISE_SET;

                default:
                  break;
              }
            }
          });
      // �\��
      listDlg.create().show();
    }
    else if( view.getId() == R.id.imageMenuCamera )
    {
      //�J�����i�A�v���j���N��
      try
      {
        Intent intent = new Intent();
        intent.setAction( MediaStore.ACTION_IMAGE_CAPTURE );
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        startActivityForResult(intent, 0); //�@������0�̔ԍ��ŌĂяo�����ƌĂяo����őΏۂ��ǂ������f����
      }
      catch (Exception e)
      {
        Log.d( "MoonTracker", "Intent MediaStore.ACTION_IMAGE_CAPTURE Error" );
      }
    }
    else if( view.getId() == R.id.imageMenuPlace )
    {
//      //�u�ʒu�v : �\����Ԃ�ύX
//      flagLocationVisible = !flagLocationVisible;
//      update_status |= kUPDATE_LOCATION_VISIBLE;

      // ���X�g�\���p�̃A���[�g�_�C�A���O
      final CharSequence[] items = {"�n�}�ŕ\��"};

      listDlg.setTitle("���ݒn");
      listDlg.setItems(
          items,
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              // ���X�g�I�����̏���
              // which �́A�I�����ꂽ�A�C�e���̃C���f�b�N�X
              switch( which )
              {
                case 0: //���ݒn��\��
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
      // �\��
      listDlg.create().show();      
    }
    else if( view.getId() == R.id.imageMenuShare )
    {
      //�u���̏o�v�u�쒆�v�u���̓��v : [�����̌�]���N���b�v�{�[�h�ɃR�s�[
      strAzimuthNameRise
        = CoordinateHori.toAzimuthName( infoRise.GetAzimuth() );
      strAzimuthNameRise.replaceAll("[\\s�@]", "");

      strAzimuthNameSet
        = CoordinateHori.toAzimuthName( infoSet.GetAzimuth()  );
      strAzimuthNameSet.replaceAll("[\\s�@]", "");

      final String strinfo
        = String.format( "[�����̌�]%s ���̏o %s(%s�̋�) - ���̓� %s(%s�̋�) ���� %s",
                         infoDate,
                         Chronus.toElapsedTime( infoRise.GetD() ),
                         strAzimuthNameRise,
                         Chronus.toElapsedTime( infoSet.GetD()  ),
                         strAzimuthNameSet,
                         moonage.GetAgeName( moonage.GetAge( 0.5 ) ) );

      // ���X�g�\���p�̃A���[�g�_�C�A���O
      final CharSequence[] items = {"���̃A�v��", "�N���b�v�{�[�h"};

      listDlg.setTitle("[�����̌�]�����L");
      listDlg.setItems(
          items,
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              // ���X�g�I�����̏���
              // which �́A�I�����ꂽ�A�C�e���̃C���f�b�N�X
              switch( which )
              {
                case 0: //���̃A�v��
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
                case 1: //�N���b�v�{�[�h
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
      // �\��
      listDlg.create().show();
    }
  }

  //�@Utility ------------------------------------------------
//  private void SetCalendar( Astronomy astro, int index_day )
//  {
//    Chronus  chDate = new Chronus();
//    Location locRed = new Location();
//
//    int index_day_begin = 0;
//
//    //����(�����̎w���)����A�v�Z�J�n����I��
//    switch( index_day )
//    {
//      case kDAY_YESTERDAY: index_day_begin = -1; break;
//      case kDAY_TODAY:     index_day_begin =  0; break;
//      case kDAY_TOMORROW:  index_day_begin =  1; break;
//    }
//
//    //UpdateTime()�����x���ĂԂƁA���̊Ԃɓ��t���ς��\��������̂�
//    //1�񂵂��Ă΂Ȃ��悤�ɏ�������
//    chDate.UpdateTime();
//
//    chDate.AddDay( index_day_begin - 1 );  //�O��(�����̎w���-1)����J�n����
//    for( int day_cnt = 0; day_cnt < Astronomy.DAYS_OF_CALENDAR; day_cnt++ )
//    {
//      //�Ԍo�A�Ԉ܂��v�Z���A�ݒ肷��
//      astro.CalcLocation( chDate.CalcT( 0, 0 ), locRed );
//      astro.SetCalendar ( day_cnt,              locRed );
//
//      if( day_cnt == ( Astronomy.DAYS_OF_CALENDAR / 2) )
//      {
//        //����(�����̎w���)�̏���
//        //--�O���j�W���P����
//        astro.CalcGST( chDate.GetHour() - 9, chDate.GetJD( 0, 0, 0 ) );
//        astro.SetDateSerial( chDate.GetDateSerial() );
//      }
//      chDate.AddDay( 1 );  //�v�Z��������X�V
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

    //����(�����̎w���)����A�v�Z�J�n����I��
    switch( index_day )
    {
      case kDAY_YESTERDAY: index_day_begin = -1; break;
      case kDAY_TODAY:     index_day_begin =  0; break;
      case kDAY_TOMORROW:  index_day_begin =  1; break;
    }

    //UpdateTime()�����x���ĂԂƁA���̊Ԃɓ��t���ς��\��������̂�
    //1�񂵂��Ă΂Ȃ��悤�ɏ�������
    chLocalDate.UpdateTime();
    chLocalDate.SetTime
      ( chCurrentDate.GetYear(), chCurrentDate.GetMonth(),  chCurrentDate.GetDay(),
        chCurrentDate.GetHour(), chCurrentDate.GetMinute(), chCurrentDate.GetSecond() );
    
    chLocalDate.AddDay( index_day_begin - 1 );  //�O��(�����̎w���-1)����J�n����
    for( int day_cnt = 0; day_cnt < Astronomy.DAYS_OF_CALENDAR; day_cnt++ )
    {
      //�Ԍo�A�Ԉ܂��v�Z���A�ݒ肷��
      astro.CalcLocation( chLocalDate.CalcT( 0, 0 ), locRed );
      astro.SetCalendar ( day_cnt,              locRed );

      if( day_cnt == ( Astronomy.DAYS_OF_CALENDAR / 2) )
      {
        //����(�����̎w���)�̏���
        //--�O���j�W���P����
        astro.CalcGST( chLocalDate.GetHour() - 9, chLocalDate.GetJD( 0, 0, 0 ) );
        astro.SetDateSerial( chLocalDate.GetDateSerial() );
      }
      chLocalDate.AddDay( 1 );  //�v�Z��������X�V

      //debug
      //chYesterday.SetTime( 1999, 11, 13, 0, 0, 0 );
      //chToday.SetTime    ( 1999, 11, 14, 0, 0, 0 );
      //chTomorrow.SetTime ( 1999, 11, 15, 0, 0, 0 );
    }
  }
 
  public void CalcRiseSet( Astronomy[] astro, Location locate_here )
  {
    //�o�v�̌v�Z
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
    //�o�v�֘A
    //[0] = �o : [1] = �쒆 : [2] = ��
    double d = 0;
    double A = 0;

    String strAngleFormat = "";  //���ʊp
    String strAzimuthName = "";  //���ʊp�̖���

    for( int time_cnt = 0; time_cnt < 3; time_cnt++ )
    {
      d = astro.GetRiseSetTime   ( time_cnt );  //�������擾

      if( IsInRange( d, 1.0, 0.0 ) )
      {
        //�Ώۂ̏o�A�쒆�A�����������ɂ��鎞�́A�ʏ�̕\���ɂ���
        A = astro.GetRiseSetAzimuth( time_cnt );  //���ʊp���擾

        strAngleFormat = CoordinateHori.toAngleFormat( A );
        strAzimuthName = AlignSpace( CoordinateHori.toAzimuthName( A ), 3 );
      }
      else
      {
        //�Ώۂ̏o�A�쒆�A�����������ɖ������́A�����̕\���ɂ���
        strAngleFormat = "000.00";
        strAzimuthName = "�@�@�@";
      }

      txtRiseSet[time_cnt].setText(
          String.format( "���ʊp:%s(%s) / ���� %s",
                         strAngleFormat,
                         strAzimuthName,
                         Chronus.toElapsedTime( d ) ) );
    }

    //�N���b�v�{�[�h�ɃR�s�[��������i�[
    //���̏o
    infoRise.SetD      ( astro.GetRiseSetTime   ( astro.TIME_RISE ) );  //����
    infoRise.SetAzimuth( astro.GetRiseSetAzimuth( astro.TIME_RISE ) );  //���ʊp

    //���̓�
    infoSet.SetD       ( astro.GetRiseSetTime   ( astro.TIME_SET  ) );  //����
    infoSet.SetAzimuth ( astro.GetRiseSetAzimuth( astro.TIME_SET  ) );  //���ʊp
  }
  private void CalcAstroViewable( Astronomy astro[], double d, AstroViewable viewable )
  {
    //�Ώۂ̘f�������邱�Ƃ��o������ʊp�A���Ԃ͈̔͂��v�Z
    //astro[]
    //  [kDAY_TODAY]     : ����
    //  [kDAY_YESTERDAY] : ���
    //  [DAY_TOMRROW]    : ����
    double d_today_rise = astro[kDAY_TODAY].GetRiseSetTime( kTIME_RISE );
    double d_today_set  = astro[kDAY_TODAY].GetRiseSetTime( kTIME_SET  );

    int index_day_rise = 0;
    int index_day_set  = 0;

    //���̏o�A����������𒲂ׂ�
    /*
        �o ; d_today_rise �� 1.0 �` 0.0 �͈͓̔� -> ����
                                          �͈͊O -> ���

        �� ; d_today_rise �� 1.0 �` 0.0 �͈͓̔� -> ����
                                          �͈͊O -> ����
    */
    index_day_rise = IsInRange( d_today_rise, 1.0, 0.0 ) ? kDAY_TODAY : kDAY_YESTERDAY;
    index_day_set  = IsInRange( d_today_set,  1.0, 0.0 ) ? kDAY_TODAY : kDAY_TOMORROW;

    //���̓����o���x�����̕␳
    __timeline_is_rise_to_set:
    if( ( index_day_rise == kDAY_TODAY) && ( index_day_set == kDAY_TODAY ) )
    {
      //�������A���̏o -> ���̓��̏��Ԃ̏ꍇ�͗�O���������Ȃ�
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
    //���݂̈ʒu
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

    //"���o:%s / �k��:%s"
    txtview.setText(
        getString( R.string.strLayoutLocateHereBody,
                   strLongitude,
                   strLatitude ) );
  }
  
  private void ShowCoordinate( TextView txtview,
                               Geometry geometry )
  {
    txtview.setText(
        String.format( "���ʊp:%s(%s) / ���x�p:%s",
                       CoordinateHori.toAngleFormat( geometry.azimuth    ),
                       AlignSpace( CoordinateHori.toAzimuthName( geometry.azimuth, 4 ), 3 ),
                       CoordinateHori.toAngleFormat( geometry.height     ) ) );
  }

  private String AlignSpace( String strOriginal,
                             int    lengthOfAll )
  {
    //�w�肳�ꂽ������(lengthOfAll)�ɂȂ�܂�
    //���͕���(strOriginal)�ɑS�p�X�y�[�X��t�������������Ԃ�
    String strAligned = strOriginal;

    for( int index = strOriginal.length(); index < lengthOfAll; index++ )
    {
      strAligned = strAligned + "�@";  //�S�p�X�y�[�X��t��
    }
    
    return strAligned;
  }
}
