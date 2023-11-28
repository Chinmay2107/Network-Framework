package com.thinking.machines.nframework.server;
import com.thinking.machines.nframework.server.annotations.*;
import com.thinking.machines.nframework.common.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import java.nio.charset.*;
class RequestProcessor extends Thread
{
private NFrameworkServer server;
private Socket socket;
private ArrayList<Class> classes;
RequestProcessor(Socket socket,NFrameworkServer server)
{
this.socket=socket;
this.server=server;
start();
}
public void run()
{
try
{
InputStream is=socket.getInputStream();
OutputStream os=socket.getOutputStream();
int bytesToReceive=1024;
byte[] tmp=new byte[1024];
byte [] header=new byte[1024];
int bytesReadCount;
int i,j,k;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
header[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
int requestLength=0;
i=1;
j=1023;
while(j>=0)
{
requestLength=requestLength+(header[j]*i);
i=i*10;
j--;
}
byte [] ack=new byte[1];
ack[0]=1;
os.write(ack,0,1);
os.flush();
byte[] request=new byte[requestLength];
bytesToReceive=requestLength;
i=0;
j=0;
int chunkSize=4096;
tmp=new byte[chunkSize];
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
request[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}

/*Using Deserialization (Java Specific)
ByteArrayInputStream bais=new ByteArrayInputStream(request);
ObjectInputStream ois=new ObjectInputStream(bais);
Request requestObject=(Request)ois.readObject();*/

String jsonString=new String(request,StandardCharsets.UTF_8);
Request requestObject=JSONUtil.fromJSON(jsonString,Request.class);
String path=requestObject.getServicePath();
Object [] arguments=requestObject.getArguments();
//System.out.println(jsonString);
System.out.println(path);
TCPService tcpService=this.server.getTCPService(path);
Response response=new Response();
if(tcpService==null)
{
response.setSuccess(false);
response.setResult(null);
response.setException(new RuntimeException("Invalid path : "+path));
}
else
{
try
{
Class c=tcpService.c;
Method method=tcpService.method;
Object serviceObject=c.newInstance();
Object result=method.invoke(serviceObject,arguments);
response.setSuccess(true);
response.setResult(result);
response.setException(null);
}catch(InstantiationException instantiationException)
{
response.setSuccess(false);
response.setResult(null);
response.setException(new RuntimeException("Unable to create object to service class associated with the path : "+path));
}
catch(IllegalAccessException illegalAccessException)
{
response.setSuccess(false);
response.setResult(null);
response.setException(new RuntimeException("Unable to create object to service class associated with the path : "+path));
}
catch(InvocationTargetException invocationTargetException)
{
Throwable t=invocationTargetException.getCause();
response.setSuccess(false);
response.setResult(null);
response.setException(t);
}
}
String responseString=JSONUtil.toJSON(response);
//System.out.println(responseString);
/*When Serialization is used, Java specific
Response response=new Response();
response.setResult(result);
ByteArrayOutputStream baos=new ByteArrayOutputStream();
ObjectOutputStream oos=new ObjectOutputStream(baos);
oos.writeObject(response);
oos.flush();
byte [] objectBytes=baos.toByteArray();*/
byte [] objectBytes=responseString.getBytes(StandardCharsets.UTF_8);
int responseLength=objectBytes.length;
int x;
i=1023;
x=responseLength;
header=new byte[1024];
while(x>0)
{
header[i]=(byte)(x%10);
x=x/10;
i--;
}
os.write(header,0,1024);
os.flush();
//System.out.println("Response header sent : "+responseLength);
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
//System.out.println("Acknowledgement received");
int bytesToSend=responseLength;
j=0;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j=j+chunkSize;
}
//System.out.println("Response sent");
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
//System.out.println("Acknowledgement received");
socket.close();
}catch(IOException e)
{
System.out.println(e);
}
}
}