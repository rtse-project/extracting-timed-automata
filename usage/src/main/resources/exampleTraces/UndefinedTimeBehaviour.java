/**
 * Created by giovanni on 02/05/2017.
 */
public class UndefinedTimeBehaviour {
    public long method_1(int x) throws Exception {
        Object a = new Object();
        //a.wait();
        boolean flag = false;
        try{
            long duration_secs = System.currentTimeMillis()/1000;
            if ( duration_secs == x && flag){

                long length = 1000;

                return( length * 10 );

            }  else{
                long mb_per_sec = 3;	// upper limit of 3 MB a sec assumed

                return( duration_secs * mb_per_sec*1024*1024L );
            }
        }catch( Throwable e ){
            return 10;
        }
    }

    public static void main(String[] args) throws Exception {
        new UndefinedTimeBehaviour().method_1(100);
    }

}