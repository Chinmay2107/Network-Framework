package com.thinking.machines.nframework.server;
import com.thinking.machines.nframework.server.annotations.*;
import com.thinking.machines.nframework.common.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
public class NFrameworkServer
{
private ServerSocket serverSocket;
private Socket socket;
private Set<Class> tcpNetworkServiceClasses;
private Map<String,TCPService> serviceClassesMap;
public NFrameworkServer()
{
this.tcpNetworkServiceClasses=new HashSet<>();
this.serviceClassesMap=new HashMap<>();
}
public void registerClass(Class c)
{
if(c!=null)
{
Path pathOnType;
Path pathOnMethod;
Method [] methods;
String fullPath;
String path;
TCPService tcpService=null;
pathOnType=(Path)c.getAnnotation(Path.class);
if(pathOnType==null) return;
methods=c.getMethods();
int methodsWithPathAnnotationCount=0;
for(Method method:methods)
{
pathOnMethod=(Path)method.getAnnotation(Path.class);
if(pathOnMethod==null) continue;
methodsWithPathAnnotationCount++;
path=pathOnType.value()+pathOnMethod.value();
tcpService=new TCPService();
tcpService.c=c;
tcpService.method=method;
tcpService.path=path;
this.serviceClassesMap.put(path,tcpService);
}
if(methodsWithPathAnnotationCount>0) this.tcpNetworkServiceClasses.add(c);
}
}
public TCPService getTCPService(String path)
{
return this.serviceClassesMap.get(path);
/*Path pathOnType;
Path pathOnMethod;
Method [] methods;
String fullPath;
TCPService tcpService=null;
for(Class c:tcpNetworkServiceClasses)
{
pathOnType=(Path)c.getAnnotation(Path.class);
if(pathOnType==null) continue;
methods=c.getMethods();
for(Method method:methods)
{
pathOnMethod=(Path)method.getAnnotation(Path.class);
if(pathOnMethod==null) continue;
fullPath=pathOnType.value()+pathOnMethod.value();
if(path.equals(fullPath))
{
tcpService=new TCPService();
tcpService.c=c;
tcpService.method=method;
tcpService.path=path;
return tcpService;
}
}
}
return null;*/
}
public void start()
{
RequestProcessor requestProcessor;
int port=-1;
try
{
port=Configuration.getPort();
serverSocket=new ServerSocket(port);
}catch(Exception e)
{
System.out.println(e);
System.out.println("Unable to start server on port : "+port);
System.exit(0);
}
try
{
while(true)
{
System.out.println("Server is ready to accept request at port : "+port);
socket=serverSocket.accept();
requestProcessor=new RequestProcessor(socket,this);
}
}catch(Exception e)
{
System.out.println(e);
}
}
}