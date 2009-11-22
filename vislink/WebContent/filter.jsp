<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.concurrent.BlockingQueue"%>
<%@page import="java.util.concurrent.ArrayBlockingQueue"%><html>
<head>
<script src="xml.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Filter</title>
</head>
<body>
<%
	String filter = request.getParameter("filter");
	BlockingQueue<String> queue = (BlockingQueue<String>) getServletContext().getAttribute("filterQueue");
	if (queue == null) {
		queue = new ArrayBlockingQueue<String>(10);
		getServletContext().setAttribute("filterQueue", queue);
	}
	if (filter != null && !filter.isEmpty()) {
		queue.put(filter);
		System.out.println("queued filter=" + filter);
	}
%>
Filter: 
<form name="filter" method="GET" action="filter.jsp" /> 
	<input name="filter" type="text" value="<% out.print(filter); %>" /><br />
	<input type="submit" />
</form>
</body>
</html>

