package ParamAnalysis;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class CollectionDemo {
    @Test
    public void test01(){
        Set<Integer> s1 = new HashSet<>();
        Set<Integer> s2 = new HashSet<>();
        s1.add(1);
        s2.add(1);
        s1.add(2);
        s2.add(3);
        s1.retainAll(s2);
        for (Integer integer : s1) {
            System.out.println(integer);
        }

        System.out.println("a");
        for (Integer integer : s2) {
            System.out.println(integer);
        }
    }

    @Test
    public void test02(){
        String s1 = "a";
        String s2 = "a";
        System.out.println(s1.compareTo(s2));
    }
}
