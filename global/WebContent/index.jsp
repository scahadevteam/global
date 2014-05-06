<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.gbli.context.ContextManager" %>

<html>
<head><title>Example of Extends 
Attribute of page Directive in JSP</title></head>

<body>
<font size="20" color="red">
<%
ContextManager.c_seasons.scheduleSeasons();
%>
</font>
</body>


</html>
