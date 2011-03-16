MU = {
	currentContent: null,
	
	loadContent: function(contentId) {
		var content = contentId != '' ? contentId : 'home';
		
		var contentParts = content.split('/');
		if (contentParts[1] == '') {
			contentParts.length = 1;
		}
		
		$('a').removeClass('selected');
		$('a[href="content/' + contentParts[0] + '.html"]').addClass('selected');
		$('a[href="#' + contentId + '"]').addClass('selected');
		
		// page already displayed
		if (MU.currentContent && MU.currentContent.article == contentParts[0] && (!contentParts[1] || MU.currentContent.section == contentParts[1])) {
			return;
		}
		
		var loadArticle, loadSection;
		if (contentParts.length == 1) {
			loadArticle = true;
		}
		else if (!MU.currentContent || MU.currentContent.article != contentParts[0]) {
			loadArticle = true;
			loadSection = true;
		}
		else {
			loadSection = true;
		}
		
		var sectionLoadingConfig = {};
		if (loadSection) {
			sectionLoadingConfig = {
				contentPath: content,
				destination: '#section-container',
				newContent: {
					article: contentParts[0],
					section: contentParts[1]
				}
			};
		}
		
		var loadingConfig;
		if (loadArticle) {
			loadingConfig = {
				contentPath: contentParts[0],
				destination: '#article-container',
				newContent: {
					article: contentParts[0]
				}
			}
		}
		else {
			loadingConfig = sectionLoadingConfig;
			loadSection = false;
		}
		
		MU.doLoadContent(loadingConfig, function(success) {
			if (success && loadSection) {
				MU.doLoadContent(sectionLoadingConfig);
			}
		});
	},
	
	doLoadContent: function(cfg, callback) {
		$('#site-footer').fadeOut('fast'); // prevents "jumping" effect
		
		$(cfg.destination).fadeOut('fast', function() {
			var url = 'content/' + cfg.contentPath + '.html'
			var elementToRetrieve = ' .content';
			
			var onLoad = function(response, status, xhr) {
				if (status == 'error') {
					$(cfg.destination).html("Sorry, the requested content could not be loaded...");
				}
				else {
					MU.adaptLinks(cfg.destination);
					$('a[href="' + url + '"]').addClass('selected');
				}
				
				MU.currentContent = cfg.newContent;
				
				$(cfg.destination).fadeIn();
				$('#site-footer').fadeIn();
				
				if (typeof callback == 'function') {
					callback.call(cfg, status != 'error');
				}
			}
			
			if (cfg.contentPath == 'home') {
				var article = $('#home-content').clone().show();
				$(cfg.destination).empty().append(article);
				onLoad(article.html(), 'success');
			}
			else {
				$(cfg.destination).loadShiv(url + elementToRetrieve, onLoad);
			}
		});
	},

	adaptLinks: function(selector) {
		$(selector + ' a').each(function() {
			$(this).attr('href', $(this).attr('href')
					.replace(/^(\.*\/)?index.html$/, '#home') // index.html => #home
					.replace(/^(?:\.*\/)*(?:content\/)?(.*)\.html$/, '#$1')); // ../*.html, content/**/*.html => #**/*
		});
		$(selector + ' a').click(function(e) {
			var href = $(this).attr('href');
			if (href.charAt(0) != '#') {
				return true;
			}
			$.history.load(href.subtr(1));
			return false;
		});
		$(selector + ' img').each(function() {
			$(this).attr('src', $(this).attr('src')
					.replace(/^(?:\.*\/)*img\/(.*)$/, 'img/$1')); // ../img/**/* => img/**/*
		});
	}
};

$(document).ready(function() {
	$.history.init(MU.loadContent, {unescape: '/'});
	MU.adaptLinks('nav');
});