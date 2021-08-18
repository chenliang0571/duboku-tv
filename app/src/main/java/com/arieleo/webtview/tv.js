
/**
* https://www.olevod.com/
* jsLoadMeta
*/
(function() {
    const dramas = [];
    const host = window.location.protocol + '//' + window.location.host;
    const now = new Date().toISOString();
    document.querySelectorAll('div.vod_row.tit_up > div.pannel').forEach(dom => {
        const title = dom.querySelector('h2.title');
        const more = dom.querySelector('div.pannel_head > a.text_muted.pull_left');
        dom.querySelectorAll('div.cbox_list li.vodlist_item > a').forEach(a => {
            dramas.push({
                title: a.title,
                url: a.href,
                image: host + a.getAttribute('data-original'),
                tag: a.querySelector('span.tag') ? a.querySelector('span.tag').innerText : '',
                picText: a.querySelector('span.pic_text') ? a.querySelector('span.pic_text').innerText : '',
                category: title ? title.innerText: '',
                moreUrl: more ? more.href: '',
                upd: now,
            });
        });
    });
    return JSON.stringify(dramas);
})();
/**
* https://www.olevod.com/index.php/vod/search.html?wd=%E6%96%97%E7%BD%97&submit=
* jsSearchResults
*/
(function() {
    const dramas = [];
    const host = window.location.protocol + '//' + window.location.host;
    const now = new Date().toISOString();
    document.querySelectorAll('div.pannel').forEach(dom => {
        const title = dom.querySelector('div.pannel_head h3.title');
        const more = '';
        dom.querySelectorAll('div.search_box > ul.vodlist > li > div.searchlist_img > a').forEach(a => {
            dramas.push({
                title: a.title,
                url: a.href,
                image: host + a.getAttribute('data-original'),
                tag: a.querySelector('span.tag') ? a.querySelector('span.tag').innerText : '',
                picText: a.querySelector('span.pic_text') ? a.querySelector('span.pic_text').innerText : '',
                category: title ? title.innerText: '',
                moreUrl: more ? more.href: '',
                upd: now,
            });
        });
    });
    return JSON.stringify(dramas);
})();

/**
* https://www.olevod.com/index.php/vod/detail/id/5098.html
* jsLoadEpisodes
*/
(function() {
    const items = [];
    const now = new Date().toISOString();
    document.querySelector('#playlistbox ul').querySelectorAll('li a').forEach(node => {
        items.push({title: node.innerText, url: node.href, upd: now});
    });
    return JSON.stringify(items);
})();

/**
* https://www.olevod.com/index.php/vod/detail/id/5098.html
* jsStart
*/
(function() {
    document.querySelectorAll('iframe').forEach(node => {
        if(!node.src) node.style.display = 'none';
    });
    document.querySelectorAll('div.OUTBRAIN').forEach(node => node.innerHTML = "");
    const header = document.querySelector('#play_page > div.hot_banner');
    if(header) header.innerHTML = "";
    const foot = document.querySelector('#play_page > div.foot_nav');
    if(foot) foot.innerHTML = "";
    const ad = document.querySelector('#play_page > div.container');
    if(ad) ad.innerHTML = "";
    const right = document.querySelector('#play_page div.container div.right_row ');
    if(right) right.style.display = 'none';
    const boxbg = document.querySelector('#play_page div.play_boxbg');
    if(boxbg) boxbg.style = 'height: 100% !important;';
    const iframe = document.querySelector('iframe[src=\"/static/player/plyr2.html\"]')
    console.log('iframe ' + (iframe?iframe.tagName:'null'));
    if(!iframe) return 'iframe-null';
    const video = iframe.contentWindow.document.querySelector('video');
    console.log('video ' + (video?video.tagName:'null'));
    if(video) {
       video.play();
       video.scrollIntoView();
//       const container = document.querySelector('#play_page div.container');
//       if(container) container.style = 'width: auto !important; height: auto !important; margin-left:5px !important; margin-right:5px !important';
       const div = document.querySelector('#play_page div.container div.left_row');
       if(div) div.style = 'width: 90% !important; height: auto !important;';
       if(video.webkitRequestFullScreen) video.webkitRequestFullScreen();
       return 'video-start-' + new Date().toISOString()
    } else {
       return 'video-null'
    }
})();

/**
* jsPlay
*/
(function() {
    document.querySelectorAll('iframe').forEach(node => {
        if(!node.src) node.style.display = 'none';
    });
    const iframe = document.querySelector('iframe[src=\"/static/player/plyr2.html\"]')
    console.log('iframe ' + (iframe?iframe.tagName:'null'));
    if(!iframe) return 'iframe-null';
    const video = iframe.contentWindow.document.querySelector('video');
    console.log('video ' + (video?video.tagName:'null'));
    if(video) {
       video.play();
       video.scrollIntoView();
       return 'play'
    } else {
       return 'video-null'
    }
})();

/**
* jsSetCurrentTime
*/
(function(time=Number.NaN) {
    const iframe = document.querySelector('iframe[src=\"/static/player/plyr2.html\"]')
    console.log('iframe ' + (iframe?iframe.tagName:'null'));
    if(!iframe) return 'iframe-null';
    const video = iframe.contentWindow.document.querySelector('video');
    console.log('time ' + (video?video.tagName:'null'));
    const currentTime = Number(time);
    if(currentTime > 0 && video) {
       video.currentTime = currentTime;
       return 'time'
    } else {
       return 'time-null'
    }
})();