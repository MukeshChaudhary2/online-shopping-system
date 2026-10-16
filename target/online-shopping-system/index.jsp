<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Automatically forward the root context to HomeServlet
    response.sendRedirect(request.getContextPath() + "/home");
%>
