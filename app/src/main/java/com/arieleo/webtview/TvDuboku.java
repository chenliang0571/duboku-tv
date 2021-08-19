package com.arieleo.webtview;

public class TvDuboku {
    static String title() {
        return "独播库duboku.com";
    }
    static String urlHome() {
        return "https://www.duboku.com/";
    }
    static String urlSearch() {
        return "https://www.duboku.tv/vodsearch/-------------.html?submit=&wd=";
    }
    static String jsStart() {
        return
        "(function() {\n" +
        "    const iframe = document.querySelector('iframe[src=\"/static/player/videojs.html\"]')\n" +
        "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
        "    if(!iframe) return 'iframe-null';\n" +
        "    const video = iframe.contentWindow.document.querySelector('video');\n" +
        "    console.log('video ' + (video?video.tagName:'null'));\n" +
        "    if(video) {" +
        "       video.play();" +
        "       video.scrollIntoView();" +
        //Failed to execute 'requestFullScreen' on 'Element': API can only be initiated by a user gesture.
        //"       if(video.requestFullScreen) video.requestFullscreen();\n" +
        "       if(document.querySelector('a[class=btnskin]')) document.querySelector('a[class=btnskin]').click();" +
        "       const container = document.querySelector('body > div.container');" +
        "       if(container) container.style = 'height: auto !important; padding-left:0px !important; padding-right:0px !important';" +
        "       const div = document.querySelector('body > div.container > div > div');" +
        "       if(div) div.style = 'width: 100% !important; height: auto !important;';" +
        "       if(video.webkitRequestFullScreen) video.webkitRequestFullScreen();\n" +
        "       return 'video-start-' + new Date().toISOString()\n" +
        "    } else {\n" +
        "       return 'video-null'\n" +
        "    }" +
        "})()";
    }
    static String jsPlay() {
        return
        "(function() {\n" +
        "    const iframe = document.querySelector('iframe[src=\"/static/player/videojs.html\"]')\n" +
        "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
        "    if(!iframe) return 'iframe-null';\n" +
        "    const video = iframe.contentWindow.document.querySelector('video');\n" +
        "    console.log('video ' + (video?video.tagName:'null'));\n" +
        "    if(video) {" +
        "       video.play();" +
        "       video.scrollIntoView();" +
        "       return 'play'\n" +
        "    } else {\n" +
        "       return 'video-null'\n" +
        "    }" +
        "})()";
    }
    static String jsPause() {
        return
        "(function() {\n" +
        "    const iframe = document.querySelector('iframe[src=\"/static/player/videojs.html\"]')\n" +
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
        "    const iframe = document.querySelector('iframe[src=\"/static/player/videojs.html\"]')\n" +
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
        "    const iframe = document.querySelector('iframe[src=\"/static/player/videojs.html\"]')\n" +
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
        "    const now = new Date().toISOString();\n" +
        "    document.querySelectorAll('div.myui-panel.myui-panel-bg > div.myui-panel-box').forEach(dom => {\n" +
        "        const title = dom.querySelector('div.myui-panel__head > h3.title');\n" +
        "        const more = dom.querySelector('div.myui-panel__head > a.more');\n" +
        "        dom.querySelectorAll('ul.myui-vodlist > li > div > a').forEach(a => {\n" +
        "            dramas.push({ \n" +
        "                title: a.title, \n" +
        "                url: a.href, \n" +
        "                image: a.getAttribute('data-original'),\n" +
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
    static String jsSearchResults() {
        return
        "(function() {\n" +
        "    const dramas = [];\n" +
        "    const now = new Date().toISOString();\n" +
        "    document.querySelectorAll('div.myui-panel.myui-panel-bg > div.myui-panel-box').forEach(dom => {\n" +
        "        const title = dom.querySelector('div.myui-panel__head > h3.title');\n" +
        "        const more = dom.querySelector('div.myui-panel__head > a.more');\n" +
        "        dom.querySelectorAll('ul.myui-vodlist__media > li > div > a').forEach(a => {\n" +
        "            dramas.push({ \n" +
        "                title: a.title, \n" +
        "                url: a.href, \n" +
        "                image: a.getAttribute('data-original'),\n" +
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
    static String jsLoadEpisodes() {
        return
        "(function() {\n" +
        "    const items = [];\n" +
        "    const now = new Date().toISOString();\n" +
        "    document.querySelector('#playlist1 ul').querySelectorAll('li a.btn').forEach(node => {\n" +
        "        items.push({title: node.innerText, url: node.href, upd: now});\n" +
        "    });\n" +
        "    return JSON.stringify(items);" +
        "})()";
    }
    static String jsSetCurrentTime() {
        return
        "(function(time=Number.NaN) {\n" +
        "    const iframe = document.querySelector('iframe[src=\"/static/player/videojs.html\"]')\n" +
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
        "    const iframe = document.querySelector('iframe[src=\"/static/player/videojs.html\"]')\n" +
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
