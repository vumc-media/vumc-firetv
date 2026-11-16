package org.versaillesumc.firetv

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import org.w3c.dom.Element
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import java.io.ByteArrayInputStream
import java.util.concurrent.Executors

data class WorshipVideo(val title:String,val url:String,val subtitle:String="",val artwork:String="")
data class LiveState(val url:String?,val service:String,val nextName:String,val nextTime:String,val startsAt:String)

object VumcServices {
    private const val STATUS="https://vumc-roku-status.versaillesumcworshipteam.workers.dev/status"
    private const val LATEST="https://vumc-roku-status.versaillesumcworshipteam.workers.dev/latest"
    private const val CATALOG="https://vumc-video-catalog.versaillesumcworshipteam.workers.dev/videos.xml"
    private val executor=Executors.newFixedThreadPool(3)
    private val main=Handler(Looper.getMainLooper())
    private fun get(url:String):String {
        val connection=URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout=9000;connection.readTimeout=12000
        connection.setRequestProperty("Accept","application/json, application/xml, text/xml")
        return try { connection.inputStream.bufferedReader().use { it.readText() } } finally { connection.disconnect() }
    }
    fun <T> request(work:()->T,done:(Result<T>)->Unit) {
        executor.execute {
            val result=runCatching(work)
            main.post { done(result) }
        }
    }
    fun live():LiveState {
        val data=JSONObject(get(STATUS))
        val live=data.optJSONObject("live")
        val t=live?.optJSONObject("traditional")
        val c=live?.optJSONObject("contemporary")
        val active=when {
            t?.optBoolean("live")==true && !t.optString("hls").isNullOrBlank()->t
            c?.optBoolean("live")==true && !c.optString("hls").isNullOrBlank()->c
            else->null
        }
        val next=data.optJSONObject("calendar")?.optJSONObject("nextService")
        return LiveState(active?.optString("hls"),if(active===t)"Traditional Worship" else if(active===c)"Contemporary Worship" else "Off Air",
            next?.optString("name").orEmpty(),next?.optString("displayTime").orEmpty(),next?.optString("startsAt").orEmpty())
    }
    fun latest():List<WorshipVideo> {
        val obj=JSONObject(get(LATEST)).optJSONObject("latestServices") ?: return emptyList()
        return listOf("traditional" to "Traditional Worship","contemporary" to "Contemporary Worship").mapNotNull { (key,title)->
            val item=obj.optJSONObject(key) ?: return@mapNotNull null
            val url=item.optString("hls").ifBlank { item.optString("url") }
            if(!url.startsWith("https://")) null else WorshipVideo(title,url,
                listOf(item.optString("worshipDateDisplay"),item.optString("sermonTitle")).filter { it.isNotBlank() }.joinToString(" • "),
                item.optString("thumbnail"))
        }
    }
    fun sermons():List<WorshipVideo> {
        val xml=get(CATALOG)
        val factory=DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware=true
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true)
        factory.setFeature("http://xml.org/sax/features/external-general-entities",false)
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities",false)
        val doc=factory.newDocumentBuilder().parse(ByteArrayInputStream(xml.toByteArray()))
        val entries=doc.getElementsByTagName("item")
        val result=mutableListOf<WorshipVideo>()
        for(i in 0 until entries.length) {
            val item=entries.item(i) as? Element ?:continue
            val media=item.getElementsByTagNameNS("*","content")
            val url=(media.item(0) as? Element)?.getAttribute("url").orEmpty()
            if(!url.startsWith("https://"))continue
            val title=item.getElementsByTagName("title").item(0)?.textContent.orEmpty()
            val description=item.getElementsByTagName("description").item(0)?.textContent.orEmpty()
            val thumb=(item.getElementsByTagNameNS("*","thumbnail").item(0) as? Element)?.getAttribute("url").orEmpty()
            result.add(WorshipVideo(title,url,description.take(150),thumb))
        }
        return result
    }
}
