import java.io.IOException;

public class TryTest {

	public void singleMethod(){
		int intro;
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
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
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			int inside_try;
			int do_smth;
		} catch(Exception e) {} finally {
			int do_smth_in_finally;
		}
		int end;
	}
}


