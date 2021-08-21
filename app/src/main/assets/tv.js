window.hello = function (msg = "test") {
    Android.showToast(msg);
}
//window.jsGetVideoIframe();
//window.jsClearTag();
window.jsGetPlayer = function () {
    if (typeof window.jsGetVideoIframe == 'function') {
        try {
            const iframe = window.jsGetVideoIframe();
            if (iframe) {
                const video = iframe.contentWindow.document.querySelector('video');
                return { iframe, video };
            } else {
                return 'jsGetPlayer-iframe-is-null';
            }
        } catch (error) {
            return 'jsGetPlayer-exception';
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
        return `jsStart-${player}`;
    } else {
        player.iframe.style = 'position:fixed !important;top:0px !important;width:100% !important;'
            + 'height:100% !important;background:rgb(221,221,221);z-index:2147483647 !important;'
        player.video.play();
        return 'jsStart-video-started-' + new Date().toISOString()
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
                return `jsVideoCMD-${cmd}-ok`;
            case 'pause':
                player.video.pause();
                return `jsVideoCMD-${cmd}-ok`;
            case 'forward':
                player.video.currentTime += 10;
                return `jsVideoCMD-${cmd}-ok`;
            case 'backward':
                player.video.currentTime -= 10;
                return `jsVideoCMD-${cmd}-ok`;
            case 'get_current_time':
                return `jsVideoCMD-${cmd}-${player.video.currentTime}`;
            case 'set_current_time':
                if (Number(arg) > 0) {
                    player.video.currentTime = Number(arg);
                    return `jsVideoCMD-${cmd}-${player.video.currentTime}`;
                }
                else {
                    return 'jsVideoCMD-arg-required';
                }
            default:
                return 'jsVideoCMD-unknown-cmd';
        }
    }
}