package org.versaillesumc.firetv

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, HomeFragment())
            .commit()
    }
}

data class MenuItem(val title: String, val detail: String, val url: String? = null)

class HomeFragment : BrowseSupportFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title = "Versailles UMC"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = Color.rgb(14, 29, 52)
        searchAffordanceColor = Color.rgb(69, 143, 201)

        val rows = ArrayObjectAdapter(ListRowPresenter())
        val sections = listOf(
            "Live Worship" to listOf(
                MenuItem("Watch Live", "Join worship wherever you are"),
                MenuItem("Next Worship", "Upcoming service schedule")
            ),
            "Latest Streams" to listOf(
                MenuItem("Traditional Worship", "Most recent service"),
                MenuItem("Contemporary Worship", "Most recent service")
            ),
            "Sermon Series" to listOf(
                MenuItem("Current Series", "Watch the latest messages")
            ),
            "Partner With Us" to listOf(
                MenuItem("Diaper Bank", "Serving families"),
                MenuItem("Blessing Box", "Sharing essentials"),
                MenuItem("Backpack Buddies", "Supporting students"),
                MenuItem("Third Monday Meal", "Gathering around the table")
            ),
            "Connect With Us" to listOf(
                MenuItem("Church Center", "Stay connected"),
                MenuItem("Giving", "Support local ministry"),
                MenuItem("About Us", "Get to know Versailles UMC")
            )
        )
        sections.forEachIndexed { index, section ->
            val cards = ArrayObjectAdapter(MinistryCardPresenter())
            section.second.forEach(cards::add)
            rows.add(ListRow(HeaderItem(index.toLong(), section.first), cards))
        }
        adapter = rows
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _: RowPresenter.ViewHolder, _: Row ->
            if (item is MenuItem) {
                if (!item.url.isNullOrBlank()) {
                    startActivity(Intent(requireContext(), PlayerActivity::class.java).putExtra("url", item.url))
                } else {
                    Toast.makeText(requireContext(), "${item.title}: coming soon", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

class MinistryCardPresenter : Presenter() {
    private fun dp(parent: ViewGroup, value: Int): Int =
        (value * parent.resources.displayMetrics.density).toInt()

    private fun background(focused: Boolean): GradientDrawable =
        GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            if (focused) intArrayOf(Color.rgb(37, 91, 143), Color.rgb(20, 55, 100))
            else intArrayOf(Color.rgb(29, 55, 89), Color.rgb(15, 34, 62))
        ).apply {
            cornerRadius = 16f
            setStroke(if (focused) 4 else 1,
                if (focused) Color.rgb(170, 217, 255) else Color.rgb(60, 87, 118))
        }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val container = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = ViewGroup.LayoutParams(dp(parent, 310), dp(parent, 170))
            setPadding(dp(parent, 22), dp(parent, 18), dp(parent, 22), dp(parent, 18))
            isFocusable = true
            isFocusableInTouchMode = true
            background = background(false)
            elevation = dp(parent, 3).toFloat()
        }
        val heading = TextView(parent.context).apply {
            textSize = 22f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            maxLines = 2
        }
        val subtitle = TextView(parent.context).apply {
            textSize = 15f
            setTextColor(Color.rgb(196, 211, 230))
            maxLines = 2
            setPadding(0, dp(parent, 9), 0, 0)
        }
        container.addView(heading)
        container.addView(subtitle)
        container.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            view.background = background(hasFocus)
            view.animate().scaleX(if (hasFocus) 1.06f else 1f)
                .scaleY(if (hasFocus) 1.06f else 1f)
                .setDuration(160).start()
            view.elevation = dp(parent, if (hasFocus) 12 else 3).toFloat()
        }
        return ViewHolder(container)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val card = item as MenuItem
        val layout = viewHolder.view as LinearLayout
        (layout.getChildAt(0) as TextView).text = card.title
        (layout.getChildAt(1) as TextView).text = card.detail
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) = Unit
}
