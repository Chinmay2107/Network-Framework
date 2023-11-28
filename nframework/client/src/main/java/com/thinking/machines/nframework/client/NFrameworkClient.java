package com.thinking.machines.nframework.client;
import com.thinking.machines.nframework.common.*;
import com.thinking.machines.nframework.common.exceptions.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
public class NFrameworkClient
{
public Object process(String path,Object ...arguments) throws Throwable
{
try
{
Request request=new Request();
if(path==null) throw new RuntimeException("Path is null");
if(arguments==null) throw new RuntimeException("Arguments are null");
request.setPath(path);
request.setArguments(arguments);
String jsonString=JSONUtil.toJSON(request);
System.out.println(jsonString);
/*Using Serialization which makes it java specific
ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
ObjectOutputStream objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);
objectOutputStream.writeObject(request);
objectOutputStream.flush();
byte bytes[]=byteArrayOutputStream.toByteArray();
*/
byte bytes[]=jsonString.getBytes(StandardCharsets.UTF_8);
int requestLength=bytes.length;
byte header[]=new byte[1024];
int x;
int i=1023;
x=requestLength;
while(x>0)
{
header[i]=(byte)(x%10);
x=x/10;
i--;
}
//Socket socket=new Socket("localhost",5500);
Socket socket=new Socket(Configuration.getHost(),Configuration.getPort());
OutputStream outputStream;
outputStream=socket.getOutputStream();
outputStream.write(header,0,1024);
outputStream.flush();
//System.out.println("Header sent");
InputStream inputStream=socket.getInputStream();
byte ack[]=new byte[1];
int bytesReadCount;
while(true)
{
bytesReadCount=inputStream.read(ack);
if(bytesReadCount==-1) continue;
break;
}
//System.out.println("Acknowledgement received");
int j;
int chunkSize=4096;
j=0;
int bytesToSend=requestLength;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=bytesToSend-j;
outputStream.write(bytes,j,chunkSize);
j=j+chunkSize;
}
//System.out.println("Request Object sent");
int bytesToReceive=1024;
byte tmp[]=new byte[1024];
int k=0;
j=0;
i=0;
while(j<bytesToReceive)
{
bytesReadCount=inputStream.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
header[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
//System.out.println("Header received as ack");
int responseLength=0;
i=1023;
j=1;
while(i>=0)
{
responseLength=responseLength+(header[i]*j);
j=j*10;
i--;
}
//System.out.println("Response Header Length Received : "+responseLength);
ack[0]=1;
outputStream.write(ack,0,1);
outputStream.flush();
//System.out.println("Acknowledgement sent");
chunkSize=4096;
tmp=new byte[chunkSize];
byte responseBytes[]=new byte[responseLength];
bytesToReceive=responseLength;
j=0;
i=0;
while(j<bytesToReceive)
{
bytesReadCount=inputStream.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
responseBytes[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
ack[0]=1;
outputStream.write(ack);
outputStream.flush();
//System.out.println("Last Acknowledgement sent");
socket.close();
/*
ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(responseBytes);
ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream);
Response response;
response=(Response)objectInputStream.readObject();
*/
jsonString=new String(responseBytes,StandardCharsets.UTF_8);
System.out.println(jsonString);
Response response=JSONUtil.fromJSON(jsonString,Response.class);
if(response.getSuccess()==true) return response.getResult();
else
{
throw response.getException();
}
}catch(Exception exception)
{
throw new NetworkException(exception.getMessage());
}
}
}