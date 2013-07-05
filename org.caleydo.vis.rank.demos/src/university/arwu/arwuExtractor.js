var jq = document.createElement('script');
jq.src = "http://code.jquery.com/jquery-latest.min.js";
document.getElementsByTagName('head')[0].appendChild(jq);
jQuery.noConflict();


var $trs = $("#UniversityRanking tbody tr");

var ranking = $.map($trs.find("td:nth-child(1)"),function(o) { return $(o).text().trim(); }).join(";")
var institution = $.map($trs.find("td:nth-child(2) a div"),function(o) { return $(o).text().trim(); }).join(";")
var country = $.map($trs.find("td:nth-child(3) a"),function(o) { var t = $(o).attr("href").trim(); return t.substr(t.indexOf("=")+1); }).join(";")

var national = $.map($trs.find("td:nth-child(4) div"),function(o) { return $(o).text().trim(); }).join(";")
var total = $.map($trs.find("td:nth-child(5) div"),function(o) { return $(o).text().trim(); }).join(";")

var alumini = $.map($trs.find("td:nth-child(6) div"),function(o) { return $(o).text().trim(); }).join(";")
var award = $.map($trs.find("td:nth-child(7) div"),function(o) { return $(o).text().trim(); }).join(";")
var hici = $.map($trs.find("td:nth-child(8)"),function(o) { return $(o).text().trim(); }).join(";")
var nands = $.map($trs.find("td:nth-child(9)"),function(o) { return $(o).text().trim(); }).join(";")
var pub = $.map($trs.find("td:nth-child(10)"),function(o) { return $(o).text().trim(); }).join(";")
var pcb = $.map($trs.find("td:nth-child(11)"),function(o) { return $(o).text().trim(); }).join(";")

var csv = "";
csv += "ranking;"+ranking+"\n";
csv += "institution;"+institution+"\n";
csv += "country;"+country+"\n";
csv += "national;"+national+"\n";
csv += "total;"+total+"\n";
csv += "alumini;"+alumini+"\n";
csv += "award;"+award+"\n";
csv += "hici;"+hici+"\n";
csv += "nands;"+nands+"\n";
csv += "pub;"+pub+"\n";
csv += "pcb;"+pcb+"\n";

$("pre").remove();
$("<pre/>").appendTo("body");
$("pre:first").text(csv).css("font-size","8px");
