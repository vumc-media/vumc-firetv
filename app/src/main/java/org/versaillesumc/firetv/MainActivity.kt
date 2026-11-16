package org.versaillesumc.firetv

import android.content.Intent
import android.app.AlertDialog
import android.widget.ProgressBar
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity

data class TvCard(val title: String, val detail: String)
data class TvSection(val title: String, val cards: List<TvCard>)

class MainActivity : FragmentActivity() {
    private val navy = Color.rgb(12, 25, 45)
    private val side = Color.rgb(15, 33, 57)
    private val muted = Color.rgb(181, 202, 226)
    private lateinit var menu: LinearLayout
    private lateinit var content: ScrollView
    private val menuButtons = mutableListOf<TextView>()
    private val firstCards = mutableListOf<View>()

    private val sections = listOf(
        TvSection("Live Worship", listOf(TvCard("Watch Live", "Live stream and next service countdown"))),
        TvSection("Latest Streams", listOf(TvCard("Traditional Worship", "Most recent service"), TvCard("Contemporary Worship", "Most recent service"))),
        TvSection("Sermon Series", listOf(TvCard("Browse Sermons", "Watch messages from our R2 library"))),
        TvSection("Partner With Us", listOf(TvCard("Diaper Bank", "Serving families"), TvCard("Blessing Box", "Sharing essentials"), TvCard("Backpack Buddies", "Supporting students"), TvCard("Third Monday Meal", "Gathering around the table"))),
        TvSection("Connect With Us", listOf(TvCard("Church Center", "Stay connected"), TvCard("Giving", "Support local ministry"), TvCard("About Us", "Get to know Versailles UMC")))
    )

    private fun dp(value: Int) = (value * resources.displayMetrics.density + 0.5f).toInt()

    private fun shape(color: Int, border: Int = color, radius: Int = 14) =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(2), border)
        }

    private fun label(text: String, size: Float, color: Int = Color.WHITE, bold: Boolean = false) =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(navy)
            isFocusableInTouchMode = true
        }
        setContentView(root)

        menu = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(35), dp(14), dp(18))
            setBackgroundColor(side)
            isFocusable = false
        }
        root.addView(menu, LinearLayout.LayoutParams(dp(188), -1))

        val logo = label("VERSAILLES\nUMC", 22f, Color.WHITE, true)
        menu.addView(logo, LinearLayout.LayoutParams(-1, dp(110)))
        val caption = label("WORSHIP  •  CONNECT  •  SERVE", 10f, muted)
        menu.addView(caption, LinearLayout.LayoutParams(-1, dp(38)))

        val right = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(30), dp(26), dp(28), dp(10))
        }
        root.addView(right, LinearLayout.LayoutParams(0, -1, 1f))
        right.addView(label("VERSAILLES UNITED METHODIST CHURCH", 12f, muted, true))
        right.addView(label("Welcome Home", 29f, Color.WHITE, true),
            LinearLayout.LayoutParams(-1, dp(57)))

        content = ScrollView(this).apply {
            isFillViewport = false
            clipToPadding = false
            isFocusable = false
        }
        right.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))
        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(12), 0, dp(35))
            isFocusable = false
        }
        content.addView(column)

        sections.forEachIndexed { index, section ->
            val button = label(section.title, 16f, muted, false).apply {
                isFocusable = true
                setPadding(dp(12), 0, dp(5), 0)
                background = shape(side)
                setOnFocusChangeListener { v, focused ->
                    background = shape(if (focused) Color.rgb(35, 88, 145) else side,
                        if (focused) Color.rgb(160, 213, 255) else side)
                    setTextColor(if (focused) Color.WHITE else muted)
                }
                setOnClickListener { focusSection(index) }
                setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        firstCards[index].requestFocus()
                        true
                    } else false
                }
            }
            val lp = LinearLayout.LayoutParams(-1, dp(49))
            lp.bottomMargin = dp(12)
            menu.addView(button, lp)
            menuButtons.add(button)

            val heading = label(section.title.uppercase(), 15f, muted, true)
            val headingLp = LinearLayout.LayoutParams(-1, dp(36))
            headingLp.topMargin = dp(if (index == 0) 0 else 17)
            column.addView(heading, headingLp)

            val scroller = HorizontalScrollView(this).apply {
                isHorizontalScrollBarEnabled = false
                clipToPadding = false
                isFocusable = false
            }
            column.addView(scroller, LinearLayout.LayoutParams(-1, dp(164)))
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(dp(6), dp(7), dp(10), dp(10))
                clipChildren = false
                clipToPadding = false
            }
            scroller.addView(row)
            section.cards.forEachIndexed { cardIndex, card ->
                val tile = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(19), dp(12), dp(17), dp(12))
                    background = shape(Color.rgb(26, 52, 87), Color.rgb(48, 80, 119))
                    isFocusable = true
                    elevation = dp(2).toFloat()
                    setOnFocusChangeListener { v, focused ->
                        background = shape(if (focused) Color.rgb(37, 91, 150) else Color.rgb(26, 52, 87),
                            if (focused) Color.rgb(177, 224, 255) else Color.rgb(48, 80, 119))
                        animate().scaleX(if (focused) 1.04f else 1f)
                            .scaleY(if (focused) 1.04f else 1f).setDuration(150).start()
                        elevation = dp(if (focused) 9 else 2).toFloat()
                    }
                    setOnClickListener {
                        openCard(card.title)
                    }
                    setOnKeyListener { _, keyCode, event ->
                        if (event.action == KeyEvent.ACTION_DOWN &&
                            keyCode == KeyEvent.KEYCODE_DPAD_LEFT && cardIndex == 0) {
                            menuButtons[index].requestFocus()
                            true
                        } else false
                    }
                }
                tile.addView(label(card.title, 20f, Color.WHITE, true))
                val detail = label(card.detail, 13f, muted)
                val detailLp = LinearLayout.LayoutParams(-1, -2)
                detailLp.topMargin = dp(9)
                tile.addView(detail, detailLp)
                val tileLp = LinearLayout.LayoutParams(dp(248), dp(140))
                tileLp.rightMargin = dp(24)
                row.addView(tile, tileLp)
                if (cardIndex == 0) firstCards.add(tile)
            }
        }
        menuButtons[0].requestFocus()
    }

    private fun focusSection(index: Int) {
        firstCards[index].requestFocus()
        val target = firstCards[index]
        content.post { content.smoothScrollTo(0, (target.parent.parent as View).top.coerceAtLeast(0)) }
    }


    private fun play(url:String) {
        if(!url.startsWith("https://")) { message("This recording is not available.");return }
        startActivity(Intent(this,PlayerActivity::class.java).putExtra("url",url))
    }
    private fun message(value:String) {
        AlertDialog.Builder(this).setTitle("Versailles UMC").setMessage(value).setPositiveButton("OK",null).show()
    }
    private fun loading(title:String, task:()->Unit) {
        task()
    }
    private fun openCard(title:String) {
        when(title) {
            "Watch Live" -> VumcServices.request({VumcServices.live()}) { r->
                r.onSuccess { state ->
                    if(state.url!=null) play(state.url)
                    else {
                        val next=if(state.nextName.isNotBlank()) "\n\nNext: ${state.nextName}\n${state.nextTime}" else "\n\nNext worship schedule is temporarily unavailable."
                        message("Worship is currently off air."+next)
                    }
                }.onFailure { message("Unable to check the live stream. Please try again.") }
            }
            "Traditional Worship","Contemporary Worship" -> VumcServices.request({VumcServices.latest()}) { r->
                r.onSuccess { videos->
                    val video=videos.firstOrNull { it.title==title }
                    if(video==null) message("The latest recording is temporarily unavailable.") else play(video.url)
                }.onFailure { message("Unable to retrieve the latest services.") }
            }
            "Browse Sermons" -> VumcServices.request({VumcServices.sermons()}) { r->
                r.onSuccess { videos->
                    if(videos.isEmpty()) {message("No sermon recordings are available.");return@onSuccess}
                    val names=videos.take(100).map { it.title }.toTypedArray()
                    AlertDialog.Builder(this).setTitle("Sermon Series • Recordings")
                        .setItems(names) { _,which->play(videos[which].url) }
                        .setNegativeButton("Back",null).show()
                }.onFailure { message("Unable to load the sermon catalog.") }
            }
            "About Us" -> message("Versailles United Methodist Church\n230 Paynes Mill Road, Versailles, KY 40383\n\nA Church Striving to Love Like Jesus\n\nversaillesumc.org")
            else -> message("The ${title} information screen is being connected to the existing Roku artwork.")
        }
    }

    override fun onBackPressed() {
        menuButtons.firstOrNull()?.requestFocus()
    }
}
