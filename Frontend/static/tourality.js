'use strict'

var $
var LineChart
var ais
var NProgress
var EventSource

const CELL_TYPE = {
  EMPTY: 0,
  COIN: 1,
  OBSTACLE: 2
}

// https://remysharp.com/2010/07/21/throttling-function-calls
function throttle (fn, threshhold, scope) {
  threshhold || (threshhold = 250)
  let last
  let deferTimer
  return function () {
    let context = scope || this
    let now = +new Date()
    let args = arguments
    if (last && now < last + threshhold) {
      // hold on to it
      clearTimeout(deferTimer)
      deferTimer = setTimeout(function () {
        last = now
        fn.apply(context, args)
      }, threshhold)
    } else {
      last = now
      fn.apply(context, args)
    }
  }
}

let pane = {
  step: 0,
  data: [],
  is_playing: false,
  canvas: {}
}

let data = []
let ai_crashes = []

let diff_chart = new LineChart('#diff_chart',
  [{
    x: function (d) { return d.step },
    y: function (d) { return d.diff },
    label: function (d) {
      if (d.ai1_abs > d.ai2_abs) {
        return 'Diff: +' + d.diff + ' Punkte für ' + d.ai1_name
      } else if (d.ai2_abs > d.ai1_abs) {
        return 'Diff: +' + d.diff + ' Punkte für ' + d.ai2_name
      }
      return 'Unentschieden'
    }
  }], data, function (d) { return 'Schritt: ' + d.step }
)

$('#spielspezifisch').on('click', diff_chart.on_resize)
$(window).on('resize', throttle(diff_chart.on_resize, 1000))
diff_chart.update_chart = throttle(diff_chart.update_chart, 750)

let gain_chart = new LineChart('#gain_chart',
  [
    {
      x: function (d) { return d.step },
      y: function (d) { return d.ai1_gain },
      label: function (d) { return d.ai1_name + ': ' + d.ai1_gain }
    },
    {
      x: function (d) { return d.step },
      y: function (d) { return d.ai2_gain },
      label: function (d) { return d.ai2_name + ': ' + d.ai2_gain }
    }
  ], data, function (d) { return 'Schritt: ' + d.step }
)

$('#spielspezifisch').on('click', gain_chart.on_resize)
$(window).on('resize', throttle(gain_chart.on_resize, 1000))
gain_chart.update_chart = throttle(gain_chart.update_chart, 750)

let td_chart = new LineChart('#td_chart',
  [
    {
      x: function (d) { return d.step },
      y: function (d) { return d.ai1_td },
      label: function (d) { return d.ai1_name + ': ' + d.ai1_td + 'ms' }
    },
    {
      x: function (d) { return d.step },
      y: function (d) { return d.ai2_td },
      label: function (d) { return d.ai2_name + ': ' + d.ai2_td + 'ms' }
    }
  ], data, function (d) { return 'Schritt: ' + d.step }, true
)

$('#rechenpunkte').on('click', td_chart.on_resize)
$(window).on('resize', throttle(td_chart.on_resize, 1000))
td_chart.update_chart = throttle(td_chart.update_chart, 750)

let tabs_chart = new LineChart('#tabs_chart',
  [
    {
      x: function (d) { return d.step },
      y: function (d) { return d.ai1_tabs },
      label: function (d) { return d.ai1_name + ': ' + d.ai1_tabs }
    },
    {
      x: function (d) { return d.step },
      y: function (d) { return d.ai2_tabs },
      label: function (d) { return d.ai2_name + ': ' + d.ai2_tabs }
    }
  ], data, function (d) { return 'Schritt: ' + d.step }
)

$('#rechenpunkte').on('click', tabs_chart.on_resize)
$(window).on('resize', throttle(tabs_chart.on_resize, 1000))
tabs_chart.update_chart = throttle(tabs_chart.update_chart, 750)

let charts = [diff_chart, gain_chart, td_chart, tabs_chart]

setTimeout(function () {
  charts.forEach((chart) => chart.on_resize())
}, 250) // ...

function on_hover_change (index) {
  pane.step = index
  $('#step_slider').slider('option', 'value', index)
  update()
  $.each(charts, function () {
    this.set_hover(index)
  })
}

$.map(charts, function (chart) {
  chart.on_hover_change = on_hover_change
})

$('#step_slider').slider({
  range: 'max',
  min: 0,
  max: 0,
  value: 0,
  step: 1,
  slide: function (event, ui) {
    pane.step = ui.value
    update()
    $.map(charts, function (chart) {
      chart.set_hover(pane.step)
    })
  }
})

function drawEmpty (ctx, c_x, c_y, c_sx, c_sy, edgesize) {
  ctx.fillStyle = 'rgb(200, 200, 200)'
  ctx.fillRect(c_x, c_y, c_sx, c_sy)
  ctx.fillStyle = 'rgb(220, 220, 220)'
  ctx.fillRect(c_x+c_sx*edgesize*0.5, c_y+c_sy*edgesize*0.5, c_sx-c_sx*edgesize, c_sy-c_sy*edgesize)
}

function drawCovered (ctx, c_x, c_y, c_sx, c_sy, edgesize) {
  ctx.fillStyle = 'rgb(200, 200, 200)'
  ctx.fillRect(c_x, c_y, c_sx, c_sy)
  ctx.fillStyle = 'black'
  ctx.fillRect(c_x+c_sx*edgesize*0.5, c_y+c_sy*edgesize*0.5, c_sx-c_sx*edgesize, c_sy-c_sy*edgesize)
}

function draw (data) {

  pane.canvas.width = $('#canvas').width()
  pane.canvas.height = $('#canvas').height()

  if (pane.data.length < 1) return
  let d = pane.data[pane.step]

  let SX = pane.canvas.width
  let SY = pane.canvas.height

  let FIELD_SIZE = d.field.length

  let c_sx = SX / FIELD_SIZE
  let c_sy = SY / FIELD_SIZE

  let ctx = pane.ctx
  ctx.font = (SX / FIELD_SIZE) + 'px serif'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'

  let edgesize = 0.1

  for (let x = FIELD_SIZE - 1; x >= 0; x--) {
    for (let y = FIELD_SIZE - 1; y >= 0; y--) {
      let c_x = x * c_sx
      let c_y = y * c_sy
      switch (d.field[x][y].type) {
        case CELL_TYPE.EMPTY:
          break
        case CELL_TYPE.COIN:
          drawEmpty(ctx, c_x, c_y, c_sx, c_sy, edgesize, d.field[x][y].bombsAround)
          break
        case CELL_TYPE.OBSTACLE:
          drawCovered(ctx, c_x, c_y, c_sx, c_sy, edgesize)
          break
      }
    }
  }
}

function update () {
  let d = pane.data[pane.step]

  $.map(ais, function (ai_name, ai_id) {
    for (let i = 0; i < ai_crashes.length; i++) {
      let id = ai_crashes[i].id.slice(0, ai_crashes[i].id.indexOf('v'))
      if (id === ai_id) {
        if (ai_crashes[i].step > (pane.data.length - 1)) {
          $('#ai_' + ai_id + '_output_error').val('Fehler bei Schritt ' + ai_crashes[i].step + ' wird jetzt gezeigt, weil es diesen Schritt nicht gibt.\n' + ai_crashes[i].reason)
          $('#ai_' + ai_id + '_output_error').show()
        } else if (ai_crashes[i].step === pane.step) {
          $('#ai_' + ai_id + '_output_error').val(ai_crashes[i].reason)
          $('#ai_' + ai_id + '_output_error').show()
        } else {
          $('#ai_' + ai_id + '_output_error').hide()
        }
      }
    }
  })

  draw(d)

  if (d !== undefined) {
    $.map(d.output, function (value, key) {
      let id = key.slice(0, key.indexOf('v'))
      $('#ai_' + id + '_output').val(value)
    })
  }

  if (pane.is_playing) {
    $('#play_button').addClass('active')
    $('#pause_button').removeClass('active')
  } else {
    $('#play_button').removeClass('active')
    $('#pause_button').addClass('active')
  }
}

function map_sorted (obj, func) {
  let sorted_keys = $.map(obj, (e, i) => i).sort()
  return $.map(sorted_keys, function (key) {
    return func(key, obj[key])
  })
}

let return_val = (k, v) => v

$(document).ready(function () {
  console.log('Streaming game data from', window.location.origin + $('#game_script').attr('stream'))
  let evtSrc = new EventSource(window.location.origin + $('#game_script').attr('stream'))
  NProgress.configure({ trickle: false })
  NProgress.start()

  evtSrc.onerror = function () {
    console.log('SSE Err')
    evtSrc.close()
    NProgress.done()
  }

  evtSrc.addEventListener('state', function(e) {
    d = JSON.parse(e.data)
    NProgress.set(d.progress)
    console.log(d)
    pane.data.push(d)
    //NProgress.set(d.progress)
    $('#step_slider').slider('option', 'max', pane.data.length-1)
    let wonChips = map_sorted(d.wonChips, return_val)
    let chips = map_sorted(d.chips, return_val)
    let calculationPoints = map_sorted(d.calculationPoints, return_val)
    let labels = map_sorted(d.calculationPoints, function(key, value) {
      let id = key.slice(0, key.indexOf('v'))
      return ais[id]
    })
    let d = {}
    d.diff = Math.abs(wonChips[0] - wonChips[1])
    d.ai1_abs = wonChips[0]
    d.ai2_abs = wonChips[1]
    d.ai1_gain = chips[0]
    d.ai2_gain = chips[1]
    d.ai1_tabs = calculationPoints[0]
    d.ai2_tabs = calculationPoints[1]
    d.ai1_td = 0
    d.ai2_td = 0
    if (data.length > 0) {
      d.ai1_td = Math.round((data[data.length - 1].ai1_tabs - calculationPoints[0]) * 100) / 100
      d.ai2_td = Math.round((data[data.length - 1].ai2_tabs - calculationPoints[1]) * 100) / 100
    }

    d.ai1_name = labels[0]
    d.ai2_name = labels[1]

    d.step = pane.data.length
    data.push(d)
    $.map(charts, function (chart) {
      chart.update_chart()
    })
    update()
  })

  // ## in ne generelle lib verschieben
  evtSrc.addEventListener('crash', function (e) {
    console.log(e.data)
    d = JSON.parse(e.data)
    ai_crashes.push(d)
    update()
    NProgress.done()
  })

  // ## in ne generelle lib verschieben
  evtSrc.addEventListener('stream_stopped', function (e) {
    console.log('stream_stopped')
    evtSrc.close()
    NProgress.done()
  })

  // ## in ne generelle lib verschieben
  evtSrc.addEventListener('game_finished', function(e) {
    console.log('game_finished', e.data)
    $('#finished_message').show()
    $('#finished_message').find('a').attr('href', e.data)
    NProgress.done()
  })

  // ## in ne generelle lib verschieben
  evtSrc.addEventListener('qualified', function(e) {
    $('#qualified_message').show()
    NProgress.done()
  })

  // ## in ne generelle lib verschieben
  evtSrc.addEventListener('failed', function(e) {
    $('#failed_message').show()
    NProgress.done()
  })

  // ## in ne generelle lib verschieben
  evtSrc.addEventListener('error', function(e) {
    alert(e.data)
  })

  evtSrc.addEventListener('finished_transmitting', function(e) {
    console.log('finished_transmitting')
    NProgress.done()
  })
})
