//USING XML
package com.thinking.machines.nframework.client;
import org.xml.sax.*;
import javax.xml.xpath.*;
import com.thinking.machines.nframework.common.exceptions.*;
import java.io.*;
class Configuration
{
private static String host="";
private static int port=-1;
private static boolean malformed=false;
private static boolean fileMissing=false;
static
{
try
{
File file=new File("server.xml");
if(file.exists())
{
InputSource inputSource=new InputSource("server.xml");
XPath xpath=XPathFactory.newInstance().newXPath();
String host=xpath.evaluate("//server/@host",inputSource);
String port=xpath.evaluate("//server/@port",inputSource);
Configuration.host=host;
Configuration.port=Integer.parseInt(port);
}
else fileMissing=true;
}catch(Exception exception)
{
malformed=true; //do nothing
}
}
public static String getHost() throws NetworkException
{
if(fileMissing) throw new NetworkException("server.xml is missing, read documentation");
if(malformed) throw new NetworkException("server.xml not configured according to documentation");
if(host==null || host.trim().length()==0) throw new NetworkException("server.xml not configured according to documentation");
return host;
}
public static int getPort() throws NetworkException
{
if(fileMissing) throw new NetworkException("server.xml is missing, read documentation");
if(malformed) throw new NetworkException("server.xml not configured according to documentation");
if(port<0 || port>49151) throw new NetworkException("server.xml contains invalid port number, read documentation");
return port;
}
}
/*--------------------------------USING JSON 
package com.thinking.machines.network.client;
import java.io.*;
import com.google.gson.*;
public class Configuration
{
public static String host;
public static int portNumber;
static
{
host="localhost";
portNumber=5500;
try
{
File file=new File("server.cfg");
if(!file.exists())
{
System.out.println("Server configuration file doesnot exist");
System.exit(0);
}
RandomAccessFile randomAccessFile=new RandomAccessFile(file,"r");
String hostAndPort="";
while(randomAccessFile.getFilePointer()<randomAccessFile.length())
{
hostAndPort+=randomAccessFile.readLine();
}
randomAccessFile.close();
Gson gson=new Gson();
Conf conf=gson.fromJson(hostAndPort,Conf.class);
}catch(Exception e)
{
System.out.println(e);
System.exit(0);
}
}
public static String getHost()
{
return host;
}
public static int getPort()
{
return portNumber;
}
class Conf implements java.io.Serializable
{
private String host;
private int port;
public void setHost(String host)
{
this.host=host;
}
public void setPort(int port)
{
this.port=port;
}
public int getPort()
{
return this.port;
}
public String getHost()
{
return this.host;
}
}
}
---------------------------------------*/