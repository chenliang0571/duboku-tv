package com.arieleo.webtview;

public class TVduboku {
    public static final String JsTest = "(function() {\n" +
            "    Android.showToast(\"test\");\n" +
            "    return new Date().toString();\n" +
            "})()";
    public static final String JsPlayOrStop = "(function() {\n" +
            "    const iframe = document.querySelector('iframe[src=\"/static/player/videojs.html\"]')\n" +
            "    console.log('iframe ' + (iframe?iframe.tagName:'null'));\n" +
            "    if(!iframe) return null;\n" +
            "    const video = iframe.contentWindow.document.querySelector('video');\n" +
            "    console.log('video ' + (video?video.tagName:'null'));\n" +
            "    if(video) {" +
            "       video.play();" +
            "       video.scrollIntoView();" +
            //Failed to execute 'requestFullScreen' on 'Element': API can only be initiated by a user gesture.
//            "       if(video.requestFullScreen) video.requestFullscreen();\n" +
            "       if(document.querySelector('a[class=btnskin]')) document.querySelector('a[class=btnskin]').click();" +
            "       const container = document.querySelector('body > div.container');" +
            "       if(container) container.style = 'height: auto !important; padding-left:0px !important; padding-right:0px !important';" +
            "       const div = document.querySelector('body > div.container > div > div');" +
            "       if(div) div.style = 'width: 100% !important; height: auto !important;';" +
            "       if(video.webkitRequestFullScreen) video.webkitRequestFullScreen();\n" +
            "       return 'video'\n" +
            "    }\n" +
//            "    const big = iframe.contentWindow.document.querySelector('button.vjs-big-play-button');\n" +
//            "    const small = iframe.contentWindow.document.querySelector('div.vjs-control-bar button.vjs-play-control.vjs-control');\n" +
//            "    if(big) {\n" +
//            "        big.click();\n" +
//            "        return 'big'\n" +
//            "    } else if(small){\n" +
//            "        small.click();\n" +
//            "        return 'small'\n" +
//            "    } else {\n" +
//            "        return null\n" +
//            "    }\n" +
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
