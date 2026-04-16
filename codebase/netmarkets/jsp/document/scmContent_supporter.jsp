<%@ page import="wt.facade.scm.ScmFacade"%>
<%
	if ( ( request.getParameter( "type" ) !=null ) && ( !request.getParameter( "type" ).isEmpty() ) ) {
		String object = (String) request.getParameter( "type" );

		if( ScmFacade.getInstance().isSoftwareDoc(object) )
			out.write("TRUE");
		else
			out.write("FALSE");
	}
%>
