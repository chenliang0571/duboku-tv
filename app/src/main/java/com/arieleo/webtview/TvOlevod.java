package com.arieleo.webtview;

public class TvOlevod {
    static String title() {
        return "欧乐影院\nolevod.com";
    }
    static String urlHome() {
        return "https://www.olevod.com/";
    }
    static String urlSearch() {
        return "https://www.olevod.com/index.php/vod/search.html?submit=&wd=";
    }
    static String jsStart() {
        return
        "(function() {\n" +
        "    document.querySelectorAll('iframe').forEach(node => {\n" +
        "        if(!node.src) node.style.display = 'none';\n" +
        "    });\n" +
        "    document.querySelectorAll('div.OUTBRAIN').forEach(node => node.innerHTML = \"\");\n" +
        "    const header = document.querySelector('#play_page > div.hot_banner');\n" +
        "    if(header) header.innerHTML = \"\";\n" +
        "    const foot = document.querySelector('#play_page > div.foot_nav');\n" +
        "    if(foot) foot.innerHTML = \"\";\n" +
        "    const ad = document.querySelector('#play_page > div.container');\n" +
        "    if(ad) ad.innerHTML = \"\";\n" +
        "    const right = document.querySelector('#play_page div.container div.right_row ');\n" +
        "    if(right) right.style.display = 'none';\n" +
        "    const boxbg = document.querySelector('#play_page div.play_boxbg');\n" +
        "    if(boxbg) boxbg.style = 'height: 1080px !important;';\n" +
        "    const iframe = document.querySelector('iframe[src=\\\"/static/player/plyr2.html\\\"]')\n" +
        "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
        "    if(!iframe) return 'iframe-null';\n" +
        "    const video = iframe.contentWindow.document.querySelector('video');\n" +
        "    console.log('video ' + (video?video.tagName:'null'));\n" +
        "    if(video) {\n" +
        "       video.play();\n" +
        "       video.scrollIntoView();\n" +
        "       const div = document.querySelector('#play_page div.container div.left_row');\n" +
        "       if(div) div.style = 'width: 85% !important; height: auto !important;';\n" +
        "       if(video.webkitRequestFullScreen) video.webkitRequestFullScreen();\n" +
        "       return 'video-start-' + new Date().toISOString()\n" +
        "    } else {\n" +
        "       return 'video-null'\n" +
        "    }\n" +
        "})()";
    }
    static String jsPlay() {
        return
        "(function() {\n" +
        "    document.querySelectorAll('iframe').forEach(node => {\n" +
        "        if(!node.src) node.style.display = 'none';\n" +
        "    });\n" +
        "    const iframe = document.querySelector('iframe[src=\\\"/static/player/plyr2.html\\\"]')\n" +
        "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
        "    if(!iframe) return 'iframe-null';\n" +
        "    const video = iframe.contentWindow.document.querySelector('video');\n" +
        "    console.log('video ' + (video?video.tagName:'null'));\n" +
        "    if(video) {\n" +
        "       video.play();\n" +
        "       video.scrollIntoView();\n" +
        "       return 'play'\n" +
        "    } else {\n" +
        "       return 'video-null'\n" +
        "    }\n" +
        "})()";
    }
    static String jsPause() {
        return
        "(function() {\n" +
        "    const iframe = document.querySelector('iframe[src=\"/static/player/plyr2.html\"]')\n" +
        "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
        "    if(!iframe) return 'iframe-null';\n" +
        "    const video = iframe.contentWindow.document.querySelector('video');\n" +
        "    console.log('video ' + (video?video.tagName:'null'));\n" +
        "    if(video) {" +
        "       video.pause();" +
        "       video.scrollIntoView();" +
        "       return 'pause'\n" +
        "    } else {\n" +
        "       return 'video-null'\n" +
        "    }" +
        "})()";
    }
    static String jsForward() {
        return
        "(function() {\n" +
        "    const iframe = document.querySelector('iframe[src=\"/static/player/plyr2.html\"]')\n" +
        "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
        "    if(!iframe) return 'iframe-null';\n" +
        "    const video = iframe.contentWindow.document.querySelector('video');\n" +
        "    console.log('video ' + (video?video.tagName:'null'));\n" +
        "    if(video) {" +
        "       video.currentTime += 5;" +
        "       video.scrollIntoView();" +
        "       return 'forward-' + video.currentTime;\n" +
        "    } else {\n" +
        "       return 'video-null'\n" +
        "    }" +
        "})()";
    }
    static String jsBackward() {
        return
        "(function() {\n" +
        "    const iframe = document.querySelector('iframe[src=\"/static/player/plyr2.html\"]')\n" +
        "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
        "    if(!iframe) return 'iframe-null';\n" +
        "    const video = iframe.contentWindow.document.querySelector('video');\n" +
        "    console.log('video ' + (video?video.tagName:'null'));\n" +
        "    if(video) {" +
        "       video.currentTime -= 5;" +
        "       video.scrollIntoView();" +
        "       return 'backward-' + video.currentTime;\n" +
        "    } else {\n" +
        "       return 'video-null'\n" +
        "    }" +
        "})()";
    }
    static String jsLoadMeta() {
        return
        "(function() {\n" +
        "    const dramas = [];\n" +
        "    const host = window.location.protocol + '//' + window.location.host;\n" +
        "    const now = new Date().toISOString();\n" +
        "    document.querySelectorAll('div.vod_row.tit_up > div.pannel').forEach(dom => {\n" +
        "        const title = dom.querySelector('h2.title');\n" +
        "        const more = dom.querySelector('div.pannel_head > a.text_muted.pull_left');\n" +
        "        dom.querySelectorAll('div.cbox_list li.vodlist_item > a').forEach(a => {\n" +
        "            dramas.push({\n" +
        "                title: a.title,\n" +
        "                url: a.href,\n" +
        "                image: host + a.getAttribute('data-original'),\n" +
        "                tag: a.querySelector('span.tag') ? a.querySelector('span.tag').innerText : '',\n" +
        "                picText: a.querySelector('span.pic_text') ? a.querySelector('span.pic_text').innerText : '',\n" +
        "                category: title ? title.innerText: '',\n" +
        "                moreUrl: more ? more.href: '',\n" +
        "                upd: now,\n" +
        "            });\n" +
        "        });\n" +
        "    });\n" +
        "    return JSON.stringify(dramas);\n" +
        "})()";
    }
    static String jsSearchResults() {
        return
        "(function() {\n" +
        "    const dramas = [];\n" +
        "    const host = window.location.protocol + '//' + window.location.host;\n" +
        "    const now = new Date().toISOString();\n" +
        "    document.querySelectorAll('div.pannel').forEach(dom => {\n" +
        "        const title = dom.querySelector('div.pannel_head h3.title');\n" +
        "        const more = '';\n" +
        "        dom.querySelectorAll('div.search_box > ul.vodlist > li > div.searchlist_img > a').forEach(a => {\n" +
        "            dramas.push({\n" +
        "                title: a.title,\n" +
        "                url: a.href,\n" +
        "                image: host + a.getAttribute('data-original'),\n" +
        "                tag: a.querySelector('span.tag') ? a.querySelector('span.tag').innerText : '',\n" +
        "                picText: a.querySelector('span.pic_text') ? a.querySelector('span.pic_text').innerText : '',\n" +
        "                category: title ? title.innerText: '',\n" +
        "                moreUrl: more ? more.href: '',\n" +
        "                upd: now,\n" +
        "            });\n" +
        "        });\n" +
        "    });\n" +
        "    return JSON.stringify(dramas);\n" +
        "})()";
    }
    static String jsLoadEpisodes()  {
        return
        "(function() {\n" +
        "    const items = [];\n" +
        "    const now = new Date().toISOString();\n" +
        "    document.querySelector('#playlistbox ul').querySelectorAll('li a').forEach(node => {\n" +
        "        items.push({title: node.innerText, url: node.href, upd: now});\n" +
        "    });\n" +
        "    return JSON.stringify(items);" +
        "})()";
    }
    static String jsSetCurrentTime() {
        return
        "(function(time=Number.NaN) {\n" +
        "    const iframe = document.querySelector('iframe[src=\\\"/static/player/plyr2.html\\\"]')\n" +
        "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
        "    if(!iframe) return 'iframe-null';\n" +
        "    const video = iframe.contentWindow.document.querySelector('video');\n" +
        "    console.log('time ' + (video?video.tagName:'null'));\n" +
        "    const currentTime = Number(time);\n" +
        "    if(currentTime > 0 && video) {\n" +
        "       video.currentTime = currentTime;\n" +
        "       return 'time'\n" +
        "    } else {\n" +
        "       return 'time-null'\n" +
        "    }\n" +
        "})()";
    }
    static String jsGetCurrentTime() {
        return
        "(function() {\n" +
        "    const iframe = document.querySelector('iframe[src=\\\"/static/player/plyr2.html\\\"]')\n" +
        "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
        "    if(!iframe) return 'iframe-null';\n" +
        "    const video = iframe.contentWindow.document.querySelector('video');\n" +
        "    console.log('time ' + (video?video.tagName:'null'));\n" +
        "    if(video) {\n" +
        "       return 'current-time-' + video.currentTime;\n" +
        "    } else {\n" +
        "       return 'current-time-null'\n" +
        "    }\n" +
        "})()";
    }
}
