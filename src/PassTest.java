import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frederik on 03.12.2015.
 */
public class PassTest {
    public static void main(String[] args)
    {
        Integer a, b, c;
        a = new Integer(4);
        b = new Integer(3);
        c = a;
        a += b;
        System.out.println("a = " + a);
        System.out.println("c = " + c);
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>();
        list1.add(new Integer(5));
        list1.add(15);
        list1.size()
        for (int i: list1)
        {
            list2.add(i);
        }
        list3.addAll(list1);
        list3.get(1) = 0;
    }
}
