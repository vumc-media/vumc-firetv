package org.versaillesumc.firetv

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, HomeFragment()).commit()
    }
}

data class MenuItem(val title: String, val detail: String, val url: String? = null)

class HomeFragment : BrowseSupportFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title = "Versailles United Methodist Church"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = Color.rgb(17, 30, 52)
        val rows = ArrayObjectAdapter(ListRowPresenter())
        val sections = listOf(
            "Live Worship" to listOf(MenuItem("Watch Live", "Cloudflare live feed — configuration required"), MenuItem("Next Worship", "Planning Center schedule — integration pending")),
            "Latest Streams" to listOf(MenuItem("Traditional Worship", "Latest recording — catalog pending"), MenuItem("Contemporary Worship", "Latest recording — catalog pending")),
            "Sermon Series" to listOf(MenuItem("Current Series", "R2 sermon catalog — integration pending")),
            "Partner With Us" to listOf(MenuItem("Diaper Bank", "Community ministry"), MenuItem("Blessing Box", "Community ministry"), MenuItem("Backpack Buddies", "Community ministry"), MenuItem("Third Monday Meal", "Community ministry")),
            "Connect With Us" to listOf(MenuItem("Church Center", "Connect with Versailles UMC"), MenuItem("Giving", "Support the ministry"), MenuItem("About Us", "Learn about our church"))
        )
        sections.forEachIndexed { i, section ->
            val cards = ArrayObjectAdapter(MenuPresenter())
            section.second.forEach { cards.add(it) }
            rows.add(ListRow(HeaderItem(i.toLong(), section.first), cards))
        }
        adapter = rows
        onItemViewClickedListener = androidx.leanback.widget.OnItemViewClickedListener { _, item, _: RowPresenter.ViewHolder, _: Row ->
            if (item is MenuItem) {
                if (item.url != null) startActivity(Intent(requireContext(), PlayerActivity::class.java).putExtra("url", item.url))
                else android.widget.Toast.makeText(requireContext(), "${item.title}: source not configured yet", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class MenuPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(360, 185)
            setBackgroundColor(Color.rgb(29, 48, 77))
            setTextColor(Color.WHITE)
            textSize = 22f
            gravity = Gravity.CENTER
            setPadding(22, 15, 22, 15)
            isFocusable = true
        }
        return ViewHolder(view)
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val card = item as MenuItem
        (viewHolder.view as TextView).text = "${card.title}\n\n${card.detail}"
    }
    override fun onUnbindViewHolder(viewHolder: ViewHolder) = Unit
}
