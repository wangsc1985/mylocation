package com.wangsc.mylocation.utils

import android.content.Context
import android.util.Log
import com.wangsc.mylocation.utils._OkHttpUtil.getRequest
import com.wangsc.mylocation.utils._OkHttpUtil.postRequestByJson
import com.wangsc.mylocation.callbacks.CloudCallback
import com.wangsc.mylocation.callbacks.HttpCallback
import com.wangsc.mylocation.models.DataContext
import com.wangsc.mylocation.models.DateTime
import com.wangsc.mylocation.models.PostArgument
import com.wangsc.mylocation.models.User
import com.wangsc.mylocation.utils._OkHttpUtil.postRequestByJsonStr
import org.json.JSONArray
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList

object _CloudUtils {
    private var newMsgCount = 0
    private val env = "weizhao-8g92xew132e140ba"
    private val appid = "wxb7d4c0ace04910ed";
    private val secret = "cc7cc71d838f5b2045a17de11e5a90a1";

    @JvmStatic
    fun getToken(context: Context): String {
        val dc = DataContext(context)
        val setting = dc.getSetting("token_exprires")
        if (setting != null) {
            val exprires = setting.long
            if (System.currentTimeMillis() > exprires) {
                /**
                 * token过期
                 */
                return loadNewTokenFromHttp((context))
            } else {
                /**
                 * token仍有效
                 */
                e("本地token有效期：${DateTime(exprires).toLongDateTimeString()}")
                return dc.getSetting("token").string
            }
        } else {
            return loadNewTokenFromHttp(context)
        }
    }

    private fun loadNewTokenFromHttp(context: Context): String {
        var token = ""

        val a = System.currentTimeMillis()
        val latch = CountDownLatch(1)
        getRequest("https://sahacloudmanager.azurewebsites.net/home/token/${appid}/${secret}", HttpCallback { html ->
            try {
//                e(html)
                val data = html.split(":")
                if (data.size == 2) {
                    token = data[0]
                    e(data[1].toDouble())
                    e(data[1].toDouble().toLong())
                    val exprires = data[1].toDouble().toLong()

                    // 将新获取的token及exprires存入本地数据库
                    val dc = DataContext(context)
                    dc.editSetting("token", token)
                    dc.editSetting("token_exprires", exprires)


                    val b = System.currentTimeMillis()
                    e("从微软获取token：$token , 有效期：${DateTime(exprires).toLongDateTimeString()} 用时：${b - a}")
                }
            } catch (e: java.lang.Exception) {
                e(e.message!!)
            } finally {
                latch.countDown()
            }
        })
        latch.await()
        return token
    }

    @JvmStatic
    fun saveSetting(context: Context, pwd: String?, name: String?, value: Any, callback: CloudCallback?) {
        newMsgCount = 0

        val accessToken = getToken(context)
        // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
        val url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=$accessToken&env=$env&name=saveSetting"
        val args: MutableList<PostArgument> = ArrayList()
        args.add(PostArgument("name", name))
        args.add(PostArgument("value", value.toString()))
        postRequestByJson(url, args, HttpCallback { html ->
            try {
//                e(html)
                val resp_data: Any = _JsonUtils.getValueByKey(html, "resp_data")
                if (_JsonUtils.isContainsKey(resp_data, "success")) {
                    val code = _JsonUtils.getValueByKey(resp_data.toString(), "code").toInt()
                    when (code) {
                        0 -> callback?.excute(0, "修改完毕")
                        1 -> callback?.excute(1, "添加成功")
                    }
                } else if (_JsonUtils.isContainsKey(resp_data, "msg")) {
                    callback?.excute(-1, "访问码错误")
                }
            } catch (e: Exception) {
                callback?.excute(-2, e.message)
            }
        })

    }

    @JvmStatic
    fun addLocation(context: Context, phone: String, latitude: Double, longitude: Double, address: String, callback: CloudCallback?) {
        newMsgCount = 0

        val accessToken = getToken(context)
        // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
        val url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=$accessToken&env=$env&name=addLocation"
//                    e(url)
        val args: MutableList<PostArgument> = ArrayList()
        args.add(PostArgument("phone", phone))
        args.add(PostArgument("date", System.currentTimeMillis()))
        args.add(PostArgument("latitude", latitude))
        args.add(PostArgument("longitude", longitude))
        args.add(PostArgument("address", address))

        postRequestByJson(url, args, HttpCallback { html ->
            try {
                callback?.excute(0, html)
            } catch (e: Exception) {
                callback?.excute(-2, e.message)
            }
        })

    }

    fun getLocations(context: Context, teamkey: String, callback: CloudCallback?) {
        newMsgCount = 0

        val a = System.currentTimeMillis()
        val accessToken = getToken(context)

        // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
        val url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=$accessToken&env=$env&name=getLocation"
        val args: MutableList<PostArgument> = ArrayList()
        args.add(PostArgument("teamkey", teamkey))

        postRequestByJson(url, args, HttpCallback { html ->
            try {
                val users: MutableList<User> = ArrayList()
                val resp_data: Any = _JsonUtils.getValueByKey(html, "resp_data")
//                e(resp_data.toString())
                val jsonArray = JSONArray(resp_data.toString())
                for (i in 0..jsonArray.length() - 1) {
                    val jsonObject = jsonArray.getString(i)
                    val name = _JsonUtils.getValueByKey(jsonObject, "name").toString()
                    val avatar = _JsonUtils.getValueByKey(jsonObject, "avatar").toString()
                    val sex =  _JsonUtils.getValueByKey(jsonObject, "sex").toInt()
                    val nick = _JsonUtils.getValueByKey(jsonObject, "nick").toString()
                    val address = _JsonUtils.getValueByKey(jsonObject, "address").toString()
                    val phone = _JsonUtils.getValueByKey(jsonObject, "phone").toString()
                    val latitude = _JsonUtils.getValueByKey(jsonObject, "latitude").toDouble()
                    val longitude = _JsonUtils.getValueByKey(jsonObject, "longitude").toDouble()
                    val locationTime = DateTime(_JsonUtils.getValueByKey(jsonObject, "locationTime").toLong())
                    users.add(User(name, nick,sex, avatar, address,phone, latitude, longitude, locationTime))
                }
                callback?.excute(0, users)
            } catch (e: Exception) {
                e(e.message!!)
                callback?.excute(-2, e.message)
            }
            val b = System.currentTimeMillis()
            e("从微信数据库获取位置，时间间隔：${b - a}")
        })
    }

    fun getDownLoadPath(context: Context, fileId: String, callback: CloudCallback?) {
        try {
            val accessToken = getToken(context)
            val url = "https://api.weixin.qq.com/tcb/batchdownloadfile?access_token=$accessToken&env=$env"
            val json = "{\"env\": \"${env}\",\"file_list\":[{\"fileid\":\"${fileId}\", \"max_age\":7200}]}"

            postRequestByJsonStr(url, json, HttpCallback { html ->
                try {
//                    e(html)
                    val file_list: Any = _JsonUtils.getValueByKey(html, "file_list")
//                    e(file_list.toString())
                    val jsonArray = JSONArray(file_list.toString())
//                    e(jsonArray.length())
                    val jsonObject = jsonArray.getString(0)
                    val download_url = _JsonUtils.getValueByKey(jsonObject, "download_url").toString()
//                    e(download_url)
                    callback?.excute(0, download_url)
                } catch (e: Exception) {
                    e(e.message!!)
                    callback?.excute(-2, e.message)
                }
            })
        } catch (e: Exception) {
            e(e.message!!)
            callback?.excute(-2, e.message)
        }
    }


    fun updatePositions(context: Context, name: String?, positoinsJson: String?, callback: CloudCallback?) {
        newMsgCount = 0
        val accessToken = getToken(context)
        // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
        val url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=$accessToken&env=$env&name=updatePositions"
        val args: MutableList<PostArgument> = ArrayList()
        args.add(PostArgument("name", name))
        args.add(PostArgument("positions", positoinsJson))
        postRequestByJson(url, args, HttpCallback { html ->
            try {
                e(html)
                callback?.excute(0, html)
            } catch (e: Exception) {
                callback?.excute(-2, e.message)
            }
        })
    }

    private fun e(data: Any) {
        Log.e("wangsc", data.toString())
    }
}