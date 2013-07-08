//copy into the web console to generate a transposed csv of the relevant data
var jq = document.createElement('script');
jq.src = "http://code.jquery.com/jquery-latest.min.js";
document.getElementsByTagName('head')[0].appendChild(jq);
jQuery.noConflict();


var $trs = $("table.table-sort thead tr");
var csv = $.map($trs.find("td"),function(o) { return $(o).text().trim(); }).join(";");
csv += "\n";
$trs = $("table.table-sort tbody tr");
$trs.each(function(index, o) {
	var $o = $(o);
	csv += $.map($o.find("td"),function(o) { return $(o).text().trim(); }).join(";");
	csv += "\n";
});
$("pre").remove();
$("<pre/>").appendTo("body");
$("pre:first").text(csv).css("font-size","8px");
