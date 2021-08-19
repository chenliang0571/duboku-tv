window.hello = function(msg="test") {
    Android.showToast(msg);
}
window.hello("added " + new Date().toISOString());