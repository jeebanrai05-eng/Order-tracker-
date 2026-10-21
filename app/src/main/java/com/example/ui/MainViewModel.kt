package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AuthRepository
import com.example.data.AuthUiState
import com.example.data.OrderDayEntity
import com.example.data.OrderRepository
import com.example.data.PreferencesManager
import com.example.data.UserSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.data.update.DownloadStatus
import com.example.data.update.UpdateCheckResult
import com.example.data.update.UpdateManager
import com.example.data.update.UpdateManifest
import java.util.Calendar

enum class NavigationTab {
  ORDERS,
  CHART,
  SETTINGS
}

data class MonthlySummary(
  val totalOrders: Int = 0,
  val totalTips: Double = 0.0,
  val totalGas: Double = 0.0,
  val netProfit: Double = 0.0,
  val targetOrders: Int = 400,
  val completedOrders: Int = 0,
  val remainingOrders: Int = 400,
  val targetProgress: Float = 0f,
  val daysHittingGoal: Int = 0,
  val totalDaysWithData: Int = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  private val orderRepository = OrderRepository(database.orderDao())
  private val preferencesManager = PreferencesManager(application)
  private val authRepository = AuthRepository()

  val settings: StateFlow<UserSettings> = preferencesManager.settingsFlow
  val authState: StateFlow<AuthUiState> = authRepository.authState

  private val _currentTab = MutableStateFlow(NavigationTab.ORDERS)
  val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

  // Default to October 2026 as shown in screenshot, or calendar
  private val _selectedYear = MutableStateFlow(2026)
  val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

  private val _selectedMonth = MutableStateFlow(10) // October
  val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

  private val _selectedMetric = MutableStateFlow("Daily Orders")
  val selectedMetric: StateFlow<String> = _selectedMetric.asStateFlow()

  private val _snackbarMessage = MutableSharedFlow<String>()
  val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

  private val _isSyncing = MutableStateFlow(false)
  val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

  private val _editingDay = MutableStateFlow<Int?>(null)
  val editingDay: StateFlow<Int?> = _editingDay.asStateFlow()

  private val _authError = MutableStateFlow<String?>(null)
  val authError: StateFlow<String?> = _authError.asStateFlow()

  fun clearAuthError() {
    _authError.value = null
  }

  fun setEditingDay(day: Int?) {
    _editingDay.value = day
  }

  val updateManager = UpdateManager(application)

  private val _updateCheckResult = MutableStateFlow<UpdateCheckResult?>(null)
  val updateCheckResult: StateFlow<UpdateCheckResult?> = _updateCheckResult.asStateFlow()

  private val _downloadStatus = MutableStateFlow<DownloadStatus>(DownloadStatus.Idle)
  val downloadStatus: StateFlow<DownloadStatus> = _downloadStatus.asStateFlow()

  private val _isCheckingUpdate = MutableStateFlow(false)
  val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

  private val _showUpdateCenterModal = MutableStateFlow(false)
  val showUpdateCenterModal: StateFlow<Boolean> = _showUpdateCenterModal.asStateFlow()

  fun openUpdateCenter() {
    _showUpdateCenterModal.value = true
    checkForUpdates()
  }

  fun dismissUpdateCenter() {
    _showUpdateCenterModal.value = false
    _downloadStatus.value = DownloadStatus.Idle
  }

  init {
    viewModelScope.launch {
      kotlinx.coroutines.delay(2500)
      try {
        val result = updateManager.checkUpdate()
        if (result is UpdateCheckResult.UpdateAvailable) {
          _updateCheckResult.value = result
        }
      } catch (_: Exception) {}
    }
  }

  // Flow of orders for the selected year and month
  val ordersList: StateFlow<List<OrderDayEntity>> = combine(_selectedYear, _selectedMonth) { year, month ->
    Pair(year, month)
  }.flatMapLatest { (year, month) ->
    orderRepository.getOrdersForMonth(year, month)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Map of dayOfMonth -> OrderDayEntity
  val ordersMap: StateFlow<Map<Int, OrderDayEntity>> = ordersList.combine(_selectedMonth) { list, _ ->
    list.associateBy { it.day }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

  // Summary computations
  val monthlySummary: StateFlow<MonthlySummary> = combine(ordersList, settings) { orders, userSettings ->
    val totalOrders = orders.sumOf { it.orders }
    val totalTips = orders.sumOf { it.tip }
    val totalGas = orders.sumOf { it.gas }

    val normalTarget = if (userSettings.targetOrders > 0) userSettings.targetOrders else 400
    val normalOrders = totalOrders.coerceAtMost(normalTarget)
    val extraOrders = (totalOrders - normalTarget).coerceAtLeast(0)

    val commission = (normalOrders * userSettings.normalComm) + (extraOrders * userSettings.extraComm)
    val netProfit = userSettings.baseSalary + commission + totalTips - totalGas

    val remaining = (normalTarget - totalOrders).coerceAtLeast(0)
    val progress = (totalOrders.toFloat() / normalTarget.toFloat()).coerceIn(0f, 1f)

    val daysWithData = orders.count { it.orders > 0 || it.tip > 0.0 || it.gas > 0.0 }
    val daysHitGoal = orders.count {
      val dayEarnings = (it.orders * userSettings.normalComm) + it.tip - it.gas
      userSettings.dailyEarningsGoal > 0 && dayEarnings >= userSettings.dailyEarningsGoal
    }

    MonthlySummary(
      totalOrders = totalOrders,
      totalTips = totalTips,
      totalGas = totalGas,
      netProfit = netProfit,
      targetOrders = normalTarget,
      completedOrders = totalOrders,
      remainingOrders = remaining,
      targetProgress = progress,
      daysHittingGoal = daysHitGoal,
      totalDaysWithData = daysWithData
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthlySummary())

  fun setTab(tab: NavigationTab) {
    _currentTab.value = tab
  }

  fun setMetric(metric: String) {
    _selectedMetric.value = metric
  }

  fun nextMonth() {
    val month = _selectedMonth.value
    val year = _selectedYear.value
    if (month == 12) {
      _selectedMonth.value = 1
      _selectedYear.value = year + 1
    } else {
      _selectedMonth.value = month + 1
    }
  }

  fun previousMonth() {
    val month = _selectedMonth.value
    val year = _selectedYear.value
    if (month == 1) {
      _selectedMonth.value = 12
      _selectedYear.value = year - 1
    } else {
      _selectedMonth.value = month - 1
    }
  }

  fun setYearAndMonth(year: Int, month: Int) {
    _selectedYear.value = year
    _selectedMonth.value = month
  }

  fun saveDayRecord(day: Int, orders: Int, tip: Double, gas: Double, notes: String = "") {
    viewModelScope.launch {
      val year = _selectedYear.value
      val month = _selectedMonth.value
      val id = String.format("%04d-%02d-%02d", year, month, day)
      val entity = OrderDayEntity(
        id = id,
        year = year,
        month = month,
        day = day,
        orders = orders,
        tip = tip,
        gas = gas,
        notes = notes,
        updatedAt = System.currentTimeMillis()
      )
      orderRepository.saveOrderDay(entity)
    }
  }

  fun updateUserSettings(settings: UserSettings) {
    preferencesManager.updateAll(settings)
  }

  fun updateCurrency(currency: String) {
    preferencesManager.updateCurrency(currency)
  }

  fun updateBaseSalary(salary: Double) {
    preferencesManager.updateBaseSalary(salary)
  }

  fun updateTargetOrders(target: Int) {
    preferencesManager.updateTargetOrders(target)
  }

  fun updateNormalComm(rate: Double) {
    preferencesManager.updateNormalComm(rate)
  }

  fun updateExtraComm(rate: Double) {
    preferencesManager.updateExtraComm(rate)
  }

  fun setDarkMode(enabled: Boolean) {
    preferencesManager.updateDarkMode(enabled)
  }

  fun login(email: String, pass: String) {
    viewModelScope.launch {
      _isSyncing.value = true
      _authError.value = null
      val res = authRepository.login(email, pass)
      _isSyncing.value = false
      res.onSuccess {
        _authError.value = null
        _snackbarMessage.emit("Welcome back, $it!")
        syncCloudData()
      }.onFailure {
        val err = it.localizedMessage ?: "Login failed"
        _authError.value = err
        _snackbarMessage.emit(err)
      }
    }
  }

  fun signup(email: String, pass: String) {
    viewModelScope.launch {
      _isSyncing.value = true
      _authError.value = null
      val res = authRepository.signup(email, pass)
      _isSyncing.value = false
      res.onSuccess {
        _authError.value = null
        _snackbarMessage.emit("Account created successfully!")
        syncCloudData()
      }.onFailure {
        val err = it.localizedMessage ?: "Sign up failed"
        _authError.value = err
        _snackbarMessage.emit(err)
      }
    }
  }

  fun signInWithGoogle(context: android.content.Context) {
    viewModelScope.launch {
      _isSyncing.value = true
      _authError.value = null
      val res = authRepository.signInWithGoogle(context)
      _isSyncing.value = false
      res.onSuccess {
        _authError.value = null
        _snackbarMessage.emit("Welcome, $it! Signed in with Google")
        syncCloudData()
      }.onFailure {
        val err = it.localizedMessage ?: "Google sign in cancelled or unavailable"
        _authError.value = err
        _snackbarMessage.emit(err)
      }
    }
  }

  fun quickDriverLogin() {
    viewModelScope.launch {
      _isSyncing.value = true
      val defaultEmail = "driver@ordercom.com"
      val defaultPass = "Driver@1234"
      val loginRes = authRepository.login(defaultEmail, defaultPass)
      if (loginRes.isSuccess) {
        _isSyncing.value = false
        _snackbarMessage.emit("Welcome! Signed in as $defaultEmail")
        syncCloudData()
      } else {
        val signupRes = authRepository.signup(defaultEmail, defaultPass)
        if (signupRes.isSuccess) {
          _isSyncing.value = false
          _snackbarMessage.emit("Driver Account created & synced as $defaultEmail!")
          syncCloudData()
        } else {
          authRepository.setLocalDriverSession(defaultEmail, "Delivery Driver")
          _isSyncing.value = false
          _snackbarMessage.emit("Logged in as $defaultEmail (Driver Session Ready)")
        }
      }
    }
  }

  fun forgotPassword(email: String) {
    viewModelScope.launch {
      if (email.isBlank()) {
        _snackbarMessage.emit("Please enter your email address")
        return@launch
      }
      val res = authRepository.sendPasswordReset(email)
      res.onSuccess {
        _snackbarMessage.emit("Password reset link sent to $email")
      }.onFailure {
        _snackbarMessage.emit(it.localizedMessage ?: "Failed to send reset link")
      }
    }
  }

  fun logout() {
    authRepository.logout()
    viewModelScope.launch {
      _snackbarMessage.emit("Logged out successfully")
    }
  }

  fun syncCloudData() {
    viewModelScope.launch {
      _isSyncing.value = true
      val res = orderRepository.syncWithCloud()
      val remoteSettings = orderRepository.fetchSettingsFromCloud()
      if (remoteSettings != null) {
        preferencesManager.updateAll(remoteSettings)
      }
      _isSyncing.value = false
      res.onSuccess { count ->
        _snackbarMessage.emit("Cloud backup synced ($count records & settings)")
      }.onFailure {
        _snackbarMessage.emit("Sync notice: ${it.localizedMessage}")
      }
    }
  }

  fun getDaysInSelectedMonth(): Int {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, _selectedYear.value)
    cal.set(Calendar.MONTH, _selectedMonth.value - 1)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
  }

  fun updateTheme(theme: String) {
    preferencesManager.updateTheme(theme)
  }

  fun updateAutoMidnightReminder(enabled: Boolean) {
    preferencesManager.updateAutoMidnightReminder(enabled)
  }

  fun saveFullConfiguration(newSettings: UserSettings) {
    preferencesManager.updateAll(newSettings)
    viewModelScope.launch {
      val cloudRes = orderRepository.saveSettingsToCloud(newSettings)
      if (cloudRes.isSuccess && authRepository.currentUser != null) {
        _snackbarMessage.emit("Configuration & Daily Goal saved to Cloud Backup!")
      } else {
        _snackbarMessage.emit("Configuration saved locally!")
      }
    }
  }

  fun clearAllData() {
    viewModelScope.launch {
      orderRepository.clearAllData()
      _snackbarMessage.emit("All order data has been cleared.")
    }
  }

  suspend fun exportAllDataCsv(): String {
    val curr = settings.value.currency
    return orderRepository.exportAllDataCsv(curr)
  }

  suspend fun exportReport(): String {
    val curr = settings.value.currency
    return orderRepository.exportMonthReportText(_selectedYear.value, _selectedMonth.value, curr)
  }

  fun checkForUpdates(customUrl: String? = null) {
    viewModelScope.launch {
      _isCheckingUpdate.value = true
      val url = if (!customUrl.isNullOrBlank()) customUrl.trim() else UpdateManager.DEFAULT_MANIFEST_URL
      val result = updateManager.checkUpdate(url)
      _isCheckingUpdate.value = false
      _updateCheckResult.value = result
      when (result) {
        is UpdateCheckResult.UpToDate -> {
          _snackbarMessage.emit("App is up to date (${result.currentVersionName})")
        }
        is UpdateCheckResult.Error -> {
          _snackbarMessage.emit(result.message)
        }
        is UpdateCheckResult.UpdateAvailable -> {
          _snackbarMessage.emit("New update found: ${result.manifest.versionName}!")
        }
      }
    }
  }

  fun startDownloadUpdate(manifest: UpdateManifest) {
    viewModelScope.launch {
      updateManager.downloadAndVerify(manifest).collect { status ->
        _downloadStatus.value = status
      }
    }
  }

  fun installDownloadedApk(apkUri: android.net.Uri) {
    updateManager.promptInstallApk(apkUri)
  }

  fun dismissUpdateDialog() {
    _updateCheckResult.value = null
    _downloadStatus.value = DownloadStatus.Idle
    _showUpdateCenterModal.value = false
  }
}
