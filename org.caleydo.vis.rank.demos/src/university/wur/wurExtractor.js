//copy into the web console to generate a transposed csv of the relevant data
var jq = document.createElement('script');
jq.src = "http://code.jquery.com/jquery-latest.min.js";
document.getElementsByTagName('head')[0].appendChild(jq);
jQuery.noConflict();


var $trs = $("table.views-table tbody tr");

var qsrank = $.map($trs.find("td:nth-child(2)"),function(o) { return $(o).text().trim(); }).join(";")
var schoolname = $.map($trs.find("td:nth-child(5) a"),function(o) { return $(o).text().trim(); }).join(";")
var qsstars = $.map($trs.find("td:nth-child(6) a span"),function(o) { return $(o).attr("class").trim().substr(9,10); }).join(";")

var overall = $.map($trs.find("td:nth-child(7) div"),function(o) { return $(o).text().trim(); }).join(";")
var academic = $.map($trs.find("td:nth-child(8)"),function(o) { return $(o).text().trim(); }).join(";")
var employer = $.map($trs.find("td:nth-child(9)"),function(o) { return $(o).text().trim(); }).join(";")
var faculty = $.map($trs.find("td:nth-child(10)"),function(o) { return $(o).text().trim(); }).join(";")
var international = $.map($trs.find("td:nth-child(11)"),function(o) { return $(o).text().trim(); }).join(";")
var internationalstudents = $.map($trs.find("td:nth-child(12)"),function(o) { return $(o).text().trim(); }).join(";")
var citations = $.map($trs.find("td:nth-child(13)"),function(o) { return $(o).text().trim(); }).join(";")
var arts = $.map($trs.find("td:nth-child(14)"),function(o) { return $(o).text().trim(); }).join(";")
var engineering = $.map($trs.find("td:nth-child(15)"),function(o) { return $(o).text().trim(); }).join(";")
var life =  $.map($trs.find("td:nth-child(16)"),function(o) { return $(o).text().trim(); }).join(";")
var natural = $.map($trs.find("td:nth-child(17)"),function(o) { return $(o).text().trim(); }).join(";")
var social = $.map($trs.find("td:nth-child(18)"),function(o) { return $(o).text().trim(); }).join(";")

var csv = "";
csv += "qsrank;"+qsrank+"\n";
csv += "schoolname;"+schoolname+"\n";
csv += "qsstars;"+qsstars+"\n";
csv += "overall;"+overall+"\n";
csv += "academic;"+academic+"\n";
csv += "employer;"+employer+"\n";
csv += "faculty;"+faculty+"\n";
csv += "international;"+international+"\n";
csv += "internationalstudents;"+internationalstudents+"\n";
csv += "citations;"+citations+"\n";
csv += "arts;"+arts+"\n";
csv += "engineering;"+engineering+"\n";
csv += "life;"+life+"\n";
csv += "natural;"+natural+"\n";
csv += "social;"+social+"\n";

$("pre").remove();
$("<pre/>").prependTo("body");
$("pre:first").text(csv).css("font-size","8px");
