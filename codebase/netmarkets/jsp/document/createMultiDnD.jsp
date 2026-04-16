 <%@ page import="com.ptc.netmarkets.util.misc.NetmarketURL"%>
 <%@ page import="com.ptc.netmarkets.util.misc.NmAction"%>
 <%@ page import="com.ptc.netmarkets.util.beans.NmURLFactoryBean"%>
 <%@ page import="com.ptc.netmarkets.util.misc.NmActionServiceHelper"%>
 <%@ page import="wt.util.HTMLEncoder"%>
 
 <html>
	 <%
	 	String oid = HTMLEncoder.encodeForJavascript(request.getParameter("oid"));	 	 
		String ContainerOid = HTMLEncoder.encodeForJavascript(request.getParameter("ContainerOid"));			
		NmAction action = NmActionServiceHelper.service.getAction("document","createMulti");
		String url = NetmarketURL.buildURL(new NmURLFactoryBean(),action.getType(), action.getAction(), null);
	 %>
	 <body>
	 <script type = "text/javascript">
	   	  function func1() {
	 	  var baseURL = "<%=url%>";
	      window.location.replace(baseURL + "?oid=" + "<%=oid%>" + "&ContainerOid=" + "<%=ContainerOid%>" +"&isDnD=true&actionName=<%=action.getAction()%>&wizardActionClass=<%=action.getActionClass()%>&wizardActionMethod=<%=action.getActionMethod()%>"+"&portlet=poppedup","", "width=800, height=800");
	   	  }
	   	  window.onload =  func1;
	</script>
	</body>
</html>
