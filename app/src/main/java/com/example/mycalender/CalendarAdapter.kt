import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycalender.DateModels.CalendarDay
import com.example.mycalender.R


class CalendarAdapter(private val calendarDays: List<CalendarDay>, private val onDayClicked: (CalendarDay) -> Unit) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = calendarDays[position]
        holder.bind(day, onDayClicked = onDayClicked)
    }

    override fun getItemCount(): Int = calendarDays.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDay: TextView = itemView.findViewById(R.id.text_day)
        private val eventsContainer: ViewGroup = itemView.findViewById(R.id.eventsContainer)

        fun bind(day: CalendarDay, onDayClicked: (CalendarDay) -> Unit) {
            textDay.text = day.dayText
            day.events.forEach { event ->
                val eventView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.event_box, eventsContainer, false) as TextView
                eventView.text = event?.eventName
                eventView.setBackgroundColor(event!!.eventColor)
                eventsContainer.addView(eventView)
            }
            textDay.setBackgroundColor(itemView.context.getColor(R.color.day_bg_color))
            itemView.setOnClickListener {
                if (day.isValidDay) { // Only clickable if it's an actual day
                    onDayClicked(day)
                }
            }
        }
    }
}