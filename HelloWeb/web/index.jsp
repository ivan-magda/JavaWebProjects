<%-- 
    Document   : index
    Created on : May 23, 2016, 10:58:22 AM
    Author     : ivanmagda
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <%for (int i = 0; i < 10; ++i) {%>
        <h1>Number: <% out.println(i); %> </h1>
        <%}%>
    </body>
</html>
