package intermediateModelHelper.envirorment.temporal.structure;

import java.util.List;

/**
 * Created by giovanni on 07/03/2017.
 */
public class TimeTypes extends TimeInfo {

    public TimeTypes(String className, String methodName, List<String> signature) {
        super(className, methodName, signature);
    }

    @Override
    public String toString() {
        StringBuilder sign = new StringBuilder();
        int last = signature.size() - 1;
        for(int i = 0; i <= last; i++){
            if(i == last){
                sign.append(signature.get(i));
            } else {
                sign.append(signature.get(i)).append(",");
            }
        }
        return String.format("%s;%s;%s;long", className, methodName, sign.toString());
    }
}
