window.hello = function (msg = "test") {
    Android.showToast(msg);
}
//window.jsGetVideoIframe();
//window.jsClearTag();
window.jsGetPlayer = function () {
    if (typeof window.jsGetVideoIframe == 'function') {
        const iframe = window.jsGetVideoIframe();
        if (iframe) {
            const video = iframe.contentWindow.document.querySelector('video');
            return { iframe, video };
        } else {
            return 'jsGetPlayer-error-iframe-is-null';
        }
    } else {
        return 'jsGetVideoIframe-func-not-found';
    }
}
window.jsStart = function () {
    if (typeof window.jsClearTag == 'function') {
        //需要由特定播放源注入代码
        window.jsClearTag();
    }
    const player = window.jsGetPlayer();
    if (typeof player == 'string') {
        return JSON.stringify({code: 400, error: `jsStart-${player}`});
    } else {
        //https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement
        player.video.addEventListener('canplay', event => Android.onEvent(event.type));
        player.video.addEventListener('durationchange', event => Android.onEvent(event.type));
        player.video.addEventListener('ended', event => Android.onEvent(event.type));
        player.video.addEventListener('error', event => Android.onEvent(event.type));
        player.video.addEventListener('pause', event => Android.onEvent(event.type));
        player.video.addEventListener('play', event => Android.onEvent(event.type));
        player.video.addEventListener('timeupdate', event => Android.onEvent(event.type));
        player.video.addEventListener('loadedmetadata', event => Android.onEvent(event.type));
        player.iframe.style = 'position:fixed !important;top:0px !important;width:100% !important;'
            + 'height:100% !important;background:rgb(221,221,221);z-index:2147483647 !important;'
        player.video.play();
        return JSON.stringify({code: 200, data: `${player.video.duration}`});
    }
}
window.jsVideoCMD = function (cmd, arg=null) {
    const player = window.jsGetPlayer();
    if (typeof player == 'string') {
        return `video-${cmd}-player-string-null`
    } else {
        switch (cmd) {
            case 'play':
                player.video.play();
                break;
            case 'pause':
                player.video.pause();
                break;
            case 'forward':
                player.video.currentTime += 10;
                break;
            case 'backward':
                player.video.currentTime -= 10;
                break;
            case 'get_current_time':
                return JSON.stringify({code: 200, data: player.video.currentTime});
            case 'get_duration':
                return JSON.stringify({code: 200, data: player.video.duration});
            case 'set_current_time':
                if (Number(arg) > 0) {
                    player.video.currentTime = Number(arg);
                    break;
                } else {
                    return JSON.stringify({code: 400, error: 'jsVideoCMD-arg-required'});
                }
            default:
                return JSON.stringify({code: 400, error: 'jsVideoCMD-unknown-cmd'});
        }
        return JSON.stringify({code: 200});
    }
}
Android.onEvent('inject-script-ready');