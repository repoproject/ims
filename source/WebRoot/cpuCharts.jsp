<%@page language="java"%>
<%@page import="java.util.Calendar" %>
<%@page import="java.text.SimpleDateFormat" %>
<%
        int lowerLimitGoogle = 30;
        int upperLimitGoogle = 35;
        
        int lowerLimitDell = 30;
        int upperLimitDell = 35;
        double randomValueGoogle = Math.random()*100*(upperLimitGoogle-lowerLimitGoogle)/100+lowerLimitGoogle;
        double randomValueDell = Math.random()*100*(upperLimitDell-lowerLimitDell)/100+lowerLimitDell;
        long factor = (long)Math.pow(10,2);
        randomValueGoogle = randomValueGoogle * factor;
        randomValueDell = randomValueDell * factor;
        long tmpGoogle = Math.round(randomValueGoogle);
        long tmpDell = Math.round(randomValueDell);
        double roundedRandomValueGoogle = (double)tmpGoogle / factor;
        double roundedRandomValueDell = (double)tmpDell / factor;
      
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timeLabel = sdf.format(cal.getTime());
        String dataParameters = "&label=" +timeLabel+ "&value=" +roundedRandomValueGoogle + "|" + roundedRandomValueDell;
        
        out.print(dataParameters);
%>