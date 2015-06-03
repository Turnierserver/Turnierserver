var CELL_TYPE = {
	BOMB: 0,
	EMPTY: 1,
	COVERED: 2,
	COVERED_BOMB: 3
}
var FIELD_SIZE = 16;

var data = [
]

var step = 0;
var god_mode = false;

function getNear(d, x, y) {
	var count = 0;
	var mxl = [-1, 0, 1]
	if (x < 1) {
		mxl.splice(0, 1)
	} else if (x >= (FIELD_SIZE-1)) {
		mxl.splice(-1, 1)
	}
	var myl = [-1, 0, 1]
	if (y < 1) {
		myl.splice(0, 1)
	} else if (y >= (FIELD_SIZE-1)) {
		myl.splice(-1, 1)
	}
	for (var ix = mxl.length - 1; ix >= 0; ix--) {
		for (var iy = myl.length - 1; iy >= 0; iy--) {
			var mx = mxl[ix];
			var my = myl[iy];
			if (d[x+mx][y+my] === CELL_TYPE.BOMB || d[x+mx][y+my] === CELL_TYPE.COVERED_BOMB) {
				count++;
			}
		};
	};
	return count
}

function draw() {
	update()

	var canvas = document.getElementById('canvas');
	var ctx = canvas.getContext('2d');
	ctx.font = "45px serif";
	ctx.textAlign = 'center';
	ctx.textBaseline = 'middle';


	canvas.width = $("#canvas").width()
	canvas.height = $("#canvas").height()

	var SX = canvas.width
	var SY = canvas.height


	var c_sx = (SX/FIELD_SIZE)
	var c_sy = (SY/FIELD_SIZE)

	var edgesize = 0.1

	if (data.length < 1) {return;}

	var d = data[step];

	for (var x = FIELD_SIZE - 1; x >= 0; x--) {
		for (var y = FIELD_SIZE - 1; y >= 0; y--) {
			var c_x = x * c_sx
			var c_y = y * c_sy
			switch (d.cells[x][y]) {
				case CELL_TYPE.BOMB:
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
					break;
				case CELL_TYPE.EMPTY:
					ctx.fillStyle = "rgb(200, 200, 200)";
					ctx.fillRect(c_x, c_y, c_sx, c_sy);
					ctx.fillStyle = "rgb(220, 220, 220)";
					ctx.fillRect(c_x+c_sx*edgesize*0.5, c_y+c_sy*edgesize*0.5, c_sx-c_sx*edgesize, c_sy-c_sy*edgesize);
					var nearby_bombs = getNear(d.cells, x, y);
					if (nearby_bombs >= 1) {
						ctx.fillStyle = "black";
						ctx.fillText(nearby_bombs, c_x+(c_sx/2), c_y+(c_sy/2))
					}
					break;
				case CELL_TYPE.COVERED_BOMB:
					if (god_mode) {
						ctx.fillStyle = "rgb(100, 0, 0)";
						ctx.fillRect(c_x, c_y, c_sx, c_sy);
						break;
					}
				case CELL_TYPE.COVERED:
					ctx.fillStyle = "rgb(200, 200, 200)";
					ctx.fillRect(c_x, c_y, c_sx, c_sy);
					ctx.fillStyle = "black";
					ctx.fillRect(c_x+c_sx*edgesize*0.5, c_y+c_sy*edgesize*0.5, c_sx-c_sx*edgesize, c_sy-c_sy*edgesize);
					break;
			}
		};
	};

}


function update() {
	if (step >= data.length) {
		step = data.length-1;
		$(e.target).val(step)
	}
}


var evtSrc = new EventSource("http://localhost:5000/api/game/1/log");

evtSrc.onerror = function () {
	console.log("SSE Err")
	evtSrc.close()
};

evtSrc.addEventListener("state", function(e) {
	console.log(e.data)
	d = JSON.parse(e.data)
	console.log(d)
	console.log(d.status)
	data.push(d.data)
	$('#download_progress').progress({
		percent: d.progress*100
	});
	$("#step_slider").attr("max", data.length-1)
	draw();
})

evtSrc.addEventListener("stream_stopped", function (e) {
	console.log(e);
	console.log("stream_stopped")
	evtSrc.close()
})

evtSrc.addEventListener("finished_transmitting", function(e) {
	console.log("finished_transmitting")
	$("#download_progress").progress({
		percent: 100
	})
})

$(document).ready(function () {
	console.log("body loaded...")
	$("#step_slider").change(function (e) {
		console.log($(e.target).val())
		step = $(e.target).val();
		draw()
	})
})