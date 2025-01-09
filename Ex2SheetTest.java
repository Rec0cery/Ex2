import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class Ex2SheetTest
{
    @Test
    void isInTest()
    {
        Ex2Sheet s1=new Ex2Sheet(2,2);
        int x=5;
        int y=1;
        if(s1.isIn(x,y)==true) assertTrue(true);
        else assertFalse(false);

    }
    @Test
    void valueTest()
    {
        Ex2Sheet s1=new Ex2Sheet(2,2);
        s1.set(0,0,"=55+6");
        if(s1.value(0,0)=="61") assertTrue(true);
        else assertFalse(false);

    }
    @Test
    void evalTest()
    {
        Ex2Sheet s1=new Ex2Sheet(2,2);
        s1.set(0,0,"=55+6");
        s1.set(0,1,"=A0");
        s1.set(1,0,"=A0+A1");
        s1.set(1,1,"=Hello");
        if(s1.eval(0,0)=="122") assertTrue(true);
        else assertFalse(false);
    }


}
