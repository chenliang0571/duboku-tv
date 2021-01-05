package com.arieleo.webtview;

public class TVduboku {
    public static final String JsTest = "(function() {\n" +
            "    Android.showToast(\"test\");\n" +
            "    return new Date().toString();\n" +
            "})()";
    public static final String JsPlayOrStop = "(function() {\n" +
            "    const big = document.querySelector('#playerCnt > button.vjs-big-play-button');\n" +
            "    if(big) {\n" +
            "        big.click();\n" +
            "    } else {\n" +
            "        document.querySelector('#playerCnt > div.vjs-control-bar > button.vjs-play-control.vjs-control').click();\n" +
            "    }\n" +
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
}
