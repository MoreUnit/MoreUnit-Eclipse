// jQuery plugin based on .load() for use with innerShiv
// http://jdbartlett.github.com/innershiv for more info
// $('selector').loadShiv('example.html selector');
jQuery.fn.loadShiv = function (url, params, callback) {
	var off, selector, self, type;

	if (!this.length || typeof url !== 'string') {
		return this;
	}

	off = url.indexOf(' ');
	if (off >= 0) {
		selector = url.slice(off, url.length);
		url = url.slice(0, off);
	}

	type = 'GET';

	if (params) {
		if (jQuery.isFunction(params)) {
			callback = params;
			params = null;
		} else if (typeof params === 'object') {
			params = jQuery.param(params, jQuery.ajaxSettings.traditional);
			type = 'POST';
		}
	}

	self = this;

	jQuery.ajax({
		url: url,
		type: type,
		dataType: 'html',
		data: params,
		complete: function (res, status) {
			var shivved;

			if (status === 'success' || status === 'notmodified') {
				shivved = jQuery(innerShiv((selector ? '<div>' : '') + res.responseText, false));

				if (selector) {
					shivved = shivved.find(selector);
				}

				self.empty().append(shivved);
			}

			if (callback) {
				self.each(callback, [res.responseText, status, res]);
			}
		}
	});

	return this;
}