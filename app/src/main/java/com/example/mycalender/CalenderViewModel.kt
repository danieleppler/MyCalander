import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycalender.DateModels.Event
import com.example.mycalender.Reposetories.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class CalenderViewModel(

) : ViewModel() {

    private val eventRepo: EventRepository = EventRepository.get()
    private val _events = MutableStateFlow<List<Event?>>(emptyList())
    val events: StateFlow<List<Event?>> = _events.asStateFlow()

    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    val currentMonth: StateFlow<Int> = _currentMonth.asStateFlow()

    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            eventRepo.getEvents().collect { eventList ->
                _events.value = eventList
            }
        }
    }

    fun goToPreviousMonth() {
        if (_currentMonth.value == 0) {
            _currentMonth.value = 11
            _currentYear.value -= 1
        } else {
            _currentMonth.value -= 1
        }
    }

    fun goToNextMonth() {
        if (_currentMonth.value == 11) {
            _currentMonth.value = 0
            _currentYear.value += 1
        } else {
            _currentMonth.value += 1
        }
    }

    fun setMonth(month: Int, year: Int) {
        _currentMonth.value = month
        _currentYear.value = year
    }
}