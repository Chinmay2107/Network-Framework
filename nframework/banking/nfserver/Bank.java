import com.thinking.machines.nframework.server.*;
import com.thinking.machines.nframework.server.annotations.*;
@Path("/banking")
public class Bank
{
@Path("/getBranchName")
public String getBranchName(String city) throws BankingException
{
if(city.equals("Ujjain")) return "Madhav Nagar";
if(city.equals("MUMBAI")) return "Coloba";
throw new BankingException("No branch located in city: "+city);
//return "No branch located in city"+city;
}
public static void main(String gg[])
{
NFrameworkServer server=new NFrameworkServer();
server.registerClass(Bank.class);
server.start();
}
}