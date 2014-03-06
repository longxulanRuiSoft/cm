<%
	response.setContentType("application/json; charset=UTF-8");
	java.io.Writer writer = response.getWriter();
	writer.write("{\"Rows\":[],\"Total\":\"0\"}");
	writer.flush();
	writer.close();
%>