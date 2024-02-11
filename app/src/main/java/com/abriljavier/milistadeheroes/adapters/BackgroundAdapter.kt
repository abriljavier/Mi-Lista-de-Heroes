import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.abriljavier.milistadeheroes.R

class BackgroundAdapter(private val context: Context, private val details: List<String>) : BaseAdapter() {

    override fun getCount(): Int = details.size

    override fun getItem(position: Int): Any = details[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.list, parent, false)

        val detail = getItem(position) as String
        val textView = view.findViewById<TextView>(R.id.textViewItem)
        textView.text = detail

        return view
    }
}