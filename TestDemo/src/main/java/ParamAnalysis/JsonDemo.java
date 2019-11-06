package ParamAnalysis;

import com.google.gson.Gson;
import org.junit.Test;

public class JsonDemo {
    @Test
    public void test01(){
        Son son = new Son();
        son.id = "b";
        Gson gson = new Gson();
        String s = gson.toJson(son);
        Son son1 = new Gson().fromJson(s, Son.class);
        System.out.println(son1.id);
    }
}
