<?php
$date = $_REQUEST['date'];
$tumor = $_REQUEST['tumor'];

header('Content-Description: File Transfer');
header('Content-type: application/x-java-jnlp-file');
header('Content-Transfer-Encoding: binary');
header('Expires: 0');
header('Cache-Control: must-revalidate');
header('Pragma: public');
header("Content-Disposition: attachment; filename=\"{$date}_{$tumor}.jnlp\"");
flush();

$template = file_get_contents('./template.jnlp');

$parsed = str_replace( array('@DATE@', '@TUMOR@'), array($date, $tumor), $template);

echo $parsed;
exit;
?>