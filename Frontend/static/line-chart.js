'use strict'

var $
var d3

function LineChart (divID, line_functions, data, ylabel, log_scale) {
	let self = this
	let current_index
	let vis = d3.select(divID)
	let margin = {
			top: 20,
			right: 20,
			bottom: 20,
			left: 50
	}
	let width = 800 - margin.left - margin.right
	let height = 250 - margin.top - margin.bottom
	let x, y
	x = d3.scale.linear().range([0, width])
	if (log_scale) {
		y = d3.scale.log().range([height, 0]).clamp(true).nice()
	} else {
		y = d3.scale.linear().range([height, 0])
	}
	let xAxis = d3.svg.axis()
		.scale(x)
		.orient("bottom")
	let yAxis = d3.svg.axis()
		.scale(y)
		.orient("left")

	let lines = $.map(line_functions, function(line_function) {
		return d3.svg.line()
			.x(function (d) {
				return x(line_function.x(d))
			})
			.y(function (d) {
				return y(line_function.y(d))
			})
	})

	let svg = d3.select(divID).append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")")

	svg.append("g")
		.attr("class", "x axis")
		.attr("transform", "translate(0," + height + ")")
		.call(xAxis)


	svg.append("g")
		.attr("class", "y axis")
		.call(yAxis)
		.append("text")
		.attr("transform", "rotate(-90)")
		.attr("y", 6)
		.attr("dy", ".71em")
		.style("text-anchor", "end")


	$.each(lines, function(index) {
		svg.append("path")
			.attr("class", "line line" + (index + 1))
			.attr("d", lines[index](data))
			.attr("data-index", index)
	})

	// legend

	let label_group = svg.append("svg:g")
		.attr("class", "legend-group")
		.selectAll("g")
		.data(line_functions)
		.enter().append("g")
		.attr("class", "legend-labels")

	label_group.append("svg:text")
		.attr("class", function (d, i) {
			return "legend legend" + (i+1)
		})


	svg.append("svg:text")
				.attr("class", "y-label")
				.attr("text-anchor", "end") // set at end so we can position at far right edge and add text from right to left
				.attr("font-size", "10")
				.attr("y", -4)
				.attr("x", width)

	// hover-line

	let hoverLineGroup = svg.append("svg:g")
							.attr("class", "hover-line")
	let hoverLine = hoverLineGroup
						.append("svg:line")
						.attr("x1", 10).attr("x2", 10)
						.attr("y1", 0).attr("y2", height)
	let container = document.querySelector(divID)

	let hoverLineXOffset, hoverLineYOffset

	this.on_hover_change = function(index) {}
	this.set_hover = function(index) {
		let posX = x(index + line_functions[0].x(data[0]))
		hoverLine.attr("x1", posX).attr("x2", posX)
		self.setLegendText(index)
		current_index = index
	}

	$(container).mousemove(function(event) {
		let mouseX = event.pageX - hoverLineXOffset
		let mouseY = event.pageY - hoverLineYOffset
		if(mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= height) {
			hoverLine.attr("x1", mouseX).attr("x2", mouseX)
			if (data.length > 0) {
				let index = self.getIndexFromPosition(mouseX)
				self.on_hover_change(index)
			}
		}
	})


	this.on_resize = function() {
		hoverLineXOffset = margin.left + $(container).offset().left
		hoverLineYOffset = margin.top + $(container).offset().top
		width = $(divID).width() - margin.left - margin.right
		height = $(divID).height() - margin.top - margin.bottom
		d3.select(divID + " svg").attr("width", width + margin.left + margin.right)
								 .attr("height", height + margin.top + margin.bottom)
		hoverLine.attr("y2", height)
		x = d3.scale.linear().range([0, width])
		if (log_scale) {
			y = d3.scale.log().range([height, 0]).clamp(true).nice()
		} else {
			y = d3.scale.linear().range([height, 0])
		}
		self.set_axis_domain()
		xAxis = d3.svg.axis()
			.scale(x)
			.orient("bottom")
		yAxis = d3.svg.axis()
			.scale(y)
			.orient("left")
		svg.selectAll("g .x.axis").call(xAxis)
			.attr("transform", "translate(0," + height + ")")


		svg.selectAll("g .y.axis").call(yAxis)
		self.update_chart()


		// legend
		svg.selectAll("text.y-label").attr("x", width)
		svg.selectAll("text.legend").attr("y", () => height + 40)
		if (current_index) {
			self.set_hover(current_index)
		}
	}

	this.set_axis_domain = function() {
		let xvars = d3.extent(data, line_functions[0].x)
		let yvars = d3.extent(data, line_functions[0].y)
		$.map(line_functions, function(line_function) {
			let xvars_ = d3.extent(data, line_function.x)
			let yvars_ = d3.extent(data, line_function.y)
			xvars[0] = Math.min(xvars_[0], xvars[0])
			xvars[1] = Math.max(xvars_[1], xvars[1])
			yvars[0] = Math.min(yvars_[0], yvars[0])
			if (log_scale)
				yvars[0] = Math.max(1, yvars[0])
			yvars[1] = Math.max(yvars_[1], yvars[1])
		})
		x.domain(xvars)
		y.domain(yvars)
	}

	this.update_chart = function() {
		self.set_axis_domain()
		let svg = d3.select(divID).transition()
		svg.selectAll("g .line")
			.duration(750)
			.attr("d", function() {
				let ret = lines[$(this).attr("data-index")](data)
				if (/NaN/.test(ret)) {
					return
				}
				return ret
			})
		svg.select(".x.axis")
			.duration(750)
			.call(xAxis)
		if (log_scale) {
			svg.select(".y.axis")
				.duration(750)
				.call(yAxis)
			if (svg.selectAll(".y.axis .tick text")[0].length) {
				svg.selectAll(".y.axis .tick text")
					.text(null)
					.filter(function(d) {return d / Math.pow(10, Math.ceil(Math.log(d) / Math.LN10 - 1e-12)) === 1;})
					.text(function(d) {return d})
			}
		} else {
			svg.select(".y.axis")
				.duration(750)
				.call(yAxis)
		}
	}

	this.getIndexFromPosition = function(xPosition) {

		// get the date on x-axis for the current location
		let xValue = x.invert(xPosition)

		// Calculate the value from this date by determining the 'index'
		// within the data array that applies to this value
		let index = Math.round(xValue - line_functions[0].x(data[0]))
		index = Math.max(0, Math.min(data.length-1, index))
		return index
	}

	this.setLegendText = function(index) {
		let labelValueWidths = []
		svg.selectAll("text.legend")
			.text(function(d, i) {
				return d.label(data[index])
			})

		let cumulativeWidth = 0
		svg.selectAll("text.legend")
				.attr("x", function(d, i) {
					// return it at the width of previous labels (where the last one ends)
					let returnX = cumulativeWidth
					// increment cumulative to include this one + the value label at this index
					cumulativeWidth += this.getComputedTextLength() + 16
					// store where this ends
					return returnX
				})

		// remove last bit of padding from cumulativeWidth
		cumulativeWidth = cumulativeWidth - 8

		svg.select('text.y-label').text(ylabel(data[index]))

		svg.selectAll("g.legend-group g")
			.attr("transform", "translate(" + (width - cumulativeWidth) +", 0)")
	}

	// TODO: remove .get()
	this.get = function() {
		return {
			x: x,
			y: y,
			xAxis: xAxis,
			yAxis: yAxis,
			svg: svg,
			lines: lines,
			data: data,
			line_functions: line_functions
		}
	}

	self.on_resize()
}
