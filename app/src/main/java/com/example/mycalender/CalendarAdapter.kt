import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycalender.CalendarDay
import com.example.mycalender.R


class CalendarAdapter(private val calendarDays: List<CalendarDay>) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = calendarDays[position]
        holder.bind(day)
    }

    override fun getItemCount(): Int = calendarDays.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDay: TextView = itemView.findViewById(R.id.text_day)

        fun bind(day: CalendarDay) {
            textDay.text = day.dayText

            // Style headers differently
            when {
                day.isValidDay -> {
                    textDay.setBackgroundColor(itemView.context.getColor(R.color.day_bg_color))
                    textDay.textSize = 16f

                    // Add click listener for valid days
                    itemView.setOnClickListener {
                        if (day.dayText.isNotEmpty()) {
                            // Handle day selection
                            // Add your day selection logic here
                        }
                    }
                }
                else -> {
                    textDay.setBackgroundColor(itemView.context.getColor(R.color.day_bg_color))
                }
            }
        }
    }
}