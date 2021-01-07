package com.arieleo.webtview;

public class TVduboku {
    public static final String JsTest = "(function() {\n" +
            "    Android.showToast(\"test\");\n" +
            "    return new Date().toString();\n" +
            "})()";
    public static final String JsVideoStart = "(function() {\n" +
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
            "       return 'video'\n" +
            "    } else {\n" +
            "       return 'video-null'\n" +
            "    }" +
            "})()";
    public static final String JsForward = "(function() {\n" +
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
    public static final String JsBackward = "(function() {\n" +
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
    public static final String JsPlay = "(function() {\n" +
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
    public static final String JsPause = "(function() {\n" +
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
    public static final String JsPlayOrStop = "(function() {\n" +
            "    const iframe = document.querySelector('iframe[src=\"/static/player/videojs.html\"]')\n" +
            "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
            "    if(!iframe) return 'iframe-null';\n" +
            "    const video = iframe.contentWindow.document.querySelector('video');\n" +
            "    console.log('video ' + (video?video.tagName:'null'));\n" +
            "    if(video) {" +
            "       video.scrollIntoView();" +
            "       if(video.paused) {" +
            "           video.play();" +
            "           return 'play'\n" +
            "       } else {" +
            "           video.pause();" +
            "           return 'pause'\n" +
            "       }" +
            "    } else {\n" +
            "       return 'video-null'\n" +
            "    }" +
            "})()";
    public static final String JsLoadMeta = "(function() {\n" +
            "    const meta = [];\n" +
            "    document.querySelectorAll('div.myui-panel.myui-panel-bg > div.myui-panel-box').forEach(dom => {\n" +
            "        const title = dom.querySelector('div.myui-panel__head > h3.title');\n" +
            "        const more = dom.querySelector('div.myui-panel__head > a.more');\n" +
            "        const items = [];\n" +
            "        dom.querySelectorAll('ul.myui-vodlist > li > div > a').forEach(a => {\n" +
            "            items.push({ \n" +
            "                title: a.title, \n" +
            "                href: a.href, \n" +
            "                image: a.getAttribute('data-original'),\n" +
            "                tag: a.querySelector('span.tag') ? a.querySelector('span.tag').innerText : '',\n" +
            "                pic_text: a.querySelector('span.pic-text').innerText\n" +
            "            });\n" +
            "        });\n" +
            "        meta.push({\n" +
            "            category: title ? title.innerText: '',\n" +
            "            link: more ? more.href: '',\n" +
            "            items: items\n" +
            "        });\n" +
            "    });\n" +
            "    return JSON.stringify(meta);\n" +
            "})()";

    public static final String JsLoadEpisodes = "(function() {\n" +
            "const items = [];\n" +
            "document.querySelector('#playlist1 ul').querySelectorAll('li a.btn').forEach(node => {\n" +
            "    items.push({title: node.innerText, link: node.href});\n" +
            "});\n" +
            "return JSON.stringify(items);" +
            "})()";
}
