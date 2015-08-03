function LineChart(divID, line_functions, data) {
	var self = this;
	var vis = d3.select(divID);
	var margin = {
			top: 20,
			right: 20,
			bottom: 20,
			left: 50
	};
	var width = 800 - margin.left - margin.right;
	var height = 250 - margin.top - margin.bottom;
	var x = d3.scale.linear().range([0, width]);
	var y = d3.scale.linear().range([height, 0]);
	var xAxis = d3.svg.axis()
		.scale(x)
		.orient("bottom");
	var yAxis = d3.svg.axis()
		.scale(y)
		.orient("left");

	var line = d3.svg.line()
		.x(function(d) { return x(line_functions.x(d)) })
		.y(function(d) { return y(line_functions.y(d)) });

	var svg = d3.select(divID).append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

	svg.append("g")
		.attr("class", "x axis")
		.attr("transform", "translate(0," + height + ")")
		.call(xAxis);
	svg.append("g")
		.attr("class", "y axis")
		.call(yAxis)
		.append("text")
		.attr("transform", "rotate(-90)")
		.attr("y", 6)
		.attr("dy", ".71em")
		.style("text-anchor", "end");

	svg.append("path")
		.attr("class", "line")
		.attr("d", line(data));


	var hoverLineGroup = svg.append("svg:g")
							.attr("class", "hover-line");
	var hoverLine = hoverLineGroup
						.append("svg:line")
						.attr("x1", 10).attr("x2", 10)
						.attr("y1", 0).attr("y2", height);
	var container = document.querySelector(divID);

	var hoverLineXOffset, hoverLineYOffset;

	this.on_hover_change = function(index) {};
	this.set_hover = function(index) {
		var posX = x(index);
		hoverLine.attr("x1", posX).attr("x2", posX);
	};

	$(container).mousemove(function(event) {
		var mouseX = event.pageX - hoverLineXOffset;
		var mouseY = event.pageY - hoverLineYOffset;
		if(mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= height) {
			hoverLine.attr("x1", mouseX).attr("x2", mouseX);
			var index = self.getIndexFromPosition(mouseX);
			self.on_hover_change(index);
		}
	});


	this.on_resize = function() {
		hoverLineXOffset = margin.left + $(container).offset().left;
		hoverLineYOffset = margin.top + $(container).offset().top;
		width = $(divID).width() - margin.left - margin.right;
		height = $(divID).height() - margin.top - margin.bottom;
		d3.select(divID + " svg").attr("width", width + margin.left + margin.right)
								 .attr("height", height + margin.top + margin.bottom)
		hoverLine.attr("y2", height);
		x = d3.scale.linear().range([0, width]);
		y = d3.scale.linear().range([height, 0]);
		xAxis = d3.svg.axis()
			.scale(x)
			.orient("bottom");
		yAxis = d3.svg.axis()
			.scale(y)
			.orient("left");
		svg.selectAll("g .x.axis").call(xAxis)
			.attr("transform", "translate(0," + height + ")");
		svg.selectAll("g .y.axis").call(yAxis);
		self.update_chart();
	};


	this.update_chart = function() {
		x.domain(d3.extent(data, line_functions.x));
		y.domain(d3.extent(data, line_functions.y));
		var svg = d3.select(divID).transition();
		svg.select("g .line")
			.duration(750)
			.attr("d", line(data));
		svg.select(".x.axis")
			.duration(750)
			.call(xAxis);
		svg.select(".y.axis")
			.duration(750)
			.call(yAxis);
	};

	this.getIndexFromPosition = function(xPosition) {

		// get the date on x-axis for the current location
		var xValue = x.invert(xPosition);

		// Calculate the value from this date by determining the 'index'
		// within the data array that applies to this value
		var index = Math.round(xValue - line_functions.x(data[0]));
		index = Math.max(0, Math.min(data.length-1, index));
		return index;
	};

	this.get = function() {
		return {
			x: x,
			y: y,
			xAxis: xAxis,
			yAxis: yAxis,
			svg: svg,
			line: line,
			data: data,
			line_functions: line_functions
		};
	};

	self.on_resize();
}