package fogassistant2;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ParameterDataSource {
	
	//database fields
	private SQLiteDatabase database;
	private MySQLiteOpenHelper dbHelper;
	
	public ParameterDataSource(Context context) {
		dbHelper = new MySQLiteOpenHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public void insertParameter(float[] p,int ds)
	{
		ContentValues row = new ContentValues();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
		Calendar c = Calendar.getInstance();
		row.put(MySQLiteOpenHelper.PARAMETER_ORARIO, sdf.format(c.getTime()));
		row.put(MySQLiteOpenHelper.PARAMETER_DECIMISEC, ds);
		row.put(MySQLiteOpenHelper.PARAMETER_FI, p[0]);
		row.put(MySQLiteOpenHelper.PARAMETER_EN, p[1]);
		database.insert(MySQLiteOpenHelper.TABLE_PARAMETER, null, row);
	}
	
	public void insertFOG(double durata,int ds)
	{
		ContentValues row = new ContentValues();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
		Calendar c = Calendar.getInstance();
		row.put(MySQLiteOpenHelper.FREEZING_ORARIO_FINE, sdf.format(c.getTime()));
		row.put(MySQLiteOpenHelper.FREEZING_DECIMISEC_FINE,ds);
		row.put(MySQLiteOpenHelper.FREEZING_DURATA, durata);
		database.insert(MySQLiteOpenHelper.TABLE_FREEZING, null, row);
	}

}
