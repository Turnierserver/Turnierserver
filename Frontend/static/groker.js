var pane = {
	step: 0,
	data: [],
	name: name,
	is_playing: false,
};


var complete_diff_data = [];
var diff_chart = new Chartist.Line('#unterschied', {
	labels: ['1', '2', '3', '4'],
	series: [
		[-50, 0, 50, -100],
	]
}, {
	fullWidth: true,
	chartPadding: {
		right: 40
	}
});


function set_chart_data() {
	//diff_data.datasets[0].data = complete_diff_data.slice(0, pane.step)
	start = Math.max(0, pane.step - 20);
	end = Math.min(complete_diff_data.length-1, pane.step + 20)
	diff_chart.data.labels = []
	diff_chart.data.series[0] = []
	for (var i = start; i < end; i++) {
		diff_chart.data.labels.push(i);
		diff_chart.data.series[0].push(complete_diff_data[i]);
	};
	diff_chart.update();
	// = complete_diff_data.slice(0, pane.step)
}

function draw() {
	update();
	var d = pane.data[pane.step];
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
	}
});


function update() {
	var d = pane.data[pane.step];
	$.map(d.output, function(value, key) {
		$("#ai_"+key+"_output").val(value);
	})

	if (pane.is_playing) {
		$("#play_button").addClass("active");
		$("#pause_button").removeClass("active");
	} else {
		$("#play_button").removeClass("active");
		$("#pause_button").addClass("active");
	}

	set_chart_data();
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
		NProgress.inc()
		console.log(e.data);
		d = JSON.parse(e.data);
		console.log(d);
		pane.data.push(d);
		//NProgress.set(d.progress);
		$("#step_slider").slider("option", "max", pane.data.length-1);
		var values = $.map(d.wonChips, function (value, key) {return value})
		complete_diff_data.push(values[0] - values[1])
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
