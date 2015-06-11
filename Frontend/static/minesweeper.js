var CELL_TYPE = {
	COVERED: "COVERED",
	EMPTY: "EMPTY"
};

var FIELD_SIZE = 3;

var data = [];


var panes = []

function add_pane(name) {
	d = {
		step: 0,
		data: [],
		name: name,
		is_playing: false,
		canvas: document.getElementById('canvas_'+name)
	}
	d.ctx = d.canvas.getContext('2d');

	d.ctx.font = "75px serif";
	d.ctx.textAlign = 'center';
	d.ctx.textBaseline = 'middle';

	panes.push(d)
}

add_pane('left');
add_pane('right');



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
	ctx.fillStyle = "rgba(0, 255, 0, 0.75)";
	ctx.beginPath();
	ctx.moveTo(c_x + c_sx, c_y);
	ctx.lineTo(c_x + c_sx, c_y + c_sx * (edgesize*2));
	ctx.lineTo(c_x + c_sx * (1 - edgesize*2), c_y);
	ctx.fill();
}


function draw(pane) {
	update(pane);

	pane.canvas.width = $("#canvas_" + pane.name).width();
	pane.canvas.height = $("#canvas_" + pane.name).height();

	var SX = pane.canvas.width;
	var SY = pane.canvas.height;

	var c_sx = (SX/FIELD_SIZE);
	var c_sy = (SY/FIELD_SIZE);

	var edgesize = 0.1;

	if (data.length < 1) {return;}

	var d = pane.data[pane.step];

	for (var x = FIELD_SIZE - 1; x >= 0; x--) {
		for (var y = FIELD_SIZE - 1; y >= 0; y--) {
			var c_x = x * c_sx;
			var c_y = y * c_sy;
			switch (d.field[x][y].type) {
				case CELL_TYPE.BOMB:
					drawBomb()(c_x, c_y, c_sx, c_sy, edgesize);
					break;
				case CELL_TYPE.EMPTY:
					drawEmpty(c_x, c_y, c_sx, c_sy, edgesize, d.field[x][y].bombsAround);
					break;
				case CELL_TYPE.COVERED:
					drawCovered(c_x, c_y, c_sx, c_sy, edgesize);
					break;
			}
			if (d.field[x][y].flagged) {
				drawFlagged(c_x, c_y, c_sx, c_sy, edgesize);
			}
		}
	}

}

function draw_panes() {
	for (var i = panes.length - 1; i >= 0; i--) {
		draw(panes[i]);
	};
}


function update(pane) {
	var d = pane.data[pane.step];
	//$("#ai_left_output").val(d.ai_logs[0])
	//$("#ai_right_output").val(d.ai_logs[1])

	if (pane.is_playing) {
		$("#play_button").addClass("active");
		$("#pause_button").removeClass("active");
	} else {
		$("#play_button").removeClass("active");
		$("#pause_button").addClass("active");
	}
}


var evtSrc = new EventSource(window.location.origin + "/api/game/1/log");

evtSrc.onerror = function () {
	console.log("SSE Err");
	evtSrc.close();
};

evtSrc.addEventListener("state", function(e) {
	console.log(e.data);
	d = JSON.parse(e.data);
	console.log(d);
	console.log(d.aiID)
	data.push(d.data);
	$('#download_progress').progress({
		percent: d.progress*100
	});
	$("#step_slider").attr("max", data.length-1);
	draw_panes();
});


evtSrc.addEventListener("stream_stopped", function (e) {
	console.log(e);
	console.log("stream_stopped");
	evtSrc.close();
});

evtSrc.addEventListener("finished_transmitting", function(e) {
	console.log("finished_transmitting");
	$("#download_progress").progress({
		percent: 100
	});
});

$(document).ready(function () {
	$("#step_slider").change(function (e) {
		console.log($(e.target).val());
		step = $(e.target).val();
		draw();
	});
});