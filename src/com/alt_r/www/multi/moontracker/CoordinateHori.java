package com.alt_r.www.multi.moontracker;

//�n�����W�n
public class CoordinateHori
{
  private double d_;        //�o�ߎ���(24���� = 1.0)
  private double Azimuth_;  //���ʊp
  private double Height_;   //���x�p

  static String[] AzimuthString
    = {"�k", "�k�k��", "�k��", "���k��",
       "��", "���쓌", "�쓌", "��쓌",
       "��", "��쐼", "�쐼", "���쐼",
       "��", "���k��", "�k��", "�k�k��"};

  //-------------------------------------------
  public double GetD()
  {
    return d_;
  }
  public void SetD( double d )
  {
    d_ = d;
  }
  public void AddD( double delta_d )
  {
    d_ += delta_d;
  }

  public double GetAzimuth()
  {
    return Azimuth_;
  }
  public void SetAzimuth( double azimuth )
  {
    Azimuth_ = azimuth;
  }

  public double GetHeight()
  {
    return Height_;
  }
  public void SetHeight( double height )
  {
    Height_ = height;
  }

  public void SetGeometry( double azimuth, double height )
  {
    SetAzimuth( azimuth );
    SetHeight ( height  );
  }

  public static String toAzimuthName( double azimuth )
  {
    return toAzimuthName( azimuth, 8 );
  }
  public static String toAzimuthName( double azimuth, int div )
  {
    //���ʊp�̖��O�i���ʁj��Ԃ�
    int    index = 16  / div;
    double step  = 360 / div;

    double azimuth_min = 0;
    double azimuth_max = 0;
    
    for( int cnt = 1; cnt < div; cnt++ )
    {
      azimuth_min = cnt * step - ( step / 2 );
      azimuth_max = cnt * step + ( step / 2 );
      
      if( ( azimuth_min <= azimuth     ) && 
          ( azimuth     <  azimuth_max )    )
      {
        return AzimuthString[cnt * index];
      }
    }
    return AzimuthString[0];
  }
  
  public static String toAngleFormat( double angle )
  {
    //�p�x�𐳋K�������������Ԃ�
    return String.format( "%06.2f", angle );
  }
}
