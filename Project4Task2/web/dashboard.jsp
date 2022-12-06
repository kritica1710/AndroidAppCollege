<%@ page import="task2.LogResults" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="task2.LogData" %><%--
  Created by IntelliJ IDEA.
  User: kriticasinha
  Date: 3/31/20
  Time: 4:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<html>
<head>
    <title>BoozStir Analytics Dashboard</title>
    <style>
        body {background-color: #ffffff;}
        thead {
            background: #97a1ff;
            color: white;
            font-family: "Comic Sans MS", cursive, sans-serif;


        }

        tbody tr:nth-child(odd) {
            background-color: #ffdaf3;
            border-radius: 8px;
        }
        tbody tr:nth-child(even) {
            background-color: #dce9ff;
            border-radius: 8px;
        }
        table {
            background-color: #ffffff;
            border-radius: 8px;
            table-border-color-light: #61626f;
        }
        tbody
        {color: #61626f;
            text-align: center;}
        h4
        {
            background: gray;
            color: white;
            font-family: "Comic Sans MS", cursive, sans-serif;
            text-align: center;
            border-radius: 8px;
        }


    </style>
</head>
<body>

<h4> Operations Analytics </h4>


<table align="center">
    <thead>
    <tr>
        <th>Total Requests Received</th>
        <th>Most Searched Drink and its count</th>
        <th>Average Total Latency</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <%
            ArrayList<LogResults> analytics = (ArrayList<LogResults>) request.getAttribute("results");
            for (LogResults l : analytics)

            {
        %>
        <td><%=l.requestNum%></td>
        <td><%=l.mostSearchedDrink%> : <%=l.drinkFreq%></td>
        <td><%=l.avgLatency%></td>

        <% } %>
    </tr>
    </tbody>
</table>

<h4> Full Logs </h4>


<table>
    <thead>
    <tr>
        <th>Searched Drink</th>
        <th>User Agent</th>
        <th>Timestamp of request from BoozStir</th>
        <th>Timestamp of request to TheCocktailDB API</th>
        <th>Timestamp of response from TheCocktailDB API</th>
        <th>Timestamp of response to BoozStir</th>
        <th>Total Latency in communication (ms) </th>
    </tr>
    </thead>
    <tbody>
    <%
        ArrayList<LogData> data = (ArrayList<LogData>) request.getAttribute("document");
        for (LogData d : data)

        {
    %>
    <tr>

        <td><%=d.searchedDrink%></td>
        <td><%=d.device%></td>
        <td><%=d.reqFromBoozStir%></td>
        <td><%=d.reqToAPI%></td>
        <td><%=d.repFromAPI%></td>
        <td><%=d.repToBoozStir%></td>
        <td><%=d.TotalLatency%></td>


    </tr>
    <% } %>
    </tbody>
</table>

</body>
</html>
