<html><body>
<SCRIPT language=javascript>
if (window.opener && window.opener.setFileOids) {
  window.opener.setFileOids('${param.oid}');
  window.close();
}
else if (window.opener == null) {
  document.write('window.opener == null');
  window.alert('window.opener == null');
}
else if (window.opener.setFileOids == null) {
  document.write('window.opener.setFileOids == null');
  window.alert('window.opener.setFileOids == null');
}
</SCRIPT>
</body></html>