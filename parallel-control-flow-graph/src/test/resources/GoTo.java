public class ForTest {

	public void singleMethod(){
		int intro;
		for(int i = 0; i < 10; i++){
			continue;
		}
		int end;
	}

	public void singleMethod1(){
		int intro;
		for(int i = 0; i < 10; ){
			break;
		}
		int end;
	}

	public void singleMethod2(){
		int intro;
		for(; i < 10; i++){
			return;
		}
		int end;
	}

	public void singleMethod2(){
		int intro;
		throw new Exception();
		int end;
	}

}