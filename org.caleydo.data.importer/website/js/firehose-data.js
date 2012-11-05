// Nils Gehlenborg, July 2012
// start scope
(function() {
// ---------------------------------

var tumor;
var analysis;
var runs;
var runData;
var dataTypes = null;
var tumorTypes = null;  

var dataTypeColors = {
	"mRNA": "#8dd3c7",
	"mRNA-seq": "#77b3a8",
	"microRNA": "#b3de69",
	"microRNA-seq": "#90b354",
	"Mutations": "#CCEBC5",
	"Copy Number": "#fccde5",
	"Methylation": "#80b1d3",
	"RPPA": "#bebada",
	 };

// Read a page's GET URL variables and return them as an associative array.
function getUrlParameters( decode )
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');

    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        
        if ( hash.length == 2 ) {
	        vars.push(hash[0]);
	        
	        if ( decode == true ) {
		        vars[hash[0]] = decodeURIComponent( hash[1].split( "#" )[0] );
		    }
		    else {
		        vars[hash[0]] = hash[1].split( "#" )[0];	    
			}	            	
        }	    
    }

    return vars;
}   


function getBaseUrl()
{
    //return window.location.href.slice(0, window.location.href.indexOf('?'));
    return "http://compbio.med.harvard.edu/tcga/stratomex";
}   

function getCurrentUrl()
{
    var base = getBaseUrl();

    return base + "/index.html"
    			+ "?" + "analysis=" + analysis
    			+ "&" + "tumor=" + tumor;
}   


function initialize() {
	tumor = getUrlParameters()["tumor"];
	analysis = getUrlParameters()["analysis"];
	
	// open webstart tab if both tumor and analysis date are supplied 
	if ( tumor != undefined && analysis != undefined ) {
		$( "#tabs a[href=#stratomex]" ).tab('show');		
	}
	else {		
		$( "#tabs a[href=#overview]" ).tab('show');		
	}
	
	loadRuns( function( data ) {
		runs = data;
		renderAnalysisSelection( data, "#analysis-selection-container" );		
		analysisIndex = renderAnalysisSelectionOverview( data, "#analysis-selection-overview-container" );
		loadRun( getBaseUrl() + "/data/" + runs[analysisIndex].json, renderContent );
	});		
}


function loadRuns( callback ) {
	$.getJSON( getBaseUrl() + "/data/" + "tcga_analysis_runs.json", callback );
}


function loadRun( url, callback ) {
	$.getJSON( url, callback );
}


function renderContent( data )
{
	if ( !data ) {
		$( "<div/>", {
			"class": "alert alert-info",
			"html": "<a class=\"close\" data-dismiss=\"alert\" href=\"#\">x</a>" + getCurrentUrl()
		}).appendTo(  "#header-container"  );		
		
	}

	runData = data;
	
	tumorIndex = renderTumorSelection( runData, "#tumor-selection-container", tumor );
	
	if ( dataTypes == null ) {
		initializeDataTypeSelection( runData );		
	}
	
	if ( tumorTypes == null ) {
		initializeTumorTypeSelection( runData );		
	}
	
	renderDataTypeSelectionOverview( runData, "#datatype-selection-overview-container" );
	renderTumorTypeSelectionOverview( runData, "#tumortype-selection-overview-container" );
	renderOverviewChart();
	renderHeaderOverview( runData, "#header-overview-container" );			
	renderTumorType( data, tumorIndex );		
}

function renderTumorType( data, tumorIndex ) {
	renderHeader( data, tumorIndex, "#header-container" );	
	renderTable( data, tumorIndex, "#table-container" );	
	renderControls( data, tumorIndex, "#controls-container" );	
}

function clearTumorType() {
	$( "#header-container" ).html("");	
	$( "#table-container" ).html("");	
	$( "#controls-container" ).html("");
	
	$( "#tumor-selection-container" ).html(""); 	
	$( "#webstart-instructions-container" ).html( "" );
	$( "#webstart-button-container" ).html( "" );
	$( "#download-button-container" ).html( "" );
	$( "#report-button-container" ).html( "" );
	$( "#direct-link-container" ).html( "" );
	$( "#direct-link-url-container" ).html( "" );		
}


function renderAnalysisSelectionOverview( runs, element ) {
	
	var analysisDates = [];
	var analysisIndex = 0;
	//console.log(runs);
	
	for ( var i = 0; i < runs.length; ++i ) {
		if ( runs[i].id === analysis ) {
			analysisIndex = i;
			analysisDates.push( "<option selected id=\"" + "analysis_" + i + "\" value=\"" + i + "\">" + runs[i].label + "</option>" );
		}
		else {
			analysisDates.push( "<option id=\"" + "analysis_" + i + "\" value=\"" + i + "\">" + runs[i].label + "</option>" );
		}		
	}
	
	$( "<select/>", {
		id: "analysis-selection-overview",
		"class": "",
		"style": "width: 100%;",
		"html": analysisDates.join("\n")
	}).appendTo( element );

	analysis = runs[analysisIndex].id;
	
	
	$( "#analysis-selection-overview" ).on( "change", function() {
		clearTumorType();
		loadRun( getBaseUrl() + "/data/" + runs[this.value].json, renderContent );

		$( "#analysis-selection" ).val( this.value ).selected = true;
	});	
	
	return analysisIndex;
}


function renderAnalysisSelection( runs, element ) {
	
	var analysisDates = [];
	var analysisIndex = 0;
	//console.log(runs);
	
	for ( var i = 0; i < runs.length; ++i ) {
		if ( runs[i].id === analysis ) {
			analysisIndex = i;
			analysisDates.push( "<option selected id=\"" + "analysis_" + i + "\" value=\"" + i + "\">" + runs[i].label + "</option>" );
		}
		else {
			analysisDates.push( "<option id=\"" + "analysis_" + i + "\" value=\"" + i + "\">" + runs[i].label + "</option>" );
		}		
	}
	
	$( "<select/>", {
		id: "analysis-selection",
		"class": "",
		"style": "width: 100%;",
		"html": analysisDates.join("\n")
	}).appendTo( element );

	analysis = runs[analysisIndex].id;
	
	
	$( "#analysis-selection" ).on( "change", function() {
		clearTumorType();
		loadRun( getBaseUrl() + "/data/" + runs[this.value].json, renderContent );
		
		$( "#analysis-selection-overview" ).val( this.value ).selected = true;		
	});	
	
	return analysisIndex;
}


function renderTumorSelection( data, element ) {	
	var tumorTypes = [];
	var tumorIndex = 0;
	
	$( element ).html("");
	
	for ( var i = 0; i < data.details.length; ++i ) {
		if ( data.details[i].tumorAbbreviation === tumor ) {
			tumorIndex = i;
			tumorTypes.push( "<option selected id=\"" + "tumor_" + i + "\" value=\"" + i + "\">" + data.details[i].tumorAbbreviation + " - " + data.details[i].tumorName + "</option>" );
		}
		else {
			tumorTypes.push( "<option id=\"" + "tumor_" + i + "\" value=\"" + i + "\">" + data.details[i].tumorAbbreviation + " - " + data.details[i].tumorName + "</option>" );			
		}		
	}

	tumor = data.details[tumorIndex].tumorAbbreviation;
	
	$( "<select/>", {
		id: "tumor-selection",
		"class": "",
		"style": "width: 100%;",
		"html": tumorTypes.join("\n")
	}).appendTo( element );
	
	
	$( "#tumor-selection" ).on( "change", function() {
		renderTumorType( data, this.value );
		tumor = data.details[this.value].tumorAbbreviation;
	});	
	
	return tumorIndex;
}

function _makeDataTypeId( name ) {
	return name.replace( " ", "-" ).toLowerCase();
}

function initializeDataTypeSelection( data ) {
	dataTypes = {};
	
	for ( var p in data.details[0].genomic ) {
		if ( data.details[0].genomic.hasOwnProperty( p ) ) {
			dataTypes[p] = true; 
		}
	}	
}

function renderDataTypeSelectionOverview( data, element ) {		
	var dataTypeSelectorElements = [];
	
	$( element ).html("");
	
	dataTypeSelectorElements.push( "<thead><tr><th><i class=\" icon-eye-open\"></th><th colspan=2>Data Type&nbsp;<span style=\"margin-left: 0px;\" class=\"btn-group\"><span class=\"btn btn-mini\" id=\"clear-datatypes\">Clear</span><span class=\"btn btn-mini\" id=\"all-datatypes\">All</span></span></th></tr></thead>" );

	
	for ( var p in data.details[0].genomic ) {
		if ( data.details[0].genomic.hasOwnProperty( p ) ) { 
			dataTypeSelectorElements.push( "<tr>" + "<td>" + "<input type=\"checkbox\" name=\"" + "dataType" + "\" value=\"" + p + "\" " + ( dataTypes[p] == true ? "checked" : "" ) + ">" + "</td>" + "<td>" + p + "</td>" + "<td>" 
			+ "<span class=\"badge\" style=\"background: " + mapDataTypeToColor( p ) +";\"></span>" + "</td>" + "</tr>" );
		}
	}

	$( "<table/>", {
		id: "datatype-selector",
		"class": "table table-condensed table-striped",
		"html": dataTypeSelectorElements.join("\n")
	}).appendTo( element );
	
    $('input[name=dataType]').click(showCheckedValues);
                
	function showCheckedValues() {
		$('input[name=dataType]').map(function() {
			dataTypes[this.value] = this.checked;
		});

		renderOverviewChart();
	}
	
    $('#clear-datatypes').click(function() {
    	$('input[name=dataType]').map(function() {
			dataTypes[this.value] = false;
			this.checked = false;
		});
		renderOverviewChart();
    });

    $('#all-datatypes').click(function() {
    	$('input[name=dataType]').map(function() {
			dataTypes[this.value] = true;
			this.checked = true;
		});
		renderOverviewChart();
    });

	
}

function initializeTumorTypeSelection( data ) {
	tumorTypes = {};
	
	for ( var i = 0; i < data.details.length; ++i ) {
		tumorTypes[data.details[i].tumorAbbreviation] = true; 
	}	
}

function renderTumorTypeSelectionOverview( data, element ) {		
	var tumorTypeSelectorElements = [];
	
	$( element ).html("");
	
	tumorTypeSelectorElements.push( "<thead><tr><th><i class=\" icon-eye-open\"></th><th colspan=2>Tumor Type&nbsp;<span class=\"btn-group\"><span class=\"btn btn-mini\" id=\"clear-tumortypes\">Clear</span><span class=\"btn btn-mini\" id=\"all-tumortypes\">All</span></span></th></tr></thead>" );
	
	for ( var i = 0; i < data.details.length; ++i ) {
		tumorTypeSelectorElements.push( "<tr>" + "<td>" + "<input type=\"checkbox\" name=\"" + "tumorType" + "\" value=\"" + data.details[i].tumorAbbreviation + "\" " + ( tumorTypes[data.details[i].tumorAbbreviation] == true ? "checked" : "" ) + ">" + "</td>" + "<td>" + data.details[i].tumorAbbreviation + "</td>" + "<td>" 
		+ data.details[i].tumorName + "</td>" + "</tr>" );
	}

	$( "<table/>", {
		id: "tumortype-selector",
		"class": "table table-condensed table-striped",
		"html": tumorTypeSelectorElements.join("\n")
	}).appendTo( element );
	
    $('input[name=tumorType]').click(showCheckedValues);
                
	function showCheckedValues() {
		$('input[name=tumorType]').map(function() {
			tumorTypes[this.value] = this.checked;
		});

		renderOverviewChart();
	}
	
    $('#clear-tumortypes').click(function() {
    	$('input[name=tumorType]').map(function() {
			tumorTypes[this.value] = false;
			this.checked = false;
		});
		renderOverviewChart();
    });

    $('#all-tumortypes').click(function() {
    	$('input[name=tumorType]').map(function() {
			tumorTypes[this.value] = true;
			this.checked = true;
		});
		renderOverviewChart();
    });
	
}



function renderHeader( data, tumorIndex, element ) {	
	// set default element
	var element = element || "#header-container";
		
	// clear the element
	$( element ).html( "" );
	
	// render caption
	$( "<div>", {
		"class": "",
		"html": "<h2>" + data.details[tumorIndex].tumorAbbreviation + " - " + data.details[tumorIndex].tumorName + " (" + data.analysisRun  + ")</h2>"
	}).appendTo( element );
}


function renderHeaderOverview( data, element ) {	
	// set default element
	var element = element || "#header-container-overview";
		
	// clear the element
	$( element ).html( "" );
	
	// render caption
	$( "<div>", {
		"class": "",
		//"html": "<h2>" + "Analysis Run " + data.analysisRun + "/" + "Data Run " + data.dataRun + "</h2>"
		"html": "<h2>" + "Firehose Patient Data (" + data.analysisRun + ")</h2>"
	}).appendTo( element );
}


function renderControls( data, tumorIndex, element ) {	
	// set default element
	var element = element || "#controls-container";

	// clear the element
	$( "#control-button-group" ).html( "" );
	$( "#direct-link-url-container" ).html( "" );		
		
	// links
	$( "<span/>", {
		"class": "",
		"html": "Start <b>Caleydo Stratomex " + data.caleydoVersion + "</b> with Java Web Start and automatically load all data for the selected tumor type."
	}).appendTo( "#webstart-instructions-container" );	
	
	$( "<a/>", {
		"href": data.details[tumorIndex]["Caleydo JNLP"],
		"class": "btn btn-primary",
		"style": "",
		"rel": "tooltip",
		"title": "Run <b>Caleydo Stratomex " + data.caleydoVersion + "</b> with Java WebStart and load all data for <i>" + data.details[tumorIndex].tumorName + "</i> from <i>" + data.analysisRun + "</i>.",
		"html": "<i class=\"icon-play icon-white\"></i>&nbsp;Run"
	}).appendTo( "#control-button-group" );
	
	$( "<a/>", {
		"href": data.details[tumorIndex]["Caleydo Project"], 
		"class": "btn",
		"rel": "tooltip",
		"title": "Download the complete <i>" + data.details[tumorIndex].tumorName + "</i> data set from <i>" + data.analysisRun + "</i> as a Caleydo data package.",
		"html": "<i class=\"icon-download\"></i>"
	}).appendTo( "#control-button-group" );

	$( "<a/>", {
		"href": data.details[tumorIndex]["Firehose Report"], 
		"class": "btn",
		"rel": "tooltip",
		target: "_new",
		"title": "View the Nozzle analysis reports from the <i>" + data.analysisRun + "</i> Firehose analysis run for <i>" + data.details[tumorIndex].tumorName + "</i> on the Firehose website.",
		"html": "<i class=\"icon-file\"></i>"
	}).appendTo( "#control-button-group" );

	$( "<a/>", {
		"class": "btn",
		"rel": "tooltip",
		"id": "direct-link",
		"title": "Get a direct link to with website for the <i>" + data.details[tumorIndex].tumorName + "</i> data set from <i>" + data.analysisRun + "</i>.",
		"html": "<i class=\"icon-retweet\"></i>"
	}).appendTo(  "#control-button-group"  );
	
	$(".btn").tooltip({
		'selector': '',
		'placement': 'bottom'
	});	
	
	$( "#direct-link" ).on( "click", function() {
		$( "#direct-link-url-container" ).html( "" );		
		$( "<div/>", {
			"class": "alert alert-info",
			"html": "<a class=\"close\" data-dismiss=\"alert\" href=\"#\">x</a><b>Direct Link</b> <a href=\"" + getCurrentUrl() + "\">" + getCurrentUrl() + "</a>"
		}).appendTo( "#direct-link-url-container"  );
	});		
}


function renderTable( data, tumorIndex, element ) {
	
	// set default element
	var element = element || "#table-container";
		
	// clear the element
	$( element ).html( "" );
	

	// render table
	$( "<h3/>", {
		"html": "Molecular Data Types",
	}).appendTo( element );
	
	$( "<table/>", {
		"width": "100%",
		"class": "table table-striped table-condensed",
		"html": _genomicHeaderRender() + _.map( data.details[tumorIndex].genomic, _genomicRowRender ).join("\n"),
	}).appendTo( element );

	$( "<h3/>", {
		"html": "Other Data Types",
	}).appendTo( element );
	
	$( "<table/>", {
		"width": "100%",
		"class": "table table-striped",
		"html": _nonGenomicHeaderRender() + _.map( data.details[tumorIndex].nonGenomic, _nonGenomicRowRender ).join("\n"),
	}).appendTo( element );

}


function _genomicHeaderRender() {
		return "<thead><tr><th width=10%>" + "Data Type" + "</th><th></th><th>" + "#Patients" + "</th><th width=40%>" + "Patient Stratifications" + 
										  "</td><th>" + "#Genes" + "</th><th width=40%>" + "Gene Stratifications" + "</th></tr></thead>";			
	}

function _genomicRowRender( dataset, datasetName ) {
	if ( dataset ) {
		return "<tr><th>" + datasetName + "</th><td><span class=\"badge\" style=\"background: " + mapDataTypeToColor( datasetName ) +";\"></span></td><td>" + dataset.sample.count + "</td><td>" + dataset.sample.groupings.join( "; ") + 
										  "</td><td>" + dataset.gene.count + "</td><td>" + dataset.gene.groupings.join( "; ") + "</td></tr>";			
	}
	/*
	else {
		return "<tr><th>" + datasetName + "</th><td>" + "0" + "</td><td>" + "" + "</td><td>" + "0" + "</td><td>" + "" + "</td></tr>";					
	}
	*/
}


function _nonGenomicHeaderRender() {
		return "<thead><tr><th width=10%>" + "Data Type" + "</th><th>" + "#Patients" + "</th><th>" + "Parameters" + 
										  "</th></tr></thead>";			
	}

function _nonGenomicRowRender( dataset, datasetName ) {
	if ( dataset ) {
		return "<tr><th>" + datasetName + "</th><td>" + dataset.count + "</td><td>" + dataset.parameters.join( "; ") + 
										  "</td></tr>";			
	}
	else {
		return "<tr><th>" + datasetName + "</th><td>" + "0" + "</td><td>" + "" + "</td></tr>";					
	}
}


function mapDataTypeToColor( dataType ){
	return ( dataTypeColors[dataType] == null ) ? "#777" : dataTypeColors[dataType];
} 

// test if has contains given value
function _contains( array, value ) {
	for ( var key in array ) {
		if ( array.hasOwnProperty( key ) ) {
			if ( array[key] == value ) {
				return true;
			}
		}
	}
	return false;	
}


function renderOverviewChart() {
	input = runData;
	
	// clear plotting area
	$( "#chart-container" ).html( "" );
				
	// test if at least one tumor type and one data type have been selected
	if ( !_contains( dataTypes, true ) && _contains( tumorTypes, true ) ) {
		$( "<div/>", {
			"class": "alert alert-warning",
			"html": "<a class=\"close\" data-dismiss=\"warning\" href=\"#\"></a><b>Warning</b> Please select <i>at least one data type</i> to display in the data overview chart."
		}).appendTo( "#chart-container"  );
		
		return;
	}	

	if ( !_contains( tumorTypes, true ) && _contains( dataTypes, true ) ) {
		$( "<div/>", {
			"class": "alert alert-warning",
			"html": "<a class=\"close\" data-dismiss=\"warning\" href=\"#\"></a><b>Warning</b> Please select <i>at least one tumor type</i> to display in the data overview chart."
		}).appendTo( "#chart-container"  );
		
		return;
	}	

	if ( !_contains( tumorTypes, true ) && !_contains( dataTypes, true ) ) {
		$( "<div/>", {
			"class": "alert alert-warning",
			"html": "<a class=\"close\" data-dismiss=\"warning\" href=\"#\"></a><b>Warning</b> Please select <i>at least one data type and one tumor type</i> to display in the data overview chart."
		}).appendTo( "#chart-container"  );
		
		return;
	}	

	var newData = []; 
			
	for ( var p in input.details[0].genomic ) {
		if ( input.details[0].genomic.hasOwnProperty( p ) ) {
			if ( dataTypes[p] == false ) {
				continue;
			}
			newList = []
			for ( var t = 0; t < input.details.length; ++t ) {
				
				if ( !tumorTypes[input.details[t].tumorAbbreviation] ) {
					continue;
				}
								
				if ( input.details[t].genomic[p] != null ) {
					newList.push( { tumorType: input.details[t].tumorAbbreviation, dataType: p, x: newList.length, y: parseInt( input.details[t].genomic[p].sample.count ), y0: 0 } );					
				}
				else {
					newList.push( { tumorType: input.details[t].tumorAbbreviation, dataType: p, x: newList.length, y: 0, y0: 0 } );					
				}					
			}
			
			newData.push( newList );
		}
	}

	console.log( newData );
			
	var n = newData.length; // number of layers
	var m = newData[0].length; // number of samples per layer
	var data = newData; //
	var color = function( data ) { 	if ( data == null ) return "orange"; return mapDataTypeToColor( data.dataType ); };
		
	var margin = 40,
	    width = window.innerWidth - 2*margin - .5 - margin,
	    height = 300 - .5 - margin,
	    mx = m,
	    my = d3.max(data, function(d) {
	      return d3.max(d, function(d) {
	        return d.y0 + d.y > 0 ? d.y0 + d.y : 5;
	      });
	    }),
	    mz = d3.max(data, function(d) {
	      return d3.max(d, function(d) {
	        return d.y;
	      });
	    }),
	    x = function(d) { return d.x * width / mx; },
	    y0 = function(d) { return height - d.y0 * height / my; },
	    y1 = function(d) { return height - (d.y + d.y0) * height / my; },
	    y2 = function(d) { return d.y * height / mz; }; // or `my` to not rescale
	
	var vis = d3.select("#chart-container")
	  .append("svg")
	    .attr("width", width + 2*margin)
	    .attr("height", height + margin);
	
	var layers = vis.selectAll("g.layer")
	    .data(data)
	  .enter().append("g")
	    .style("fill", function(d, i) { return color(d[0]); })
	    .attr("class", "layer");
	
	var bars = layers.selectAll("g.bar")
	    .data(function(d) { return d; })
	  .enter().append("g")
	    .attr("class", "bar")
	    .attr("transform", function(d) { return "translate(" + ( x(d) + margin + 2.5 ) + ",0)"; })
		.attr("xlink:title", function(d) {
    		return "<b style=\"color:" + mapDataTypeToColor( d.dataType ) + "\">"  + d.dataType + ": " + d.y + "</b>";
		})	    
	
	bars.append("rect")
	    .attr("width", x({x: .9}))
	    .attr("x", 0)
	    .attr("y", height)
	    .attr("height", 0)
	  .transition()
	    .delay(function(d, i) { return i * 20; })
	    .attr("y", y1)
	    .attr("height", function(d) { return y0(d) - y1(d); });

	$('.bar').tipsy({
        gravity:'s',
        html: true,
        delayIn: 100,
        fade: true
	});
	
	var labels = vis.selectAll("text.label")
	    .data(data[0])
	  .enter().append("text")
	    .attr("class", "axis-label")
	    //.attr("x", x)
	    .attr("transform", function(d) { return "translate(" + ( x(d) + margin ) + ",0)"; })	    
	    .attr("y", function(d,i) { return ( height + 6 + ( 6 * ((i%2)*3) ) ); } )
	    .attr("dx", x({x: .5}))
	    .attr("dy", ".71em")
	    .attr("text-anchor", "middle")	    
	    .attr("font-weight", "normal")	    
	    .text(function(d, i) { return d.tumorType });
	
	vis.append("line")
	    .attr("x1", 0)
	    .attr("x2", width - x({x: .1}))
	    .attr("y1", height)
	    .attr("y2", height);

var yScale = d3.scale.linear().domain([0, my]).range([height,0]);
var xScale = d3.scale.linear().domain([0, mx]).range([0,width]);
	    
//Define Y axis
var yAxis = d3.svg.axis()
                  .scale(yScale)
                  .orient("left")
                  .ticks(5);

var xAxis = d3.svg.axis()
                  .scale(xScale)
                  .orient("top")
                  .ticks(mx)
                  .tickSize(height,0,height)
                  .tickFormat(null);

                  
//Create Y axis
vis.append("g")
    .attr("class", "axis")
    .attr("transform", "translate(" + margin + ",0)")
    .call(yAxis);

vis.append("g")
    .attr("class", "axis")
    .attr("transform", "translate(" + margin + "," + height + ")")
    .call(xAxis);
                  	    
	    
	var group = d3.selectAll("#chart-container");

  group.select("#group")
      .attr("class", "first active");

  group.select("#stack")
      .attr("class", "last");

  group.selectAll("g.layer rect")
    .transition()
      .duration(500)
      .delay(function(d, i) { return (i % m) * 10; })
      .attr("x", function(d, i) { return x({x: .9 * ~~(i / m) / n}); })
      .attr("width", x({x: .9 / n}))
      .each("end", transitionEnd);

  function transitionEnd() {
    d3.select(this)
      .transition()
        .duration(500)
        .attr("y", function(d) { return height - y2(d); })
        .attr("height", y2);
  }
}

function transitionGroup() {
  var m = 21;
  var group = d3.selectAll("#chart-container");

  group.select("#group")
      .attr("class", "first active");

  group.select("#stack")
      .attr("class", "last");

  group.selectAll("g.layer rect")
    .transition()
      .duration(500)
      .delay(function(d, i) { return (i % m) * 10; })
      .attr("x", function(d, i) { return x({x: .9 * ~~(i / m) / n}); })
      .attr("width", x({x: .9 / n}))
      .each("end", transitionEnd);

  function transitionEnd() {
    d3.select(this)
      .transition()
        .duration(500)
        .attr("y", function(d) { return height - y2(d); })
        .attr("height", y2);
  }
}

function transitionStack() {
  var m = 21;
  var stack = d3.select("#chart-container");

  stack.select("#group")
      .attr("class", "first");

  stack.select("#stack")
      .attr("class", "last active");

  stack.selectAll("g.layer rect")
    .transition()
      .duration(500)
      .delay(function(d, i) { return (i % m) * 10; })
      .attr("y", y1)
      .attr("height", function(d) { return y0(d) - y1(d); })
      .each("end", transitionEnd);

  function transitionEnd() {
    d3.select(this)
      .transition()
        .duration(500)
        .attr("x", 0)
        .attr("width", x({x: .9}));
  }
}

/* Inspired by Lee Byron's test data generator. */
function stream_layers(n, m, o) {
  if (arguments.length < 3) o = 0;
  function bump(a) {
    var x = 1 / (.1 + Math.random()),
        y = 2 * Math.random() - .5,
        z = 10 / (.1 + Math.random());
    for (var i = 0; i < m; i++) {
      var w = (i / m - y) * z;
      a[i] += x * Math.exp(-w * w);
    }
  }
  return d3.range(n).map(function() {
      var a = [], i;
      for (i = 0; i < m; i++) a[i] = o + o * Math.random();
      for (i = 0; i < 5; i++) bump(a);
      return a.map(stream_index);
    });
}

/* Another layer generator using gamma distributions. */
function stream_waves(n, m) {
  return d3.range(n).map(function(i) {
    return d3.range(m).map(function(j) {
        var x = 20 * j / m - i / 3;
        return 2 * x * Math.exp(-.5 * x);
      }).map(stream_index);
    });
}

function stream_index(d, i) {
  return {x: i, y: Math.max(0, d)};
}


initialize();
$(window).resize(renderOverviewChart);


// ---------------------------------
})();
// end scope  
