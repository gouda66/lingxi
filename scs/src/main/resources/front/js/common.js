var web_prefix = '/front'

function imgPath(path){
    if (!path) {
        return '/front/images/noImg.png';
    }
    // 如果已经是完整路径或http开头，直接返回
    if (path.startsWith('http') || path.startsWith('/')) {
        return path;
    }
    // 否则通过后端接口下载
    return '/common/download?name=' + path;
}

//将url传参转换为数组
function parseUrl(url) {
    // 找到url中的第一个?号
    var parse = url.substring(url.indexOf("?") + 1),
        params = parse.split("&"),
        len = params.length,
        item = [],
        param = {};

    for (var i = 0; i < len; i++) {
        item = params[i].split("=");
        param[item[0]] = item[1];
    }

    return param;
}

