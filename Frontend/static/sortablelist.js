function sortable_list(id) {

	var options = {
		valueNames: []
	};

	$("#"+id+" > thead > tr > th").each(function () {
		$.each($(this).attr("class").split(/\s+/), function(index, item){
			if (item.indexOf('data-') === 0) {
				options.valueNames.push(item);
			}
		});
	});

	var list = new List(id, options);

	var s_type = null;
	var s_dir = "ascending";

	function toggle(type) {
		if (s_type === type) {
			if (s_dir === "ascending") {
				s_dir = "descending";
			} else {
				s_dir = "ascending";
			}
		} else {
			s_dir = "ascending";
		}
		s_type = type;

		if (s_dir === "ascending") {
			list.sort(s_type, {order: "asc"});
		} else {
			list.sort(s_type, {order: "desc"});
		}

		$("#"+id+" > thead > tr > th").each(function () {
			var e = $(this);
			e.removeClass("sorted");
			e.removeClass("ascending");
			e.removeClass("descending");
			if (e.hasClass(s_type)) {
				e.addClass("sorted");
				e.addClass(s_dir);
			}
		});
	}

	$("#"+id+" > thead > tr > th").on("click", function () {
		$.each($(this).attr("class").split(/\s+/), function(index, item){
			if (item.indexOf('data-') === 0) {
				toggle(item);
			}
		});
	});

	return {
		toggle: toggle,
	};
}