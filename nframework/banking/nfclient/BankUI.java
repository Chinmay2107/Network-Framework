import com.thinking.machines.nframework.client.*;
class BankUI
{
public static void main(String gg[])
{
try
{
NFrameworkClient nfc=new NFrameworkClient();
/*Object result=nfc.process("/banking/getBranchName",gg[0]);
String branch=(String)result;
System.out.println(branch);*/
/* For calculator class*/
Object result=nfc.process("/calculator/add",Integer.parseInt(gg[0]),Integer.parseInt(gg[1]));
Double i=(Double)result;
System.out.println(i);
result=nfc.process("/calculator/sub",150,150);
System.out.println(result);
result=nfc.process("/calculator/mul",15,150);
System.out.println(result);
result=nfc.process("/calculator/div",9000,150);
System.out.println(result);

}catch(Throwable t)
{
System.out.println(t.getMessage());
}
}
}