import { MessageBox, Message  } from 'element-ui';

// main.js中 import './logout.js'
// 其他自定义complete方法的只需要使用import {isLogout} from '../../logout' 引入 isLogout这个方法，然后在complete里边调用一下就行

// 使用该标记来防止多个请求同时弹出box
var keepLogout = false;

$.ajaxSetup({
    xhrFields: {
        withCredentials: true
    },
    beforeSend: function (jqXHR, settings) {
        /*header里加请求头参数*/
        jqXHR.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    },
    complete: function (xhr, status) {
      isLogout(xhr)
    }
});

/**
 * 判断当前是否已经退出登录了
 * @param {XmlHttpRequest} xhr  ajax complete函数的第一个参数 XmlHttpRequest
 */
function isLogout(xhr){
  let res = xhr.responseJSON;
  // console.log("判断是否登录");
  // 如果后端已经退出登录了，会返回当前403，并且response.code=5000
  if (xhr.status === 403 && res.code === 5000 && !keepLogout) {
      keepLogout = true;
      MessageBox.confirm('登录失效，是否重新登录?', '', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          closeOnClickModal: false,
          type: 'warning'
      }).then(() => {
          let win = window;
          while (win != win.top) {
              win = win.top;
          }
          win.location.href = res.data;
      }).catch(() => {
          keepLogout = true;
          Message.warning({
              showClose: true,
              message: '请手动刷新页面获取新数据',
              center: true
            })
            return true;
      });
  }
  return false;
}


export {isLogout}
