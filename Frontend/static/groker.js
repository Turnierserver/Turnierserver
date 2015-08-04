// https://remysharp.com/2010/07/21/throttling-function-calls
function throttle(fn, threshhold, scope) {
	threshhold || (threshhold = 250);
	var last,
			deferTimer;
	return function () {
		var context = scope || this;
		var now = +new Date,
				args = arguments;
		if (last && now < last + threshhold) {
			// hold on to it
			clearTimeout(deferTimer);
			deferTimer = setTimeout(function () {
				last = now;
				fn.apply(context, args);
			}, threshhold);
		} else {
			last = now;
			fn.apply(context, args);
		}
	};
}


var pane = {
	step: 0,
	data: [],
	name: name,
	is_playing: false,
};


var data = [];

var diff_chart = new LineChart("#diff_chart",
	[{
		x: function (d) { return d.step; },
		y: function (d) { return d.diff; },
		label: function() { return; }
	}], data
);

$("#spielspezifisch").on("click", diff_chart.on_resize);
$(window).on("resize", throttle(diff_chart.on_resize, 1000));
diff_chart.update_chart = throttle(diff_chart.update_chart, 750);

var abs_chart = new LineChart("#abs_chart",
	[{
		x: function (d) { return d.step; },
		y: function (d) { return d.ai1_abs; },
		label: function() { return "Ai1"; }
	},
	{
		x: function (d) { return d.step; },
		y: function (d) { return d.ai2_abs; },
		label: function() { return "Ai2"; }
	}], data
);

$("#spielspezifisch").on("click", abs_chart.on_resize);
$(window).on("resize", throttle(abs_chart.on_resize, 1000));
abs_chart.update_chart = throttle(abs_chart.update_chart, 750);

var td_chart = new LineChart("#td_chart",
	[{
		x: function (d) { return d.step; },
		y: function (d) { return d.ai1_td; },
		label: function() { return "Ai1"; }
	},
	{
		x: function (d) { return d.step; },
		y: function (d) { return d.ai2_td; },
		label: function() { return "Ai2"; }
	}], data
);

$("#rechenpunkte").on("click", td_chart.on_resize);
$(window).on("resize", throttle(td_chart.on_resize, 1000));
td_chart.update_chart = throttle(td_chart.update_chart, 750);


var charts = [diff_chart, abs_chart, td_chart];

function on_hover_change(index) {
	pane.step = index;
	$("#step_slider").slider("option", "value", index);
	draw();
	$.each(charts, function () {
		this.set_hover(index);
	});
}

$.map(charts, function(chart) {
	chart.on_hover_change = on_hover_change;
});

function draw() {
	update();
	//var d = pane.data[pane.step];
}

$("#step_slider").slider({
	range: "max",
	min: 0,
	max: 0,
	value: 0,
	step: 1,
	slide: function (event, ui) {
		pane.step = ui.value;
		draw();
		$.map(charts, function(chart) {
			chart.set_hover(pane.step);
		});
	}
});


function update() {
	var d = pane.data[pane.step];
	$.map(d.output, function(value, key) {
		var id = key.slice(0, key.indexOf("v"));
		$("#ai_" + id + "_output").val(value);
	});

	if (pane.is_playing) {
		$("#play_button").addClass("active");
		$("#pause_button").removeClass("active");
	} else {
		$("#play_button").removeClass("active");
		$("#pause_button").addClass("active");
	}
}

$(document).ready(function () {
	console.log("Streaming game data from", window.location.origin + $("#game_script").attr("stream"));
	var evtSrc = new EventSource(window.location.origin + $("#game_script").attr("stream"));
	NProgress.configure({ trickle: false });
	NProgress.start();

	evtSrc.onerror = function () {
		console.log("SSE Err");
		evtSrc.close();
		NProgress.done();
	};

	evtSrc.addEventListener("state", function(e) {
		NProgress.inc();
		console.log(e.data);
		d = JSON.parse(e.data);
		console.log(d);
		pane.data.push(d);
		//NProgress.set(d.progress);
		$("#step_slider").slider("option", "max", pane.data.length-1);
		var values = $.map(d.wonChips, function (value, key) {return value});
		var d = {};
		d.diff = Math.abs(values[0] - values[1]);
		d.ai1_abs = values[0];
		d.ai2_abs = values[1];
		d.ai1_gain = values[0];
		d.ai2_gain = values[1];
		if (data.length > 0) {
			d.ai1_gain -= data[data.length-1].ai1_abs;
			d.ai2_gain -= data[data.length-1].ai2_abs;
		}
		d.ai1_td = Math.random();
		d.ai2_td = Math.random();
		d.step = pane.data.length;
		data.push(d);
		$.map(charts, function (chart) {
			chart.update_chart();
		});
		draw();
	});


	evtSrc.addEventListener("stream_stopped", function (e) {
		console.log(e);
		console.log("stream_stopped");
		evtSrc.close();
		NProgress.done();
	});

	// ## in ne generelle lib verschieben
	evtSrc.addEventListener("game_finished", function(e) {
		console.log("game_finished", e.data);
		$("#finished_message").show();
		$("#finished_message").find("a").attr("href", e.data);
	});

	// ## in ne generelle lib verschieben
	evtSrc.addEventListener("qualified", function(e) {
		$("#qualified_message").show();
		NProgress.done();
	});

	evtSrc.addEventListener("finished_transmitting", function(e) {
		console.log("finished_transmitting");
		NProgress.done();
	});
});
