import java.io.IOException;

public class TryTest {

	public void singleMethod(){
		int intro;
		try {
			int inside_try;
			int do_smth;
		} catch(Exception e){
			int exception_general;
		} catch (IOException e) {
			int cathc_io_ex;
		}
		int end;
	}

	public void singleMethod1(){
		int intro;
		try {
			int inside_try;
			int do_smth;
		} catch(Exception e) {
			int we_are_inside_an_Ex;
		}
		finally {
			int do_smth_in_finally;
		}
		int end;
	}
}


