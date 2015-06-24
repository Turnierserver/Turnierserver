var CELL_TYPE = {
	COVERED: "COVERED",
	EMPTY: "EMPTY",
	BOMB: "BOMB"
};

var pane = {
	step: 0,
	data: [],
	name: name,
	is_playing: false,
	canvas: document.getElementById('canvas')
};
pane.ctx = pane.canvas.getContext('2d');
pane.aiID = $("#canvas").attr("aiID");

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


function draw() {
	update();

	pane.canvas.width = $("#canvas").width();
	pane.canvas.height = $("#canvas").height();


	if (pane.data.length < 1) {return;}
	var d = pane.data[pane.step];

	var SX = pane.canvas.width;
	var SY = pane.canvas.height;

	var FIELD_SIZE = d.field.length;

	var c_sx = (SX/FIELD_SIZE);
	var c_sy = (SY/FIELD_SIZE);

	var ctx = pane.ctx;
	ctx.font = (SX/FIELD_SIZE) + "px serif";
	ctx.textAlign = 'center';
	ctx.textBaseline = 'middle';

	var edgesize = 0.1;

	for (var x = FIELD_SIZE - 1; x >= 0; x--) {
		for (var y = FIELD_SIZE - 1; y >= 0; y--) {
			var c_x = x * c_sx;
			var c_y = y * c_sy;
			switch (d.field[x][y].type) {
				case CELL_TYPE.BOMB:
					drawBomb(ctx, c_x, c_y, c_sx, c_sy, edgesize);
					break;
				case CELL_TYPE.EMPTY:
					drawEmpty(ctx, c_x, c_y, c_sx, c_sy, edgesize, d.field[x][y].bombsAround);
					break;
				case CELL_TYPE.COVERED:
					drawCovered(ctx, c_x, c_y, c_sx, c_sy, edgesize);
					break;
			}
			if (d.field[x][y].flagged) {
				drawFlagged(ctx, c_x, c_y, c_sx, c_sy, edgesize);
			}
		}
	}

}


function update() {
	var d = pane.data[pane.step];
	if (d !== undefined) {
		$("#ai_"+pane.name+"_output").val(d.output);
	}

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

	evtSrc.onerror = function () {
		console.log("SSE Err");
		evtSrc.close();
	};

	evtSrc.addEventListener("state", function(e) {
		console.log(e.data);
		d = JSON.parse(e.data);
		console.log(d);
		console.log(d);
		pane.data.push(d);
		$('#download_progress').progress({
			percent: d.progress*100
		});
		$("#step_slider").slider("option", "max", pane.data.length-1);
		draw();
	});


	evtSrc.addEventListener("stream_stopped", function (e) {
		console.log(e);
		console.log("stream_stopped");
		evtSrc.close();
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
	});

	evtSrc.addEventListener("finished_transmitting", function(e) {
		console.log("finished_transmitting");
		$("#download_progress").progress({
			percent: 100
		});
	});
});
