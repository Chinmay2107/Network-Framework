import com.thinking.machines.nframework.server.*;
import com.thinking.machines.nframework.server.annotations.*;
@Path("/calculator")
public class Calculator
{
@Path("/add")
public double add(double e,double f)
{
return e+f;
}
@Path("/sub")
public double subtract(double e,double f)
{
return e-f;
}
@Path("/mul")
public double multiply(double e,double f)
{
return e*f;
}
@Path("/div")
public double divide(double e,double f)
{
return e/f;
}
}
class Main
{
public static void main(String gg[])
{
try
{
NFrameworkServer nf=new NFrameworkServer();
nf.registerClass(Calculator.class);
nf.start();
}catch(Exception e)
{
System.out.println(e);
}
}
}