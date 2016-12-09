<%@ page language="java" pageEncoding="gb2312" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head><title>hello.jsp</title></head>
<body>
</body>
</html>
<%
    Connection conn =null;
    Statement stmt = null;
    String connectionString = "jdbc:mysql://localhost:3306/sockma?user=root&password=123456";
    try
    {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(connectionString);
        System.out.println("connected!!!!!!!!!!!!!");
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM POWER");
        out.print("<table border=\"1\"><tr><th>编号</th><th>日期</th><th>用电量</th></tr>");
        while (rs.next())
        {
            out.print("<tr>");
            for (int i = 1; i <= 3; i++) {
                out.print("<th>" + rs.getString(i) + "</th>");
            }
            out.print("</tr>");
        }
    }
    catch (Exception e) {
        e.printStackTrace();
    }
%>
