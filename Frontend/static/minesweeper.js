var CELL_TYPE = {
	COVERED: "COVERED",
	EMPTY: "EMPTY",
	BOMB: "BOMB"
};

var panes = [];
var panes_lookup = {};

function add_pane(name) {
	var d = {
		step: 0,
		data: [],
		name: name,
		is_playing: false,
		canvas: document.getElementById('canvas_'+name)
	};
	d.ctx = d.canvas.getContext('2d');
	d.aiID = $("#canvas_"+name).attr("aiID");

	$("#step_slider_"+name).slider({
		range: "max",
		min: 0,
		max: 0,
		value: 0,
		step: 1,
		slide: function (event, ui) {
			d.step = ui.value;
			draw_panes();
		}
	});

	panes_lookup[d.aiID] = panes.length;
	panes.push(d);
	return d;
}




function drawBomb(ctx, c_x, c_y, c_sx, c_sy, edgesize) {
	ctx.fillStyle = "rgb(200, 200, 200)";
	ctx.fillRect(c_x, c_y, c_sx, c_sy);
	ctx.fillStyle = "rgb(220, 220, 220)";
	ctx.fillRect(c_x+c_sx*edgesize*0.5, c_y+c_sy*edgesize*0.5, c_sx-c_sx*edgesize, c_sy-c_sy*edgesize);
	ctx.beginPath();
	ctx.arc(c_x+c_sx/2, c_y+c_sy/2, c_sx/3, 0, 2 * Math.PI, false);
	ctx.fillStyle = 'black';
	ctx.fill();
	ctx.lineWidth = 2;
	ctx.strokeStyle = 'red';
	ctx.stroke();
}

function drawEmpty(ctx, c_x, c_y, c_sx, c_sy, edgesize, nearby_bombs) {
	ctx.fillStyle = "rgb(200, 200, 200)";
	ctx.fillRect(c_x, c_y, c_sx, c_sy);
	ctx.fillStyle = "rgb(220, 220, 220)";
	ctx.fillRect(c_x+c_sx*edgesize*0.5, c_y+c_sy*edgesize*0.5, c_sx-c_sx*edgesize, c_sy-c_sy*edgesize);
	if (nearby_bombs >= 1) {
		ctx.fillStyle = "black";
		ctx.fillText(nearby_bombs, c_x+(c_sx/2), c_y+(c_sy/2));
	}
}

function drawCovered(ctx, c_x, c_y, c_sx, c_sy, edgesize) {
	ctx.fillStyle = "rgb(200, 200, 200)";
	ctx.fillRect(c_x, c_y, c_sx, c_sy);
	ctx.fillStyle = "black";
	ctx.fillRect(c_x+c_sx*edgesize*0.5, c_y+c_sy*edgesize*0.5, c_sx-c_sx*edgesize, c_sy-c_sy*edgesize);
}

function drawFlagged(ctx, c_x, c_y, c_sx, c_sy, edgesize) {
	ctx.fillStyle = "rgba(0, 255, 0, 0.8)";
	ctx.beginPath();
	ctx.moveTo(c_x + c_sx, c_y);
	ctx.lineTo(c_x + c_sx, c_y + c_sx * (edgesize*3));
	ctx.lineTo(c_x + c_sx * (1 - edgesize*3), c_y);
	ctx.fill();
}


function draw(pane) {
	update(pane);

	pane.canvas.width = $("#canvas_" + pane.name).width();
	pane.canvas.height = $("#canvas_" + pane.name).height();


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

function draw_panes() {
	for (var i = panes.length - 1; i >= 0; i--) {
		draw(panes[i]);
	}
}


function update(pane) {
	var d = pane.data[pane.step];
	if (d !== undefined) {
		$("#ai_"+pane.name+"_output").val(d.output);
	}

	if (pane.is_playing) {
		$("#play_button_"+pane.name).addClass("active");
		$("#pause_button_"+pane.name).removeClass("active");
	} else {
		$("#play_button_"+pane.name).removeClass("active");
		$("#pause_button_"+pane.name).addClass("active");
	}
}

$(document).ready(function () {
	add_pane('left');
	add_pane('right');

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
		console.log(d.aiID);
		var pane = panes[panes_lookup[d.aiID]];
		pane.data.push(d);
		$('#download_progress_'+pane.name).progress({
			percent: d.progress*100
		});
		$("#step_slider_"+pane.name).slider("option", "max", pane.data.length-1);
		draw_panes();
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

	evtSrc.addEventListener("finished_transmitting", function(e) {
		console.log("finished_transmitting");
		$("#download_progress").progress({
			percent: 100
		});
	});
});