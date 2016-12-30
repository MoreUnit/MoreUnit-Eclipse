;(function() {
	'use strict';

	var DEFAULT_CONTENT_ID = 'overview';
	var currentContentId;

	function markLinksAsSelected(content) {
		$('a').removeClass('selected');
		$('a[href="' + content.article + '"]').addClass('selected');
		if (content.section) {
			$('a[href="' + content.section + '"]').addClass('selected');
		}
	}

	function showSectionOrSections(content) {
		if (content.section) {
			// requested section
			$(content.section).show();
		}
		else {
			// sections of the requested article
			$(content.article + ' > section').show();
		}
	}

	function loadContent(requestedContentId) {
		var contentId = requestedContentId || DEFAULT_CONTENT_ID;
		if (contentId == 'doc') {
			contentId = 'doc-gettingstarted';
		}

		// article/section already displayed
		if (currentContentId == contentId) {
			return;
		}

		var newContent = (function() {
			var contentParts = contentId.split('-');
			return {
				article: '#' + contentParts[0],
				section: contentParts.length == 2 ? '#' + contentId : undefined
			};
		})();

		markLinksAsSelected(newContent);

		// first display: hides everything and shows requested article/section without effects
		if (!currentContentId) {
			currentContentId = contentId;
			$('#site-content > article, #site-content > article > section').hide();
			showSectionOrSections(newContent); // surrounding article is still hidden
			$(newContent.article).show(function() {
				$(document).scrollTop(0);
			});
			return;
		}

		// #site-footer is temporarily hidden to avoid a "jump" effect
		// all articles and sections are hidden
		var fadingOut = $('#site-footer, #site-content > article, #site-content > article > section').fadeOut('fast');
		$.when(fadingOut).done(function() {
			currentContentId = contentId;
			showSectionOrSections(newContent); // surrounding article is still hidden
			// the requested article (or the article surrounding the requested section)
			$(newContent.article + ', #site-footer').fadeIn();
		});
	}

	function adaptLinks() {
		$('a').click(function(e) {
			var href = $(this).attr('href');
			if (href === '#' || href.charAt(0) != '#') {
				return true;
			}
			$.history.load(href.substr(1));
			return false;
		});
	}

	function addDocMenu() {
		var additionalDocNav = $('.js-additional-doc-nav').removeClass('hidden');
		var additionalDocLink = additionalDocNav.find('a');

		var additionalDocSummary = additionalDocNav.append('<ul>' + $('#doc-summary').html() + '</ul>').find('ul');
		additionalDocSummary.hide();

		additionalDocLink.click(function(e) {
			e.preventDefault();
			additionalDocSummary.toggle('fast');
		});
	}

	$(document).ready(function() {
		$.history.init(loadContent, {unescape: '/'});
		adaptLinks();
		addDocMenu();
	});
})();
