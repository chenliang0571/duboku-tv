package com.arieleo.webtview;

public class TvOlevod {
    static String title() {
        return "olevod.com";
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
        "    const ad = document.querySelector('html > iframe');\n" +
        "    if(ad) ad.style.visibility = 'hidden';\n" +
        "    const right = document.querySelector('#play_page div.container div.right_row ');\n" +
        "    if(right) right.style.visibility = 'hidden';\n" +
        "    const iframe = document.querySelector('iframe[src=\\\"/static/player/plyr2.html\\\"]')\n" +
        "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
        "    if(!iframe) return 'iframe-null';\n" +
        "    const video = iframe.contentWindow.document.querySelector('video');\n" +
        "    console.log('video ' + (video?video.tagName:'null'));\n" +
        "    if(video) {\n" +
        "       video.play();\n" +
        "       video.scrollIntoView();\n" +
        "       const container = document.querySelector('#play_page div.container');\n" +
        "       if(container) container.style = 'z-index: 2147483647 !important;width: auto !important; height: auto !important; margin-left:0px !important; margin-right:0px !important';\n" +
        "       const div = document.querySelector('#play_page div.container div.left_row');\n" +
        "       if(div) div.style = 'width: 100% !important; height: auto !important;';\n" +
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
        "    const ad = document.querySelector('html > iframe');\n" +
        "    if(ad) ad.style.visibility = 'hidden';\n" +
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
        "    document.querySelectorAll('div.pannel.search_box > ul.vodlist > li').forEach(dom => {\n" +
        "        const title = dom.querySelector('div.myui-panel__head > h3.title');\n" +
        "        const more = dom.querySelector('div.myui-panel__head > a.more');\n" +
        "        dom.querySelectorAll('ul.myui-vodlist__media > li > div > a').forEach(a => {\n" +
        "            dramas.push({\n" +
        "                title: a.title,\n" +
        "                url: a.href,\n" +
        "                image: host + a.getAttribute('data-original'),\n" +
        "                tag: a.querySelector('span.tag') ? a.querySelector('span.tag').innerText : '',\n" +
        "                picText: a.querySelector('span.pic-text').innerText,\n" +
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
}
