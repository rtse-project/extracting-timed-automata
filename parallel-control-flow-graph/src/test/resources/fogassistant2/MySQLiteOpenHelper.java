package fogassistant2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	
	  public static final String TABLE_PARAMETER = "parameter";
	  public static final String PARAMETER_ORARIO = "orario";
	  public static final String PARAMETER_DECIMISEC = "decimisec";
	  public static final String PARAMETER_FI = "indicefreezing";
	  public static final String PARAMETER_EN = "energia";
	  
	  public static final String TABLE_FREEZING = "freezing";
	  public static final String FREEZING_ID = "id";
	  public static final String FREEZING_ORARIO_FINE = "orariofine";
	  public static final String FREEZING_DECIMISEC_FINE = "decimisecfine";
	  public static final String FREEZING_DURATA = "durata";

	  private static final String DATABASE_NAME = "monitoring.db";
	  private static final int DATABASE_VERSION = 1;
	  
	// Database creation sql statement
		  private static final String DATABASE_CREATE_PARAMETER = "create table if not exists "
		      + TABLE_PARAMETER + "(" + PARAMETER_ORARIO + " timestamp, " 
			  + PARAMETER_DECIMISEC + " tinyint, "
			  + PARAMETER_FI + " float(10,6), "
			  + PARAMETER_EN + " float(10,6), "
		      + " primary key("+ PARAMETER_ORARIO +","+ PARAMETER_DECIMISEC + "));";
	
		  private static final String DATABASE_CREATE_FREEZING = "create table if not exists "
			  + TABLE_FREEZING + "(" + FREEZING_ID + " integer primary key autoincrement, "
			  + FREEZING_ORARIO_FINE + " timestamp, "
			  + FREEZING_DECIMISEC_FINE + " tinyint, "
			  + FREEZING_DURATA + " float(3,1));";


	public MySQLiteOpenHelper(Context context) {
		super(context, Environment.getExternalStorageDirectory()+File.separator+DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_PARAMETER);
		db.execSQL(DATABASE_CREATE_FREEZING);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
